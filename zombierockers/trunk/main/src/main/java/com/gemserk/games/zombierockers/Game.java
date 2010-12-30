package com.gemserk.games.zombierockers;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.slick.util.ScreenshotGrabber;
import com.gemserk.commons.slick.util.SlickScreenshotGrabber;
import com.gemserk.componentsengine.commons.entities.FpsEntityBuilder;
import com.gemserk.componentsengine.commons.entities.GameStateManagerEntityBuilder;
import com.gemserk.componentsengine.commons.entities.ScreenshotGrabberEntityBuilder;
import com.gemserk.componentsengine.commons.entities.gui.LabelEntityBuilder;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
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
import com.gemserk.componentsengine.templates.RegistrableTemplateProvider;
import com.gemserk.componentsengine.utils.EntityDumper;
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
import com.gemserk.games.zombierockers.gamestates.EnterScoreScreenEntityBuilder.UploadScoreEntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.dataloaders.StaticDataLoader;
import com.gemserk.resources.datasources.ClassPathDataSource;
import com.gemserk.resources.monitor.FileMonitorResourceHelper;
import com.gemserk.resources.monitor.FileMonitorResourceHelperNullImpl;
import com.gemserk.resources.monitor.FilesMonitor;
import com.gemserk.resources.monitor.FilesMonitorImpl;
import com.gemserk.resources.resourceloaders.CachedResourceLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;
import com.gemserk.resources.slick.PropertiesAnimationLoader;
import com.gemserk.resources.slick.PropertiesImageLoader;
import com.gemserk.resources.slick.PropertiesSoundLoader;
import com.gemserk.resources.slick.dataloaders.SlickAngelCodeFontLoader;
import com.gemserk.resources.slick.dataloaders.SlickMusicLoader;
import com.gemserk.resources.slick.dataloaders.SlickTrueTypeFontLoader;
import com.gemserk.resources.slick.dataloaders.SlickUnicodeFontLoader;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresHttpImpl;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
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

	public static void main(String[] arguments) {

		try {
			Game game = new Game();

			game.getGameProperties().put("runningFromMain", true);
			game.getGameProperties().put("screenResolution", new Rectangle(0, 0, 800, 600));

			AppGameContainer app = new AppGameContainer(game);

			// inicializo todos los subsistemas?

			app.setDisplayMode(800, 600, false);
			app.setAlwaysRender(true);

			app.setFullscreen(false);

			app.setMinimumLogicUpdateInterval(1);

			app.setShowFPS(false);
			app.setVSync(true);

			game.getGameProperties().put("showFps", false);
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
		Map<String, Object> gameProperties = getGameProperties();
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
	public void initStatesList(GameContainer container) throws SlickException {
		// container.setVSync(true);
		container.setShowFPS(false);

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

		Injector injector = Guice.createInjector(new SlickModule(container, this), // 
				new SlickSoundSystemModule(), // 
				new BasicModule(), //
				new AbstractModule() {
					@Override
					protected void configure() {
						bind(ScreenshotGrabber.class).to(SlickScreenshotGrabber.class).in(Singleton.class);
						bind(GlobalProperties.class).toInstance(globalProperties);
						bind(SlickSvgUtils.class).in(Singleton.class);

						bind(ResourceManager.class).to(ResourceManagerImpl.class).in(Singleton.class);
						bind(FilesMonitor.class).to(FilesMonitorImpl.class).in(Singleton.class);
						bind(FileMonitorResourceHelper.class).to(FileMonitorResourceHelperNullImpl.class).in(Singleton.class);

						bind(Scores.class).toInstance(scores);
						bind(DataStore.class).toInstance(dataStore);
					}
				});

		injector.getInstance(InitEntityManager.class).config();
		injector.getInstance(InitBuilderUtilsBasic.class).config();
		injector.getInstance(InitDefaultTemplateProvider.class).config();

		{
			// register each java template provdier to the template provider manager...
			RegistrableTemplateProvider registrableTemplateProvider = injector.getInstance(RegistrableTemplateProvider.class);
			registrableTemplateProvider.add("GameStateManager", javaEntityTemplateProvider.get().with(new GameStateManagerEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.entities.ball", javaEntityTemplateProvider.get().with(new BallEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.bullet", javaEntityTemplateProvider.get().with(new BulletEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.limbo", javaEntityTemplateProvider.get().with(new LimboEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.base", javaEntityTemplateProvider.get().with(new BaseEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.cannon", javaEntityTemplateProvider.get().with(new CannonEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.cursor", javaEntityTemplateProvider.get().with(new CursorEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.spawner", javaEntityTemplateProvider.get().with(new SpawnerEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.segment", javaEntityTemplateProvider.get().with(new SegmentEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.path", javaEntityTemplateProvider.get().with(new PathEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.entities.segmentsmanager", javaEntityTemplateProvider.get().with(new SegmentsManagerEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.entities.world", javaEntityTemplateProvider.get().with(new WorldEntityBuilder()));

			registrableTemplateProvider.add("entities.placeable", javaEntityTemplateProvider.get().with(new PlaceableEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.entities.editor", javaEntityTemplateProvider.get().with(new EditorEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.scenes.playing", javaEntityTemplateProvider.get().with(new PlayingGameStateEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.scenes.scene", javaEntityTemplateProvider.get().with(new SceneGameStateEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.screens.splash", javaEntityTemplateProvider.get().with(new SplashScreenEntityBuilder()));

			registrableTemplateProvider.add("gamestates.paused", javaEntityTemplateProvider.get().with(new PausedGameStateEntityBuilder()));
			registrableTemplateProvider.add("screens.paused", javaEntityTemplateProvider.get().with(new PausedScreenEntityBuilder()));

			registrableTemplateProvider.add("gamestates.settings", javaEntityTemplateProvider.get().with(new SettingsGameStateEntityBuilder()));
			registrableTemplateProvider.add("screens.settings", javaEntityTemplateProvider.get().with(new SettingsScreenEntityBuilder()));

			registrableTemplateProvider.add("gamestates.highscores", javaEntityTemplateProvider.get().with(new HighscoresGameStateEntityBuilder()));
			registrableTemplateProvider.add("screens.highscores", javaEntityTemplateProvider.get().with(new HighscoresScreenEntityBuilder()));

			registrableTemplateProvider.add("gamestates.menu", javaEntityTemplateProvider.get().with(new MenuGameStateEntityBuilder()));
			registrableTemplateProvider.add("screens.menu", javaEntityTemplateProvider.get().with(new MenuScreenEntityBuilder()));

			registrableTemplateProvider.add("gamestates.profile", javaEntityTemplateProvider.get().with(new ProfileGameStateEntityBuilder()));
			registrableTemplateProvider.add("screens.profile", javaEntityTemplateProvider.get().with(new ProfileScreenEntityBuilder()));

			registrableTemplateProvider.add("gamestates.enterscore", javaEntityTemplateProvider.get().with(new EnterScoreGameStateEntityBuilder()));
			registrableTemplateProvider.add("screens.enterscore", javaEntityTemplateProvider.get().with(new EnterScoreScreenEntityBuilder()));
			registrableTemplateProvider.add("entities.uploadScore", javaEntityTemplateProvider.get().with(new UploadScoreEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.scenes.sceneEditor", javaEntityTemplateProvider.get().with(new EditorGameStateEntityBuilder()));

			registrableTemplateProvider.add("gemserk.gui.label", javaEntityTemplateProvider.get().with(new LabelEntityBuilder()));
			registrableTemplateProvider.add("gemserk.gui.button", javaEntityTemplateProvider.get().with(new ButtonEntityBuilder()));

			registrableTemplateProvider.add("gemserk.gui.labelbutton", javaEntityTemplateProvider.get().with(new LabelButtonEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.gui.button", javaEntityTemplateProvider.get().with(new CustomButtonEntityBuilder()));
			registrableTemplateProvider.add("zombierockers.gui.checkbox", javaEntityTemplateProvider.get().with(new CheckboxEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.gui.bonusmessage", javaEntityTemplateProvider.get().with(new BonusMessageEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.gui.highscorestable", javaEntityTemplateProvider.get().with(new HighscoresTableEntityBuilder()));

			registrableTemplateProvider.add("zombierockers.effects.fade", javaEntityTemplateProvider.get().with(new FadeEffectEntityBuilder()));

			registrableTemplateProvider.add("commons.entities.screenshotGrabber", javaEntityTemplateProvider.get().with(new ScreenshotGrabberEntityBuilder()));
			registrableTemplateProvider.add("commons.entities.fps", javaEntityTemplateProvider.get().with(new FpsEntityBuilder()));
			registrableTemplateProvider.add("commons.entities.utils", javaEntityTemplateProvider.get().with(new UtilsEntityBuilder()));
		}

		injector.getInstance(InitBuilderUtilsSlick.class).config();
		injector.getInstance(InitSlickRenderer.class).config();

		GemserkGameState gameState = new GameGameState(0, "zombierockers.scenes.scene");
		injector.injectMembers(gameState);
		addState(gameState);

		getGameProperties().put("screenshot", new ResourceLoaderImpl<Image>(new StaticDataLoader<Image>(new Image(800, 600))).load());

	}

	class GameGameState extends GemserkGameState {

		@Inject
		@BuilderUtils
		Map<String, Object> builderUtils;

		@Inject
		PropertiesAnimationLoader propertiesAnimationLoader;

		@Inject
		PropertiesImageLoader propertiesImageLoader;

		@Inject
		PropertiesSoundLoader propertiesSoundLoader;

		@Inject
		ResourceManager resourceManager;

		@Override
		public void onInit() {
			super.onInit();

			// images("assets/images.properties");
			// animations("assets/animations.properties");

			propertiesImageLoader.load("assets/images.properties");
			propertiesAnimationLoader.load("assets/animations.properties");
			propertiesSoundLoader.load("assets/sounds.properties");

			resourceManager.add("FontFps", new CachedResourceLoader<Font>(new ResourceLoaderImpl<Font>(new SlickTrueTypeFontLoader(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18)))));

			resourceManager.add("FontTitle", new CachedResourceLoader(new ResourceLoaderImpl(new SlickUnicodeFontLoader("assets/fonts/dszombiecry.ttf", "assets/fonts/gui_title.hiero"))));
			resourceManager.add("FontDialogMessage", new CachedResourceLoader(new ResourceLoaderImpl(new SlickUnicodeFontLoader("assets/fonts/dszombiecry.ttf", "assets/fonts/gui_button.hiero"))));

			resourceManager.add("FontMessage", new CachedResourceLoader(new ResourceLoaderImpl(new SlickUnicodeFontLoader("assets/fonts/dszombiecry.ttf", "assets/fonts/gui_message.hiero"))));

			resourceManager.add("FontScores", new CachedResourceLoader<Font>(new ResourceLoaderImpl<Font>(new SlickTrueTypeFontLoader(new ClassPathDataSource("assets/fonts/dszombiecry.ttf"), java.awt.Font.PLAIN, 24))));

			resourceManager.add("FontPlayingLabel", new CachedResourceLoader<Font>(new ResourceLoaderImpl<Font>(new SlickTrueTypeFontLoader(new ClassPathDataSource("assets/fonts/Mugnuts.ttf"), java.awt.Font.PLAIN, 24))));

			CachedResourceLoader bonusResourceLoader = new CachedResourceLoader(new ResourceLoaderImpl(new SlickAngelCodeFontLoader(new ClassPathDataSource("assets/fonts/bonusmessage.fnt"), new ClassPathDataSource("assets/fonts/bonusmessage.png"))));

			resourceManager.add("FontPointsLabel", bonusResourceLoader);
			resourceManager.add("FontBonusMessage", bonusResourceLoader);

			resourceManager.add("BackgroundMusic", new CachedResourceLoader(new ResourceLoaderImpl(new SlickMusicLoader("assets/musics/music.ogg"))));
			resourceManager.add("PlayMusic", new CachedResourceLoader(new ResourceLoaderImpl(new SlickMusicLoader("assets/musics/game.ogg"))));

			builderUtils.put("svg", new SlickSvgUtils());
			builderUtils.put("resourceManager", resourceManager);
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
