package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
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

		component(new ImageRenderableComponent("gameScreenshotRenderer")).withProperties(new ComponentProperties() {
			{
				// property("image", globalProperties.getProperties().get("screenshot"));
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", 900);
				property("image", globalProperties.getProperties().get("screenshot"));
				// property("image", new FixedProperty(entity) {
				// @Override
				// public Object get() {
				// return globalProperties.getProperties().get("screenshot");
				// }
				// });
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
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1010);
				put("message", "Paused, press click to continue...");
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("restartLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 40f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1010);
				put("message", "Press \"r\" to restart");
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("quitLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 80f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1010);
				put("message", "or press \"q\" to quit");
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
						press("q", "menu");
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