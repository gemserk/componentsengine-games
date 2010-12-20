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

		final Rectangle labelRectangle = slick.rectangle(-120, -20, 240, 40);
		final Rectangle screenBounds = (Rectangle) parameters.get("screenBounds");

		component(new ImageRenderableComponent("gameScreenshotRenderer")).withProperties(new ComponentProperties() {
			{
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", globalProperties.getProperties().get("screenshot"));
			}
		});

		component(new RectangleRendererComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector(0, 0));
				property("rectangle", screenBounds);
				property("lineColor", slick.color(0.2f, 0.2f, 0.2f, 0.0f));
				property("fillColor", slick.color(0.5f, 0.5f, 0.5f, 0.5f));
				property("layer", 1);
			}
		});

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("resumeButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() - 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 2);
				put("message", "Resume");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				
				put("buttonReleasedMessageId", "resume");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("restartButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 2);
				put("message", "Restart");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				
				put("buttonReleasedMessageId", "restartLevel");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("quitButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 50));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 2);
				put("message", "Quit");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				
				put("buttonReleasedMessageId", "menu");
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
						
					}
				});
			}

		}));

	}
}