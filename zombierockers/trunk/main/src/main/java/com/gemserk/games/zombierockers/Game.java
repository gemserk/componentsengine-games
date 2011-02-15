package com.gemserk.games.zombierockers;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.gemserk.commons.animation.handlers.AnimationHandlerManager;
import com.gemserk.commons.slick.util.ScreenshotGrabber;
import com.gemserk.commons.slick.util.SlickScreenshotGrabber;
import com.gemserk.componentsengine.commons.entities.FpsEntityBuilder;
import com.gemserk.componentsengine.commons.entities.GameStateManagerEntityBuilder;
import com.gemserk.componentsengine.commons.entities.ScreenshotGrabberEntityBuilder;
import com.gemserk.componentsengine.commons.entities.gui.LabelEntityBuilder;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.modules.BasicModule;
import com.gemserk.componentsengine.modules.InitBuilderUtilsBasic;
import com.gemserk.componentsengine.modules.InitDefaultTemplateProvider;
import com.gemserk.componentsengine.modules.InitEntityManager;
import com.gemserk.componentsengine.slick.gamestates.GemserkGameState;
import com.gemserk.componentsengine.slick.modules.InitBuilderUtilsSlick;
import com.gemserk.componentsengine.slick.modules.InitSlickRenderer;
import com.gemserk.componentsengine.slick.modules.SlickModule;
import com.gemserk.componentsengine.slick.modules.SlickSoundSystemModule;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickToSlf4j;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.utils.annotations.BuilderUtils;
import com.gemserk.datastore.Data;
import com.gemserk.datastore.DataStore;
import com.gemserk.datastore.DataStoreJSONInFileImpl;
import com.gemserk.games.zombierockers.entities.BallEntityBuilder;
import com.gemserk.games.zombierockers.entities.BaseEntityBuilder;
import com.gemserk.games.zombierockers.entities.BonusMessageEntityBuilder;
import com.gemserk.games.zombierockers.entities.BulletEntityBuilder;
import com.gemserk.games.zombierockers.entities.ButtonEntityBuilder;
import com.gemserk.games.zombierockers.entities.CannonEntityBuilder;
import com.gemserk.games.zombierockers.entities.CheckboxEntityBuilder;
import com.gemserk.games.zombierockers.entities.CursorEntityBuilder;
import com.gemserk.games.zombierockers.entities.CustomButtonEntityBuilder;
import com.gemserk.games.zombierockers.entities.FadeEffectEntityBuilder;
import com.gemserk.games.zombierockers.entities.LabelButtonEntityBuilder;
import com.gemserk.games.zombierockers.entities.LimboEntityBuilder;
import com.gemserk.games.zombierockers.entities.PathEntityBuilder;
import com.gemserk.games.zombierockers.entities.PlaceableEntityBuilder;
import com.gemserk.games.zombierockers.entities.SegmentEntityBuilder;
import com.gemserk.games.zombierockers.entities.SegmentsManagerEntityBuilder;
import com.gemserk.games.zombierockers.entities.SpawnerEntityBuilder;
import com.gemserk.games.zombierockers.entities.UtilsEntityBuilder;
import com.gemserk.games.zombierockers.entities.WorldEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.EditorEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.EditorGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.EnterScoreGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.EnterScoreScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.HighscoresGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.HighscoresScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.HighscoresTableEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.MenuGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.MenuScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.PausedGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.PausedScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.PlayingGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.ProfileGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.ProfileScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.SceneGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.SettingsGameStateEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.SettingsScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.SplashScreenEntityBuilder;
import com.gemserk.games.zombierockers.gamestates.UploadScoreEntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.ResourcesMonitor;
import com.gemserk.resources.ResourcesMonitorImpl;
import com.gemserk.resources.dataloaders.StaticDataLoader;
import com.gemserk.resources.resourceloaders.ResourceLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;
import com.gemserk.resources.slick.PropertiesAnimationLoader;
import com.gemserk.resources.slick.PropertiesImageLoader;
import com.gemserk.resources.slick.PropertiesSoundLoader;
import com.gemserk.resources.slick.SlickResourcesBuilder;
import com.gemserk.resources.slick.gamestates.LoadingGameState;
import com.gemserk.resources.slick.gamestates.ResourceManagerLoaderProxyImpl;
import com.gemserk.resources.slick.progress.task.EnterNextStateRunnable;
import com.gemserk.resources.util.progress.TaskQueue;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresHttpImpl;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

