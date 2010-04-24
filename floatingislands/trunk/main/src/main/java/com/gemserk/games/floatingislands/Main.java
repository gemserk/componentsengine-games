package com.gemserk.games.floatingislands;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.gamestates.GemserkGameState;

public class Main extends StateBasedGame {

	Map<String, Object> gameProperties = new HashMap<String, Object>();

	protected static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] arguments) {

		try {
			Main game = new Main();
			game.gameProperties.put("runningFromMain", true);

			AppGameContainer app = new AppGameContainer(game);

			// inicializo todos los subsistemas?

			app.setDisplayMode(640, 480, false);
			app.setAlwaysRender(true);
			app.setShowFPS(true);

			app.setMinimumLogicUpdateInterval(1);
			// app.setTargetFrameRate(60);

			app.start();

		} catch (SlickException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Main() {
		super("Floating Islands - Ludum Dare 17");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
//		if (container instanceof AppletGameContainer.Container) {
			container.setVSync(true);
//		}
		GemserkGameState menuState = new GameGameState(0, "floatingislands.scenes.game");
		addState(menuState);
	}

	class GameGameState extends GemserkGameState {

		@Override
		public void onInit() {
			super.onInit();
			images(injector, "assets/images.properties");
			sounds("assets/sounds.properties");
		}

		public GameGameState(int id) {
			super(id);
		}

		public GameGameState(int id, String defaultScene) {
			super(id, defaultScene);
		}

		@Override
		public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
			int minimumTargetFPS = 30;
			int maximumDelta = (int) (1000f / minimumTargetFPS);
			if (delta > maximumDelta)
				delta = maximumDelta;
			super.update(container, game, delta);
		}
	}

}
