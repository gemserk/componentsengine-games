package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Music;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class MenuGameStateEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@Inject
	ResourceManager resourceManager;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) globalProperties.getProperties().get("screenResolution");
		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("screenBounds", screenResolution);

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

		child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("started", false);
				put("time", 1000);
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenResolution);
				put("effect", "fadeIn");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("started", false);
				put("time", 1000);
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenResolution);
				put("effect", "fadeOut");
			}
		}));

		property("mainMenuScreen", null);
		property("buttonPressed", "play");

		component(new ReferencePropertyComponent("gameStateLogic") {

			@EntityProperty
			Property<Rectangle> screenBounds;

			@EntityProperty
			Property<Entity> mainMenuScreen;

			@EntityProperty
			Property<String> buttonPressed;

			@Handles
			public void enterNodeState(Message message) {
				mainMenuScreen.set(templateProvider.getTemplate("screens.menu").instantiate(entity.getId() + "_menuScreen", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds.get());
						put("onPlayButton", "onPlayButton");
						put("onSettingsButton", "onSettingsButton");
						put("onExitButton", "onExitButton");
					}
				}));
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(mainMenuScreen.get(), entity));
			}

			@Handles
			public void leaveNodeState(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(mainMenuScreen.get()));
				mainMenuScreen.set(null);
			}

			@Handles
			public void onPlayButton(Message message) {
				buttonPressed.set("play");
				messageQueue.enqueue(messageBuilder.newMessage("restartAnimation").property("animationId", "fadeOutEffect").get());
			}

			@Handles
			public void onSettingsButton(Message message) {
				buttonPressed.set("settings");
				messageQueue.enqueue(messageBuilder.newMessage("restartAnimation").property("animationId", "fadeOutEffect").get());
			}

			@Handles
			public void onExitButton(Message message) {
				buttonPressed.set("exit");
				messageQueue.enqueue(messageBuilder.newMessage("restartAnimation").property("animationId", "fadeOutEffect").get());
				// fadeOutAnimation.start();
				// add custom child with logic fo animation end.
				// {
				// property(animation, fadeoutanimation)
				// property(callback, callback)
				// }
			}

		});

		component(new FieldsReflectionComponent("fadeInWhenEnterState") {
			@Handles
			public void enterNodeState(Message message) {
				messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
					{
						property("animationId", "fadeInEffect");
					}
				}.build()));
			}
		});

		property("backgroundMusic", resourceManager.get("BackgroundMusic"));

		component(new FieldsReflectionComponent("restartMusicComponent") {

			@EntityProperty(readOnly = true)
			Integer time;

			Integer currentTime = 0;

			@EntityProperty
			Resource<Music> backgroundMusic;

			@Handles
			public void update(Message message) {
				if (currentTime <= 0) {
					Music music = backgroundMusic.get();
					if (!music.playing())
						music.play();
					currentTime += time;
				}
				Integer delta = Properties.getValue(message, "delta");
				currentTime -= delta;
			}

		}).withProperties(new ComponentProperties() {
			{
				property("time", 1000);
			}
		});

		component(new FieldsReflectionComponent("backgroundMusicComponent") {

			@EntityProperty
			Resource<Music> backgroundMusic;

			@Handles
			public void enterNodeState(Message message) {
				if (!backgroundMusic.get().playing())
					backgroundMusic.get().fade(1000, 1.0f, false);
			}

			@Handles
			public void buttonReleased(Message message) {
				String id = Properties.getValue(message, "buttonId");

				if ("playButton".equals(id))
					backgroundMusic.get().fade(1000, 0.0f, true);
			}

		});

		component(new FieldsReflectionComponent("animationComponentHandler") {

			@EntityProperty
			String buttonPressed;

			@Handles
			public void animationEnded(Message message) {
				String animationId = Properties.getValue(message, "entityId");
				if ("fadeOutEffect".equalsIgnoreCase(animationId)) {

					if ("exit".equals(buttonPressed)) {
						System.exit(0);
					} else if ("settings".equals(buttonPressed)) {
						messageQueue.enqueue(new Message("settings"));
					} else if ("play".equals(buttonPressed)) {
						messageQueue.enqueue(new Message("resume"));
						messageQueue.enqueueDelay(new Message("restartLevel"));
					}

				}
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

	}
}