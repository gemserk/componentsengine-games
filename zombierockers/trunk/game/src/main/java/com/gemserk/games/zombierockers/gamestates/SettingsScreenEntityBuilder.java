package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
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

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");
		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_titleLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontTitle"));
				put("position", slick.vector(screenBounds.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Settings");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "_backButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getMaxX() - 100, screenBounds.getMaxY() - 40f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "BACK");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));

				put("buttonReleasedMessageId", parameters.get("backButtonMessage"));
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_fullScreenLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() - 80f));
				put("bounds", slick.rectangle(-200, -25, 400, 50));
				put("align", "left");
				put("valign", "center");
				put("layer", 1);
				put("message", "Fullscreen");
				put("color", slick.color(0.3f, 0.3f, 0.8f, 1f));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.checkbox").instantiate(entity.getId() + "_fullScreenCheckbox", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenBounds.getCenterX() + 140, screenBounds.getCenterY() - 80f));
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

	}
}