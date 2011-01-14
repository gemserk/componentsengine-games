package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.animation.handlers.AnimationHandlerManager;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.ScenesDefinitions;
import com.gemserk.resources.ResourceManager;
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

	@Inject
	ResourceManager resourceManager;

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

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
						put("settings", "settings");
						put("highscores", "highscores");
						put("enterscore", "enterscore");
						put("profile", "profile");
					}
				});
				put("stateEntities", new HashMap<String, Object>() {
					{
						put("splash", templateProvider.getTemplate("zombierockers.screens.splash").instantiate("splash", new HashMap<String, Object>() {
							{

							}
						}));
						put("menu", templateProvider.getTemplate("gamestates.menu").instantiate("menu", new HashMap<String, Object>() {
							{
								put("screenBounds", screenBounds);
							}
						}));
						put("playing", templateProvider.getTemplate("zombierockers.scenes.playing").instantiate("playing", new HashMap<String, Object>() {
							{
								put("levels", new ReferenceProperty("levels", entity));
								put("level", new ReferenceProperty("currentLevel", entity));
								put("screenBounds", screenBounds);
							}
						}));
						put("paused", templateProvider.getTemplate("gamestates.paused").instantiate("paused", new HashMap<String, Object>() {
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
						put("settings", templateProvider.getTemplate("gamestates.settings").instantiate("settings", new HashMap<String, Object>() {
							{
								put("screenBounds", screenBounds);
							}
						}));
						put("highscores", templateProvider.getTemplate("gamestates.highscores").instantiate("highscores", new HashMap<String, Object>() {
							{
								put("level", new ReferenceProperty("currentLevel", entity));
								put("screenBounds", screenBounds);
							}
						}));
						put("enterscore", templateProvider.getTemplate("gamestates.enterscore").instantiate("enterscore", new HashMap<String, Object>() {
							{
								put("screenBounds", screenBounds);
							}
						}));
						put("profile", templateProvider.getTemplate("gamestates.profile").instantiate("profile", new HashMap<String, Object>() {
							{
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
				// messageQueue.enqueueDelay(new Message("settings"));
				// messageQueue.enqueueDelay(new Message("highscores"));
				// messageQueue.enqueueDelay(new Message("resume"));
				// messageQueue.enqueueDelay(new Message("enterscore"));
			}

		});

		// component(new ReferencePropertyComponent("temporalComponentToChangeToEnterScoreScreen") {
		//
		// @Handles
		// public void newScoreToEnter(Message message) {
		// messageQueue.enqueueDelay(messageBuilder.newMessage("enterscore").property("points", 5600l).property("levelName", "level01").get());
		// }
		//
		// });
		//
		// component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {
		//
		// @Override
		// public void build() {
		//
		// keyboard(new KeyboardMappingBuilder() {
		// @Override
		// public void build() {
		// press("m", "newScoreToEnter");
		// }
		// });
		// }
		//
		// }));

		// child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

		component(new ReflectionComponent("nextLevelHandler") {

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void nextLevel(Message message) {
				final List levels = Properties.getValue(entity, "levels");
				final Integer levelIndex = Properties.getValue(entity, "levelIndex");

				final Integer nextLevel = levelIndex + 1;
				if (nextLevel < levels.size()) {
					messageQueue.enqueueDelay(new Message("loadLevel", new PropertiesMapBuilder() {
						{
							property("levelIndex", nextLevel);
						}
					}.build()));
				} else {
					Properties.setValue(entity, "levelIndex", 0);
					messageQueue.enqueueDelay(new Message("menu"));
				}

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
		
		component(new FieldsReflectionComponent("animationManagerComponent") {

			@Inject
			AnimationHandlerManager animationHandlerManager;

			@Handles
			public void update(Message message) {
				animationHandlerManager.checkAnimationChanges();
			}

		});

	}
}