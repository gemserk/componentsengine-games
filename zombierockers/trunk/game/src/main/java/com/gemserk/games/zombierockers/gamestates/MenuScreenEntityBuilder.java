package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.properties.InterpolatedProperty;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.utils.DesktopUtils;
import com.gemserk.datastore.Data;
import com.gemserk.resources.ResourceManager;
import com.gemserk.slick.animation.timeline.ColorInterpolatedValue;
import com.gemserk.slick.animation.timeline.Vector2fInterpolatedValue;
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
	ResourceManager resourceManager;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_titleLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontTitle"));
				put("position", slick.vector(screenBounds.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Main Menu");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "_playButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() - 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "PLAY");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				put("buttonReleasedMessageId", parameters.get("onPlayButton"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "_settingsButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "SETTINGS");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				put("buttonReleasedMessageId", parameters.get("onSettingsButton"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "_profileButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Profile");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				put("buttonReleasedMessageId", parameters.get("onProfileButton"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "_exitButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 100f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "EXIT");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				put("buttonReleasedMessageId", parameters.get("onExitButton"));
			}
		}));

		Data profile = (Data) globalProperties.getProperties().get("profile");

		final String text;

		String name = (String) profile.getValues().get("name");

		if (profile.getTags().contains("guest"))
			text = "Welcome, you are playing as guest";
		else
			text = "Welcome, you are playing as " + name;

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_welcomeBackProfileLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getMaxY() - 90f));
				put("color", slick.color(0.5f, 0.8f, 0.5f, 1f));
				put("bounds", slick.rectangle(-160, -25, 320, 50));
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", text);
			}
		}));

		// child(templateProvider.getTemplate("gemserk.gui.image").instantiate(entity.getId() + "_homePageImage", new HashMap<String, Object>() {
		// {
		// put("image", resourceManager.get("gemserkLogoWhiteSmall"));
		// put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getMaxY() - 40f));
		// put("color", new Color(0.4f, 0.4f, 0.7f, 1.0f));
		// }
		// }));

		final String homePageLinkEntityId = entity.getId() + "_homePageLink";

		child(javaEntityTemplateProvider.get().with(new EntityBuilder() {

			@Override
			public void build() {

				parent("gemserk.gui.image", parameters);

				property("homePageLinkId", parameters.get("homePageLinkId"));

				component(new ReferencePropertyComponent("homePageLinkEntityId") {

					@EntityProperty
					Property<String> homePageLinkId;

					@EntityProperty
					Property<Color> color;

					@EntityProperty
					Property<Vector2f> size;

					@Handles
					public void onButtonFocused(Message message) {
						Entity source = Properties.getValue(message, "source");
						if (!homePageLinkId.get().equals(source.getId()))
							return;

						color.set(new Color(0.2f, 0.2f, 0.8f, 1.0f));
						size.set(new Vector2f(1.0f, 1.0f));
					}

					@Handles
					public void onButtonLostFocus(Message message) {
						Entity source = Properties.getValue(message, "source");
						if (!homePageLinkId.get().equals(source.getId()))
							return;

						color.set(new Color(0.4f, 0.4f, 0.6f, 1.0f));
						size.set(new Vector2f(0.9f, 0.9f));
					}

					@Handles
					public void onButtonPressed(Message message) {
						Entity source = Properties.getValue(message, "source");
						if (!homePageLinkId.get().equals(source.getId()))
							return;
						size.set(new Vector2f(0.9f, 0.9f));
					}

					@Handles
					public void onButtonReleased(Message message) {
						Entity source = Properties.getValue(message, "source");
						if (!homePageLinkId.get().equals(source.getId()))
							return;
						size.set(new Vector2f(1.0f, 1.0f));
						DesktopUtils.openUrlInBrowser("http://blog.gemserk.com");
					}

				});

			}

		}).instantiate(entity.getId() + "_homePageImage", new HashMap<String, Object>() {
			{
				put("image", resourceManager.get("gemserkLogoWhiteSmall"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getMaxY() - 40f));
				// put("color", new Color(0.4f, 0.4f, 0.6f, 1.0f));

				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(new Color(0.4f, 0.4f, 0.6f, 1.0f)), 0.01f));
				put("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(new Vector2f(0.9f, 0.9f)), 0.01f));

				put("homePageLinkId", homePageLinkEntityId);
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.button").instantiate(homePageLinkEntityId, new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getMaxY() - 40f));
				put("bounds", slick.rectangle(-160, -25, 320, 50));
				put("layer", 1);
				// put("url", "http://blog.gemserk.com");
			}
		}));

	}
}