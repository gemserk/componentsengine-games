package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Music;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class MenuScreenEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Inject
	ResourceManager resourceManager;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) globalProperties.getProperties().get("screenResolution");
		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

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

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("titleLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontTitle2"));
				put("position", slick.vector(screenResolution.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Main Menu");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("playButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() - 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "PLAY");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("settingsButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY()));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "SETTINGS");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("exitButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() + 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "EXIT");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				
				put("buttonReleasedMessageId", "exitButton");
			}
		}));

		property("buttonPressed", "play");

		component(new FieldsReflectionComponent("guiHandler") {

			@EntityProperty
			String buttonPressed;

			@Handles
			public void buttonReleased(Message message) {
				String id = Properties.getValue(message, "buttonId");

				if ("playButton".equals(id))
					buttonPressed = "play";

				if ("settingsButton".equals(id))
					buttonPressed = "settings";

				if ("exitButton".equals(id))
					buttonPressed = "exit";

				messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
					{
						property("animationId", "fadeOutEffect");
					}
				}.build()));
			}
			
			@Handles
			public void exitButton(Message message) {
				System.out.println("EXIT BUTTON");
				buttonPressed = "exit";
				messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
					{
						property("animationId", "fadeOutEffect");
					}
				}.build()));
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

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {

					}
				});

				mouse(new MouseMappingBuilder() {
					@Override
					public void build() {

					}
				});
			}

		}));

	}
}