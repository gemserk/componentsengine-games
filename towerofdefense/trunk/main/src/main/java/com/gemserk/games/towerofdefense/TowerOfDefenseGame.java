package com.gemserk.games.towerofdefense;


import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.gamestates.GemserkGameState;

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
		GemserkGameState menuState = new TowerOfDefenseGameState(0,"towerofdefense.scenes.menu");
		addState(menuState);
		GemserkGameState inGameState = new TowerOfDefenseGameState(1);
		addState(inGameState);
		GemserkGameState sceneSelection = new TowerOfDefenseGameState(2,"towerofdefense.scenes.chooseScene");
		addState(sceneSelection);
	}
	
	class TowerOfDefenseGameState extends GemserkGameState {

		@Override
		public void onInit() {
			super.onInit();
			images(injector, "assets/images.properties");
			sounds("assets/sounds.properties");
		}
		
		
		public TowerOfDefenseGameState(int id) {
			super(id);
		}
		
		public TowerOfDefenseGameState(int id, String defaultScene) {
			super(id,defaultScene);
		}
		
		@Override
		public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
			int minimumTargetFPS = 30;
			int maximumDelta = (int) (1000f/minimumTargetFPS);
			if(delta > maximumDelta)
				delta = maximumDelta;
			super.update(container, game, delta);
		}
	}

}
