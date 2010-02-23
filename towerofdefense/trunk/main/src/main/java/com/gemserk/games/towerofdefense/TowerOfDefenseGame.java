package com.gemserk.games.towerofdefense;

import groovy.lang.Closure;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.components.MessageHandler;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.game.Game;
import com.gemserk.componentsengine.input.MonitorFactory;
import com.gemserk.componentsengine.input.SlickMonitorFactory;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.MessageQueueImpl;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.resources.AnimationManager;
import com.gemserk.componentsengine.resources.AnimationManagerImpl;
import com.gemserk.componentsengine.resources.ImageManager;
import com.gemserk.componentsengine.resources.ImageManagerImpl;
import com.gemserk.componentsengine.resources.PropertiesImageLoader;
import com.gemserk.componentsengine.templates.CachedScriptProvider;
import com.gemserk.componentsengine.templates.GroovyScriptProvider;
import com.gemserk.componentsengine.templates.GroovyScriptProviderImpl;
import com.gemserk.componentsengine.templates.GroovyTemplateProvider;
import com.gemserk.componentsengine.templates.TemplateProvider;
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
			app.setShowFPS(true);

			app.setMinimumLogicUpdateInterval(1);
			//app.setTargetFrameRate(60);

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

		final Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Input.class).toInstance(container.getInput());
				bind(Game.class).in(Singleton.class);
				bind(GameContainer.class).toInstance(container);

				bind(GroovyScriptProvider.class).toInstance(new CachedScriptProvider(new GroovyScriptProviderImpl()));
				bind(MonitorFactory.class).to(SlickMonitorFactory.class).in(Singleton.class);
				bind(MessageHandler.class).to(Game.class).in(Singleton.class);
				bind(MessageQueue.class).to(MessageQueueImpl.class).in(Singleton.class);

				bind(BuilderUtils.class).in(Singleton.class);
				bind(GroovyClosureRunner.class).in(Singleton.class);
				bind(Entity.class).annotatedWith(Root.class).toInstance(new Entity("root"));

				bind(ImageManager.class).to(ImageManagerImpl.class).in(Singleton.class);
				bind(AnimationManager.class).to(AnimationManagerImpl.class).in(Singleton.class);

				bind(TemplateProvider.class).toInstance(new GroovyTemplateProvider());

			}
		});

		messageQueue = injector.getInstance(MessageQueue.class);
		game = injector.getInstance(Game.class);
		final BuilderUtils builderUtils = injector.getInstance(BuilderUtils.class);

		builderUtils.addCustomUtil("templateProvider", injector.getInstance(TemplateProvider.class));
		builderUtils.addCustomUtil("game", injector.getInstance(Game.class));

		builderUtils.addCustomUtil("messageBuilderFactory", new Object() {

			public MessageBuilder messageBuilder(String messageId, Closure closure) {
				return new GroovyMessageBuilder(messageId, closure);
			}
		});

		builderUtils.addCustomUtil("genericprovider", new Object() {

			public GenericProvider provide(Closure closure) {
				return new ValueFromClosure(closure);
			}

		});

		images(injector, "assets/images.properties");

		game.loadScene("towerofdefense.scenes.scene1");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setBackground(new Color(0.0f, 0.0f, 0.0f));

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

	public void images(Injector injector, String imagePropertiesFile) {
		PropertiesImageLoader propertiesImageLoader = new PropertiesImageLoader(imagePropertiesFile);
		injector.injectMembers(propertiesImageLoader);
		propertiesImageLoader.load();

	}

}
