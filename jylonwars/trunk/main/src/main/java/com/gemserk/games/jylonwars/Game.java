package com.gemserk.games.jylonwars;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.AppletGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.gamestates.GemserkGameState;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.ScoresHttpImpl;

public class Game extends StateBasedGame {

	Map<String,Object> gameProperties = new HashMap<String, Object>();
	
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

			app.start();
			
		} catch (SlickException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Game() {
		super("JylonWars");
	}

	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		container.setShowFPS(false);
		if(container instanceof AppletGameContainer.Container){
			container.setVSync(true);
		}
		GemserkGameState gamestate = new GameGameState(0,"jylonwars.scenes.scene");
		addState(gamestate);
		
		//gameProperties.put("dataStore", new DataStoreInMemoryImpl());
//		File storageFile = new File(System.getProperty("user.home") + "/.gemserk/jylonwars/storage.data");
//		gameProperties.put("dataStore", new DataStoreJSONInFileImpl(storageFile));

//		File scoresFile = new File(System.getProperty("user.home") + "/.gemserk/jylonwars/scores.data");
//		gameProperties.put("scores", new ScoresFileImpl(scoresFile));
		
		gameProperties.put("scores", new ScoresHttpImpl("90eb28f982882fb5def25d61c9420be9", "http://gemserkscores.appspot.com/", new ScoreSerializerJSONImpl()));
		
		gameProperties.put("executor", Executors.newCachedThreadPool());
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
