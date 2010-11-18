package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.annotations.GameProperties;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class PausedGameStateEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	@GameProperties
	Map<String, Object> gameProperties;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		final Font font = slick.getResources().getFonts().font(false, false, 28);

		final Rectangle labelRectangle = slick.rectangle(-220, -50, 440, 100);
		final Rectangle screenBounds = slick.rectangle(0, 0, 800, 600);

		component(new ImageRenderableComponent("gameScreenshotRenderer")).withProperties(new ComponentProperties() {
			{
				property("image", gameProperties.get("screenshot"));
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", 900);
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

		child(javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {
				parent("gemserk.gui.label", new HashMap<String, Object>() {
					{
						put("font", font);
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
						put("fontColor", slick.color(0f, 0f, 0f, 1f));
						put("bounds", labelRectangle);
						put("align", "center");
						put("valign", "center");
						put("layer", 1010);
					}
				});
				property("message", "Paused, press click to continue...");
			}
		}).instantiate("pausedLabel"));

		child(javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {
				parent("gemserk.gui.label", new HashMap<String, Object>() {
					{
						put("font", font);
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() + 40f));
						put("fontColor", slick.color(0f, 0f, 0f, 1f));
						put("bounds", labelRectangle);
						put("align", "center");
						put("valign", "center");
						put("layer", 1010);
					}
				});
				property("message", "Press \"r\" to restart");
			}
		}).instantiate("restartLabel"));

		component(new ReflectionComponent("inputMessagesHandler") {
			@Inject
			MessageQueue messageQueue;

			@Handles
			public void resumeGame(Message message) {
				messageQueue.enqueue(new Message("resume"));
			}

			@Handles
			public void restart(Message message) {
				messageQueue.enqueue(new Message("restartLevel"));
			}

		});

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("return", "resumeGame");
						press("r", "restart");
						press("space", "resumeGame");
						press("p", "resumeGame");
						press("escape", "resumeGame");
						press("e", "goToEditor");
					}
				});

				mouse(new MouseMappingBuilder() {
					@Override
					public void build() {
						press("left", "resumeGame");
						press("right", "resumeGame");
					}
				});
			}

		}));

	}
}