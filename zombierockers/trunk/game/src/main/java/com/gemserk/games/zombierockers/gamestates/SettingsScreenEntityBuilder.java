package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SettingsScreenEntityBuilder extends EntityBuilder {

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

	@Inject
	GameContainer gameContainer;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) globalProperties.getProperties().get("screenResolution");
		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("font", resourceManager.get("FontDialogMessage2"));

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

		component(new ReferencePropertyComponent("fadeInWhenEnterState") {
			@Handles
			public void enterNodeState(Message message) {
				messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
					{
						property("animationId", "fadeInEffect");
					}
				}.build()));
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("titleLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontTitle"));
				put("position", slick.vector(screenResolution.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Settings");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("backButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenResolution.getMaxX() - 100, screenResolution.getMaxY() - 40f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "BACK");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("fullScreenLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() - 80f));
				put("bounds", slick.rectangle(-200, -25, 400, 50));
				put("align", "left");
				put("valign", "center");
				put("layer", 1);
				put("message", "Fullscreen");
				put("color", slick.color(0.3f, 0.3f, 0.8f, 1f));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.checkbox").instantiate("fullScreenCheckbox", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenResolution.getCenterX() + 140, screenResolution.getCenterY() - 80f));
				put("value", new FixedProperty(entity) {
					@Override
					public void set(Object value) {
						Boolean fullscreen = (Boolean) value;
						try {
							gameContainer.setFullscreen(fullscreen);
						} catch (SlickException e) {
							e.printStackTrace();
						}
					}
					@Override
					public Object get() {
						return gameContainer.isFullscreen();
					}
				});
				put("layer", 1);
				put("imageFalse", resourceManager.get("false"));
				put("imageTrue", resourceManager.get("true"));
				put("bounds", slick.rectangle(-30, -30, 60, 60));
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
			}
		}));

		property("buttonPressed", "back");

		component(new FieldsReflectionComponent("guiHandler") {

			@EntityProperty
			String buttonPressed;

			@Handles
			public void buttonReleased(Message message) {
				String id = Properties.getValue(message, "buttonId");

				if ("backButton".equals(id)) {
					buttonPressed = "back";
					messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
						{
							property("animationId", "fadeOutEffect");
						}
					}.build()));
				} else if ("fullScreenCheckbox".equals(id)) {
					
				}

			}

		});

		component(new FieldsReflectionComponent("animationComponentHandler") {

			@EntityProperty
			String buttonPressed;

			@Handles
			public void animationEnded(Message message) {
				String animationId = Properties.getValue(message, "entityId");
				if ("fadeOutEffect".equalsIgnoreCase(animationId)) {
					if ("back".equals(buttonPressed)) {
						messageQueue.enqueue(new Message("menu"));
					}
				}
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

	}
}