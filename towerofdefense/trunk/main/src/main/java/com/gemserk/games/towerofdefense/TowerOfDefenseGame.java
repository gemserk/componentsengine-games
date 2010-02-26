package com.gemserk.games.towerofdefense;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TowerOfDefenseGame extends StateBasedGame {

	protected static final Logger logger = LoggerFactory.getLogger(TowerOfDefenseGame.class);

	public static void main(String[] arguments) {

		try {
			TowerOfDefenseGame game = new TowerOfDefenseGame();

			AppGameContainer app = new AppGameContainer(game);

			// inicializo todos los subsistemas?

			app.setDisplayMode(800, 600, false);
			app.setAlwaysRender(true);
			app.setShowFPS(true);

			app.setMinimumLogicUpdateInterval(1);
			// app.setTargetFrameRate(60);

			app.start();

		} catch (SlickException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public TowerOfDefenseGame() {
		super("Tower of defense");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new MenuGameState());
		addState(new PlayingGameState());
	}

}
