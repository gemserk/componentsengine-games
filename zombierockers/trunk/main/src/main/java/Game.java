import groovy.lang.Closure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.gamestates.GemserkGameState;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.utils.EntityDumper;
import com.google.common.collect.Lists;
import com.google.inject.Key;

public class Game extends StateBasedGame {

	Map<String, Object> gameProperties = new HashMap<String, Object>();

	protected static final Logger logger = LoggerFactory.getLogger(Game.class);

	public static void main(String[] arguments) {

		try {
			Game game = new Game();
			game.gameProperties.put("runningFromMain", true);

			AppGameContainer app = new AppGameContainer(game);

			// inicializo todos los subsistemas?

			app.setDisplayMode(800, 600, false);
			app.setAlwaysRender(true);

			app.setMinimumLogicUpdateInterval(1);
			// app.setTargetFrameRate(60);
			app.setForceExit(true);
			app.start();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.exit(0);
		}
	}

	public Game() {
		super("Zombie Rockers");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		container.setVSync(true);
		container.setShowFPS(false);

		GemserkGameState menuState = new GameGameState(0, "zombierockers.scenes.scene");
		addState(menuState);
	}
	
	class GameGameState extends GemserkGameState {

		@Override
		public void onInit() {
			super.onInit();
			images(injector, "assets/images.properties");
			injector.getInstance(BuilderUtils.class).addCustomUtil("components", new Object() {
				public Component closureComponent(String id, Closure closure) {
					return new ComponentFromListOfClosures(id, Lists.newArrayList(closure));
				}
			});

		}

		public GameGameState(int id) {
			super(id);
		}

		public GameGameState(int id, String defaultScene) {
			super(id, defaultScene);
		}
		
		@Override
		public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
			try {
				super.render(container, game, g);
			} catch (Exception e) {
				Entity rootEntity = injector.getInstance(Key.get(Entity.class, Root.class));
				JSONArray jobject = JSONArray.fromObject(new EntityDumper().dumpEntity(rootEntity));
				logger.info(jobject.toString(4));
				throw new RuntimeException(e);
			}
			
		}

		@Override
		public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
			try {
				int minimumTargetFPS = 30;
				int maximumDelta = (int) (1000f / minimumTargetFPS);
				if (delta > maximumDelta)
					delta = maximumDelta;
				super.update(container, game, delta);
			} catch (Exception e) {
				Entity rootEntity = injector.getInstance(Key.get(Entity.class, Root.class));
				JSONArray jobject = JSONArray.fromObject(new EntityDumper().dumpEntity(rootEntity));
				logger.info(jobject.toString(4));
				throw new RuntimeException(e);
			}

		}

	
	}

}
