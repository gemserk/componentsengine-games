package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.datastore.Data;
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
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getMaxY() - 50f));
				put("color", slick.color(0.5f, 0.8f, 0.5f, 1f));
				put("bounds", slick.rectangle(-160, -25, 320, 50));
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", text);
			}
		}));
	}
}