import groovy.lang.Closure;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.gamestates.GemserkGameState;
import com.gemserk.componentsengine.input.InputMappingComponent;
import com.gemserk.componentsengine.utils.EntityDumper;
import com.gemserk.componentsengine.utils.SlickToSlf4j;
import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;

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
		gameProperties.put("runningInDebug", System.getProperty("runningInDebug")!=null);
		System.out.println(gameProperties);
		Log.setLogSystem(new SlickToSlf4j());

		logger.info("OS: " + System.getProperty("os.name"));
		logger.info("OS-VERSION: " + System.getProperty("os.version"));
		logger.info("OS-ARCH: " + System.getProperty("os.arch"));
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		//container.setVSync(true);
		container.setShowFPS(false);

		GemserkGameState menuState = new GameGameState(0, "zombierockers.scenes.scene");
		addState(menuState);
	}

	class GameGameState extends GemserkGameState {

		@Override
		public void onInit() {
			super.onInit();

			images("assets/images.properties");
			animations("assets/animations.properties");

			BuilderUtils builderUtils = injector.getInstance(BuilderUtils.class);
			builderUtils.addCustomUtil("components", new Object() {
				public Component closureComponent(String id, Closure closure) {
					return new ComponentFromListOfClosures(id, Lists.newArrayList(closure));
				}
			});

			builderUtils.addCustomUtil("svg", new Object() {

				public List<Vector2f> loadPoints(String file, String pathName) throws URISyntaxException {
					ArrayList<Vector2f> points = new ArrayList<Vector2f>();
					URI fileUri = Thread.currentThread().getContextClassLoader().getResource(file).toURI();
					SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(fileUri);
					SVGElement element = diagram.getElement(pathName);
					List vector = element.getPath(null);
					com.kitfox.svg.Path pathSVG = (com.kitfox.svg.Path) vector.get(1);
					Shape shape = pathSVG.getShape();
					PathIterator pathIterator = shape.getPathIterator(null, 0.001d);
					float[] coords = new float[2];

					while (!pathIterator.isDone()) {
						pathIterator.currentSegment(coords);
						points.add(new Vector2f(coords[0], coords[1]));
						pathIterator.next();
					}

					return points;
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
				if(container.getInput().isKeyPressed(Input.KEY_BACK))
					container.exit();
				
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
