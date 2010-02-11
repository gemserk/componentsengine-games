package com.gemserk.games.towerofdefense;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.components.MessageHandler;
import com.gemserk.componentsengine.game.Game;
import com.gemserk.componentsengine.input.MonitorFactory;
import com.gemserk.componentsengine.input.SlickMonitorFactory;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.MessageQueueImpl;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.scene.GroovySceneProvider;
import com.gemserk.componentsengine.scene.SceneProvider;
import com.gemserk.componentsengine.templates.CachedScriptProvider;
import com.gemserk.componentsengine.templates.GroovyScriptProvider;
import com.gemserk.componentsengine.templates.GroovyScriptProviderImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

public class TowerOfDefenseGame extends BasicGame {

	protected static final Logger logger = LoggerFactory.getLogger(TowerOfDefenseGame.class);

	public static void main(String[] arguments) {

		try {
			TowerOfDefenseGame game = new TowerOfDefenseGame();

			AppGameContainer app = new AppGameContainer(game);

			// inicializo todos los subsistemas?

			app.setDisplayMode(800, 600, false);
			app.setAlwaysRender(true);
			//app.setMinimumLogicUpdateInterval(10);
			app.setShowFPS(true);

			app.setTargetFrameRate(60);
			app.start();

		} catch (SlickException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public TowerOfDefenseGame() {
		super("Tower of defense");
	}

	private GameContainer gameContainer;
	private MessageQueue messageQueue;

	@Override
	public void init(final GameContainer container) throws SlickException {

		gameContainer = container;

		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Input.class).toInstance(container.getInput());
				bind(Game.class).in(Singleton.class);
				bind(SceneProvider.class).to(GroovySceneProvider.class).in(Singleton.class);
				bind(GameContainer.class).toInstance(container);

				bind(GroovyScriptProvider.class).toInstance(new CachedScriptProvider(new GroovyScriptProviderImpl()));
				bind(MonitorFactory.class).to(SlickMonitorFactory.class).in(Singleton.class);
				bind(MessageHandler.class).to(Game.class).in(Singleton.class);
				bind(MessageQueue.class).to(MessageQueueImpl.class).in(Singleton.class);
			}
		});
		messageQueue = injector.getInstance(MessageQueue.class);
		game = injector.getInstance(Game.class);
		game.loadScene("towerofdefense.scenes.scene1");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setBackground(new Color(0.8f, 0.8f, 1.0f));
		
		SlickRenderMessage message = new SlickRenderMessage(g);
		messageQueue.enqueue(message);
		messageQueue.processMessages();
	}

	private Game game;

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
		UpdateMessage message = new UpdateMessage(delta);
		messageQueue.enqueue(message);
		messageQueue.processMessages();
	}
	
	@Override
	public void keyPressed(int key, char c) {

		super.keyPressed(key, c);

		if (key == Input.KEY_ESCAPE)
			gameContainer.exit();

	}

}
