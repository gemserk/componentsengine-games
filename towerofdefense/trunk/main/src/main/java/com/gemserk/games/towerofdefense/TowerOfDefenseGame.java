package com.gemserk.games.towerofdefense;


import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.AppletGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TowerOfDefenseGame extends StateBasedGame {

	Map<String,Object> gameProperties = new HashMap<String, Object>();
	
	protected static final Logger logger = LoggerFactory.getLogger(TowerOfDefenseGame.class);

	public static void main(String[] arguments) {

		try {
			TowerOfDefenseGame game = new TowerOfDefenseGame();
			game.gameProperties.put("runningFromMain", true);

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
		//if(container instanceof AppletGameContainer.Container){
			container.setVSync(true);
		//}
		GemserkGameState menuState = new GemserkGameState(0,"towerofdefense.scenes.menu");
		addState(menuState);
		GemserkGameState inGameState = new GemserkGameState(1);
		addState(inGameState);
		GemserkGameState sceneSelection = new GemserkGameState(2,"towerofdefense.scenes.chooseScene");
		addState(sceneSelection);
	}

}
