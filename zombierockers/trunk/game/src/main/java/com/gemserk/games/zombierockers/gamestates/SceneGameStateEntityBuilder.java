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
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.genericproviders.GenericProvider;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.EntityTemplate;
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
		// List<Map<String, Object>> levels = (List<Map<String, Object>>) parameters.get("levels");
		final Map<String, Object> currentLevel = levels.get(levelIndex);
		final Rectangle screenBounds = slick.rectangle(0, 0, 800, 600);

		property("gameState", "playing");
		property("playtime", 0);

		property("levelIndex", levelIndex);
		property("levels", levels);

		parent("GameStateManager", new HashMap<String, Object>() {
			{
				put("transitions", new HashMap<String, Object>() {
					{
						put("splash", "splash");
						put("menu", "menu");
						put("resume", "playing");
						put("paused", "paused");
						put("gameover", "gameover");
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
								put("level", currentLevel);
								put("screenBounds", screenBounds);
							}
						}));
						put("paused", templateProvider.getTemplate("zombierockers.scenes.paused").instantiate("paused", new HashMap<String, Object>() {
							{
								put("screenBounds", screenBounds);
							}
						}));
						put("gameover", templateProvider.getTemplate("zombierockers.scenes.gameover").instantiate("gameover", new HashMap<String, Object>() {
							{
								put("screenBounds", screenBounds);
							}
						}));
						put("editor", templateProvider.getTemplate("zombierockers.scenes.sceneEditor").instantiate("editor", new HashMap<String, Object>() {
							{
								put("level", currentLevel);
								put("screenBounds", screenBounds);
								put("currentLevelIndex", levelIndex);
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

		property("sceneInstantiationTemplate", new InstantiationTemplateImpl(templateProvider.getTemplate("zombierockers.scenes.scene"), new GenericProvider() {

			@Override
			public <T> T get(Object... objects) {
				final Map<String, Object> data = (Map<String, Object>) objects[0];
				return (T) new HashMap<String, Object>() {
					{
						put("levels", data.get("levels"));
						put("levelIndex", data.get("levelIndex"));
					}
				};
			}

			@Override
			public <T> T get() {
				throw new RuntimeException("must never be called");
			}

		}));

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
				Entity newScene = sceneInstantiationTemplate.get().get(new HashMap<String, Object>() {
					{
						put("levelIndex", nextLevelIndex);
						put("levels", levels.get());
					}
				});
				messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(newScene, entity.getRoot()));
				messageQueue.enqueueDelay(new Message("resume"));
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("levels");
				propertyRef("sceneInstantiationTemplate");
			}
		});

		component(new ReferencePropertyComponent("goToEditorHandler") {

			@EntityProperty
			Property<Integer> levelIndex;

			@EntityProperty
			Property<List> levels;

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void goToEditor(Message message) {
				EntityTemplate levelEditorTemplate = templateProvider.getTemplate("zombierockers.scenes.sceneEditor");
				Entity newScene = levelEditorTemplate.instantiate(entity.getId(), new HashMap<String, Object>() {
					{
						put("levelIndex", levelIndex.get());
						put("level", levels.get().get(levelIndex.get()));
					}
				});
				messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(newScene, entity.getRoot()));
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("levelIndex");
				propertyRef("levels");
			}
		});

	}
}