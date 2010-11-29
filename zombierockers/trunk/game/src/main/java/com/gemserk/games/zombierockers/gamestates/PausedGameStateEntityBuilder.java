package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class PausedGameStateEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;
	
	@Inject
	ResourceManager resourceManager;

	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-220, -50, 440, 100);
		final Rectangle screenBounds = (Rectangle) parameters.get("screenBounds");

		property("fontResource", resourceManager.get("FontDialogMessage", Font.class));
		property("font", new FixedProperty(entity) {
			@Override
			public Object get() {
				Resource fontResource = Properties.getValue(getHolder(), "fontResource");
				return fontResource.get();
			}
		});

		component(new ImageRenderableComponent("gameScreenshotRenderer")).withProperties(new ComponentProperties() {
			{
				// property("image", globalProperties.getProperties().get("screenshot"));
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", 900);

				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						Resource resource = (Resource) globalProperties.getProperties().get("screenshot");
						return resource.get();
					}
				});
			}
		});

		component(new RectangleRendererComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector(0, 0));
				property("rectangle", screenBounds);
				property("lineColor", slick.color(0.2f, 0.2f, 0.2f, 0.0f));
				property("fillColor", slick.color(0.5f, 0.5f, 0.5f, 0.5f));
				property("layer", 1000);
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("pausedLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1010);
				put("message", "Paused, press click to continue...");
//				put("font", font);
				put("font", new ReferenceProperty("font", entity));
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("restartLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 40f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1010);
				put("message", "Press \"r\" to restart");
//				put("font", font);
				put("font", new ReferenceProperty("font", entity));
			}
		}));

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("return", "resume");
						press("r", "restartLevel");
						press("space", "resume");
						press("p", "resume");
						press("escape", "resume");
						press("e", "editor");
					}
				});

				mouse(new MouseMappingBuilder() {
					@Override
					public void build() {
						press("left", "resume");
						press("right", "resume");
					}
				});
			}

		}));

	}
}