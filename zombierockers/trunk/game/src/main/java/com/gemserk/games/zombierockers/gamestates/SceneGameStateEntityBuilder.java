package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.slick.util.ScreenshotGrabber;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.utils.EntityDumper;
import com.gemserk.games.zombierockers.ScenesDefinitions;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SceneGameStateEntityBuilder extends EntityBuilder {

	private static final Logger logger = LoggerFactory.getLogger(SceneGameStateEntityBuilder.class);

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@SuppressWarnings( { "unchecked", "serial" })
	@Override
	public void build() {

		List<Map<String, Object>> levels = ScenesDefinitions.levels();

		final Integer levelIndex = (Integer) (parameters.get("levelIndex") != null ? parameters.get("levelIndex") : 0);
		final Map<String, Object> currentLevel = levels.get(levelIndex);
		final Rectangle screenBounds = slick.rectangle(0, 0, 800, 600);

		property("screenBounds", screenBounds);

		property("gameState", "playing");
		property("playtime", 0);

		property("levelIndex", levelIndex);
		property("levels", levels);
		property("currentLevel", new FixedProperty(entity) {
			@Override
			public Object get() {
				List levels = Properties.getValue(getHolder(), "levels");
				Integer levelIndex = Properties.getValue(getHolder(), "levelIndex");
				return levels.get(levelIndex);
			}
		});

		parent("GameStateManager", new HashMap<String, Object>() {
			{
				put("transitions", new HashMap<String, Object>() {
					{
						put("splash", "splash");
						put("menu", "menu");
						put("resume", "playing");
						put("paused", "paused");
						put("editor", "editor");
					}
				});
				put("stateEntities", new HashMap<String, Object>() {
					{
						put("splash", templateProvider.getTemplate("zombierockers.screens.splash").instantiate("splash", new HashMap<String, Object>() {
							{

							}
						}));
						put("menu", templateProvider.getTemplate("zombierockers.screens.menu").instantiate("menu", new HashMap<String, Object>() {
							{

							}
						}));
						put("playing", templateProvider.getTemplate("zombierockers.scenes.playing").instantiate("playing", new HashMap<String, Object>() {
							{
								put("levels", new ReferenceProperty("levels", entity));
								put("level", new ReferenceProperty("currentLevel", entity));
								put("screenBounds", screenBounds);
							}
						}));
						put("paused", templateProvider.getTemplate("zombierockers.scenes.paused").instantiate("paused", new HashMap<String, Object>() {
							{
								put("screenBounds", screenBounds);
							}
						}));
						put("editor", templateProvider.getTemplate("zombierockers.scenes.sceneEditor").instantiate("editor", new HashMap<String, Object>() {
							{
								put("level", new ReferenceProperty("currentLevel", entity));
								put("screenBounds", screenBounds);
							}
						}));
					}
				});
			}
		});

		component(new ReflectionComponent("enterStateHandler") {

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void enterState(Message message) {
				messageQueue.enqueueDelay(new Message("splash"));
			}

		});

		component(new ReflectionComponent("makeScreenshotHandler") {

			@Inject
			ScreenshotGrabber screenshotGrabber;

			@Handles
			public void makeScreenshot(Message message) {
				screenshotGrabber.saveScreenshot("zombierockers-", "png");
			}

		});

		component(new ReflectionComponent("toggleFpsHandler") {
			
			@Inject
			GlobalProperties globalProperties;

			@Handles
			public void toggleFps(Message message) {
				Boolean showFps = (Boolean) globalProperties.getProperties().get("showFps");
				globalProperties.getProperties().put("showFps", !showFps);
			}

		});

		// component(new ReflectionComponent("reloadResourcesHandler") {
		//
		// @Inject
		// ResourceManager resourceManager;
		//			
		// @Handles
		// public void reloadResources(Message message) {
		// resourceManager.reloadAll();
		// }
		//
		// });

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("x", "dumpEntities");
						press("n", "nextLevel");
						press("k", "makeScreenshot");
						press("f", "toggleFps");
						// press("u", "reloadResources");
					}
				});
			}

		}));

		component(new ReflectionComponent("dumpEntitiesHandler") {

			@Handles
			public void dumpEntities(Message message) {
				System.out.println(JSONArray.fromObject(new EntityDumper().dumpEntity(entity.getRoot())).toString(4));
			}

		});

		component(new ReflectionComponent("nextLevelHandler") {

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void nextLevel(Message message) {
				final List levels = Properties.getValue(entity, "levels");
				final Integer levelIndex = Properties.getValue(entity, "levelIndex");
				messageQueue.enqueueDelay(new Message("loadLevel", new PropertiesMapBuilder() {
					{
						property("levelIndex", (levelIndex + 1) % levels.size());
					}
				}.build()));
			}

		});

		component(new ReflectionComponent("restartLevelHandler") {

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void restartLevel(Message message) {
				final Integer levelIndex = Properties.getValue(entity, "levelIndex");
				messageQueue.enqueueDelay(new Message("loadLevel", new PropertiesMapBuilder() {
					{
						property("levelIndex", levelIndex);
					}
				}.build()));
			}

		});

		component(new ReferencePropertyComponent("changeLevelHandler") {

			@EntityProperty
			Property<List> levels;

			@EntityProperty
			Property<InstantiationTemplate> sceneInstantiationTemplate;

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void loadLevel(Message message) {
				final Integer nextLevelIndex = Properties.getValue(message, "levelIndex");
				Properties.setValue(entity, "levelIndex", nextLevelIndex);
				messageQueue.enqueue(new Message("resume"));
				messageQueue.enqueueDelay(new Message("changeLevel", new PropertiesMapBuilder() {
					{

					}
				}.build()));
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("levels");
				propertyRef("sceneInstantiationTemplate");
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("fpsLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenBounds.getMinX() + 60f, screenBounds.getMinY() + 30f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", slick.rectangle(-50f, -20f, 100f, 40f));
				put("align", "left");
				put("valign", "top");
				put("layer", 10000);
				put("message", new FixedProperty(entity) {
					public Object get() {
						Boolean showFps = (Boolean) globalProperties.getProperties().get("showFps");
						if (showFps)
							return "FPS: " + slick.getGameContainer().getFPS();
						return "";
					};
				});
			}
		}));

	}
}