public class Game extends StateBasedGame {

	protected static final Logger logger = LoggerFactory.getLogger(Game.class);

	/**
	 * to be used in groovy templates for now...
	 */
	public Map<String, Object> getGameProperties() {
		return globalProperties.getProperties();
	}

	GlobalProperties globalProperties = new GlobalProperties();

	static long time;

	public static void main(String[] arguments) {

		try {
			Game game = new Game();

			game.getGameProperties().put("runningFromMain", true);

			AppGameContainer app = new AppGameContainer(game);

			// inicializo todos los subsistemas?

			app.setDisplayMode(800, 600, false);
			app.setAlwaysRender(true);

			app.setFullscreen(false);

			app.setMinimumLogicUpdateInterval(1);

			app.setForceExit(true);
			app.start();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.exit(0);
		}
	}

	public Game() {
		super("Zombie Rockers");

		time = System.currentTimeMillis();

		System.out.println("APPLICATION CONSTRUCTOR");

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			// the context was probably already configured by default configuration
			// rules
			lc.reset();
			// ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader classLoader = Game.class.getClassLoader();
			configurator.doConfigure(classLoader.getResourceAsStream("zombierockers-logback.xml"));
		} catch (JoranException je) {
			je.printStackTrace();
			// StatusPrinter will handle this
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		Map<String, Object> gameProperties = getGameProperties();

		gameProperties.put("screenResolution", new Rectangle(0, 0, 800, 600));
		gameProperties.put("showFps", false);

		gameProperties.put("runningInDebug", System.getProperty("runningInDebug") != null);

		System.out.println(gameProperties);

		Log.setLogSystem(new SlickToSlf4j());

		logger.info("OS: " + System.getProperty("os.name"));
		logger.info("OS-VERSION: " + System.getProperty("os.version"));
		logger.info("OS-ARCH: " + System.getProperty("os.arch"));

	}

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void initStatesList(final GameContainer container) throws SlickException {
		container.setVSync(true);
		container.setShowFPS(false);
		container.setTargetFrameRate(60);

		final TaskQueue taskQueue = new TaskQueue();
		LoadingGameState loadingGameState = new LoadingGameState(1, new Image("assets/images/logo-gemserk-512x116-white.png"), taskQueue);
		addState(loadingGameState);

		taskQueue.add(new Runnable() {

			@Override
			public void run() {
				// getGameProperties().put("scores", );
				File storageFile = new File(System.getProperty("user.home") + "/.gemserk/zombierockers/profiles.data");

				// final Scores scores = new ScoresFileImpl(storageFile);
				final Scores scores = new ScoresHttpImpl("9eba9d1d13f8190d934e3dd0f58f58ca", "http://gemserkscores.appspot.com/");
				getGameProperties().put("scores", scores);
				getGameProperties().put("executor", Executors.newCachedThreadPool());

				final DataStore dataStore = new DataStoreJSONInFileImpl(storageFile);
				Collection<Data> profilesData = dataStore.get(Sets.newHashSet("profile", "selected"));

				Data profile = null;
				if (profilesData.size() != 1) {
					dataStore.remove(Sets.newHashSet("profile", "selected"));
					profile = new Data(Sets.newHashSet("profile", "selected", "guest"), new HashMap<String, Object>() {
						{
							put("name", "guest-" + Math.random());
						}
					});
					dataStore.submit(profile);
					if (logger.isInfoEnabled())
						logger.info("creating new profile " + profile.getValues().get("name"));
				} else {
					profile = profilesData.iterator().next();
					if (logger.isInfoEnabled())
						logger.info("using existing profile " + profile.getValues().get("name"));
				}

				getGameProperties().put("profile", profile);

				final Injector injector = Guice.createInjector(new SlickModule(container, Game.this), // 
						new SlickSoundSystemModule(), // 
						new BasicModule(), //
						new AbstractModule() {
							@Override
							protected void configure() {
								bind(ScreenshotGrabber.class).to(SlickScreenshotGrabber.class).in(Singleton.class);
								bind(GlobalProperties.class).toInstance(globalProperties);
								bind(SlickSvgUtils.class).in(Singleton.class);

								bind(TaskQueue.class).toInstance(taskQueue);

								ResourceManager resourceManager = new ResourceManagerLoaderProxyImpl(new ResourceManagerImpl(), taskQueue);
								requestInjection(resourceManager);
								ResourcesMonitorImpl resourcesMonitor = new ResourcesMonitorImpl(resourceManager);

								bind(ResourceManager.class).toInstance(resourcesMonitor);
								bind(ResourcesMonitor.class).toInstance(resourcesMonitor);

								bind(Scores.class).toInstance(scores);
								bind(DataStore.class).toInstance(dataStore);

								bind(AnimationHandlerManager.class).in(Singleton.class);
							}
						});

				injector.getInstance(InitEntityManager.class).config();
				injector.getInstance(InitBuilderUtilsBasic.class).config();
				injector.getInstance(InitDefaultTemplateProvider.class).config();

				injector.getInstance(InitBuilderUtilsSlick.class).config();
				injector.getInstance(InitSlickRenderer.class).config();

				GemserkGameState gameState = new LimitedDeltaUpdateGameState(0, "zombierockers.scenes.scene");
				injector.injectMembers(gameState);

				ResourceManager resourceManager = injector.getInstance(ResourceManager.class);

				// declare entity templates

				TemplateRegistrator templateRegistrator = injector.getInstance(TemplateRegistrator.class);

				templateRegistrator.with("GameStateManager").register(new GameStateManagerEntityBuilder());

				templateRegistrator.with("zombierockers.entities.ball").register(new BallEntityBuilder());
				templateRegistrator.with("zombierockers.entities.bullet").register(new BulletEntityBuilder());
				templateRegistrator.with("zombierockers.entities.limbo").register(new LimboEntityBuilder());
				templateRegistrator.with("zombierockers.entities.base").register(new BaseEntityBuilder());
				templateRegistrator.with("zombierockers.entities.cannon").register(new CannonEntityBuilder());
				templateRegistrator.with("zombierockers.entities.cursor").register(new CursorEntityBuilder());
				templateRegistrator.with("zombierockers.entities.spawner").register(new SpawnerEntityBuilder());
				templateRegistrator.with("zombierockers.entities.segment").register(new SegmentEntityBuilder());
				templateRegistrator.with("zombierockers.entities.path").register(new PathEntityBuilder());

				templateRegistrator.with("zombierockers.entities.segmentsmanager").register(new SegmentsManagerEntityBuilder());
				templateRegistrator.with("zombierockers.entities.world").register(new WorldEntityBuilder());

				templateRegistrator.with("entities.placeable").register(new PlaceableEntityBuilder());

				templateRegistrator.with("zombierockers.entities.editor").register(new EditorEntityBuilder());

				templateRegistrator.with("zombierockers.scenes.playing").register(new PlayingGameStateEntityBuilder());

				templateRegistrator.with("zombierockers.scenes.scene").register(new SceneGameStateEntityBuilder());

				templateRegistrator.with("zombierockers.screens.splash").register(new SplashScreenEntityBuilder());

				templateRegistrator.with("gamestates.paused").register(new PausedGameStateEntityBuilder());
				templateRegistrator.with("screens.paused").register(new PausedScreenEntityBuilder());

				templateRegistrator.with("gamestates.settings").register(new SettingsGameStateEntityBuilder());
				templateRegistrator.with("screens.settings").register(new SettingsScreenEntityBuilder());

				templateRegistrator.with("gamestates.highscores").register(new HighscoresGameStateEntityBuilder());
				templateRegistrator.with("screens.highscores").register(new HighscoresScreenEntityBuilder());

				templateRegistrator.with("gamestates.menu").register(new MenuGameStateEntityBuilder());
				templateRegistrator.with("screens.menu").register(new MenuScreenEntityBuilder());

				templateRegistrator.with("gamestates.profile").register(new ProfileGameStateEntityBuilder());
				templateRegistrator.with("screens.profile").register(new ProfileScreenEntityBuilder());

				templateRegistrator.with("gamestates.enterscore").register(new EnterScoreGameStateEntityBuilder());
				templateRegistrator.with("screens.enterscore").register(new EnterScoreScreenEntityBuilder());
				templateRegistrator.with("entities.uploadScore").register(new UploadScoreEntityBuilder());

				templateRegistrator.with("zombierockers.scenes.sceneEditor").register(new EditorGameStateEntityBuilder());

				templateRegistrator.with("gemserk.gui.label").register(new LabelEntityBuilder());
				templateRegistrator.with("gemserk.gui.button").register(new ButtonEntityBuilder());

				templateRegistrator.with("gemserk.gui.labelbutton").register(new LabelButtonEntityBuilder());

				templateRegistrator.with("zombierockers.gui.button").register(new CustomButtonEntityBuilder());
				templateRegistrator.with("zombierockers.gui.checkbox").register(new CheckboxEntityBuilder());

				templateRegistrator.with("zombierockers.gui.bonusmessage").register(new BonusMessageEntityBuilder());

				templateRegistrator.with("zombierockers.gui.highscorestable").register(new HighscoresTableEntityBuilder());

				templateRegistrator.with("effects.fade").register(new FadeEffectEntityBuilder());

				templateRegistrator.with("commons.entities.screenshotGrabber").register(new ScreenshotGrabberEntityBuilder());
				templateRegistrator.with("commons.entities.fps").register(new FpsEntityBuilder());
				templateRegistrator.with("commons.entities.utils").register(new UtilsEntityBuilder());

				ResourcesDeclaration resourcesDeclaration = injector.getInstance(ResourcesDeclaration.class);
				resourcesDeclaration.init();
				
				getGameProperties().put("screenshot", new ResourceLoaderImpl<Image>(new StaticDataLoader<Image>(getScreenShotImage())).load());
				
				injector.getInstance(InitCustomBuilderUtils.class).config();

				taskQueue.add(new Runnable() {
					@Override
					public void run() {
						System.out.println("AFTER LOADING RESOURCES - " + (System.currentTimeMillis() - time));
					}
				});

				taskQueue.add(new EnterNextStateRunnable(container, Game.this, gameState));

				// System.out.println("AFTER INITIALIZING - " + (System.currentTimeMillis() - time));

			}

			Image getScreenShotImage() {
				try {
					return new Image(800, 600);
				} catch (SlickException e) {
					throw new RuntimeException(e);
				}
			}

		});

		System.out.println("AFTER INITIALIZING - " + (System.currentTimeMillis() - time));

	}

