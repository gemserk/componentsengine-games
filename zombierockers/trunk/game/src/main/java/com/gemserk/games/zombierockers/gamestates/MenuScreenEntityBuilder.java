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
		
		Data profile = (Data) globalProperties.getProperties().get("profile");
		
		final String text;
		
		String name = (String) profile.getValues().get("name");
		
		if (profile.getTags().contains("guest"))
			text = "Welcome, you are playing as guest";
		else 
			text = "Welcome, you are playing as " + name;
		
		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("welcomeBackProfileLabel", new HashMap<String, Object>() {
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