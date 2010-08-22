package com.gemserk.games.grapplinghookus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.slick.util.ScreenshotGrabber;
import com.gemserk.commons.slick.util.SlickScreenshotGrabber;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.gamestates.GemserkGameState;
import com.gemserk.componentsengine.utils.EntityDumper;
import com.gemserk.componentsengine.utils.SlickToSlf4j;
import com.gemserk.datastore.DataStore;
import com.gemserk.datastore.DataStoreJSONInFileImpl;
import com.google.inject.Key;

public class Game extends StateBasedGame {

	Map<String, Object> gameProperties = new HashMap<String, Object>();

	protected static final Logger logger = LoggerFactory.getLogger(Game.class);

	public static void main(String[] arguments) {

		try {
			Game game = new Game();
			game.gameProperties.put("runningFromMain", true);

			AppGameContainer app = new AppGameContainer(game);

			app.setDisplayMode(640, 480, false);
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
		super("Grappling Hookus - Ludum Dare 18");
		gameProperties.put("runningInDebug", System.getProperty("runningInDebug") != null);
		System.out.println(gameProperties);
		Log.setLogSystem(new SlickToSlf4j());

		logger.info("OS: " + System.getProperty("os.name"));
		logger.info("OS-VERSION: " + System.getProperty("os.version"));
		logger.info("OS-ARCH: " + System.getProperty("os.arch"));
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		container.setVSync(false);
		container.setShowFPS(true);
		GemserkGameState menuState = new GameGameState(0, "grapplinghookus.scenes.scene");
		addState(menuState);

		gameProperties.put("screenshot", new Image(640, 480));
	}

	class GameGameState extends GemserkGameState {

		@Override
		public void onInit() {
			super.onInit();
			images("assets/images.properties");
			animations("assets/animations.properties");

			BuilderUtils builderUtils = injector.getInstance(BuilderUtils.class);
			builderUtils.addCustomUtil("enemyFactory", new EnemyFactory(injector, builderUtils));

			ScreenshotGrabber screenshotGrabber = new SlickScreenshotGrabber();
			injector.injectMembers(screenshotGrabber);
			builderUtils.addCustomUtil("screenshotGrabber", screenshotGrabber);

			File storageFile = new File(System.getProperty("user.home") + "/.gemserk/grapplinghookus/storage.data");
			DataStore dataStore = new DataStoreJSONInFileImpl(storageFile);

			gameProperties.put("dataStore", dataStore);
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
				if (container.getInput().isKeyPressed(Input.KEY_BACK))
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