	@SuppressWarnings("unchecked")
	public static class InitCustomBuilderUtils {

		@Inject
		@BuilderUtils
		Map<String, Object> builderUtils;

		@Inject
		ResourceManager resourceManager;

		public void config() {
			if (logger.isDebugEnabled())
				logger.debug("Registering custom builder utils.");
			builderUtils.put("svg", new SlickSvgUtils());
			builderUtils.put("resourceManager", resourceManager);
		}

	}

	@SuppressWarnings("unchecked")
	public static class ResourcesDeclaration {

		@Inject
		ResourceManager resourceManager;

		@Inject
		PropertiesAnimationLoader propertiesAnimationLoader;

		@Inject
		PropertiesImageLoader propertiesImageLoader;

		@Inject
		PropertiesSoundLoader propertiesSoundLoader;

		public void init() {
			propertiesImageLoader.load("assets/images.properties");
			propertiesAnimationLoader.load("assets/animations.properties");
			propertiesSoundLoader.load("assets/sounds.properties");

			new SlickResourcesBuilder(resourceManager) {
				{
					resource("FontFps", cached(loader(trueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18)))));
					resource("FontTitle", cached(loader(unicodeFont("assets/fonts/dszombiecry.ttf", "assets/fonts/gui_title.hiero"))));
					resource("FontDialogMessage", cached(loader(unicodeFont("assets/fonts/dszombiecry.ttf", "assets/fonts/gui_button.hiero"))));

					resource("FontMessage", cached(loader(unicodeFont("assets/fonts/dszombiecry.ttf", "assets/fonts/gui_message.hiero"))));

					resource("FontScores", cached(loader(trueTypeFont(classpath("assets/fonts/dszombiecry.ttf"), java.awt.Font.PLAIN, 24))));

					resource("FontPlayingLabel", cached(loader(trueTypeFont(classpath("assets/fonts/Mugnuts.ttf"), java.awt.Font.PLAIN, 24))));

					ResourceLoader bonusResourceLoader = cached(loader(angelCodeFont(classpath("assets/fonts/bonusmessage.fnt"), classpath("assets/fonts/bonusmessage.png"))));

					resource("FontPointsLabel", bonusResourceLoader);
					resource("FontBonusMessage", bonusResourceLoader);

					resource("BackgroundMusic", cached(loader(music("assets/musics/music.ogg"))));
					resource("PlayMusic", cached(loader(music("assets/musics/game.ogg"))));
				}
			};
		}

	}

}
