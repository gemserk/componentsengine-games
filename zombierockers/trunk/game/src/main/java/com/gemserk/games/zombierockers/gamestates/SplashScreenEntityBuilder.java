package com.gemserk.games.zombierockers.gamestates;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SplashScreenEntityBuilder extends EntityBuilder {

	private static final Logger logger = LoggerFactory.getLogger(SplashScreenEntityBuilder.class);

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) globalProperties.getProperties().get("screenResolution");

		component(new RectangleRendererComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector(0, 0));
				property("rectangle", slick.rectangle(0, 0, screenResolution.getWidth(), screenResolution.getHeight()));
				property("fillColor", slick.color(0.0f, 0.0f, 0.0f, 1.0f));
				property("lineColor", slick.color(0f, 0f, 0f, 0f));
				property("layer", -100);
			}
		});

		component(new ImageRenderableComponent("renderer")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
				property("image", slick.getResources().image("gemserklogo"));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
			}
		});

		property("timeToNextScreen", 2500);
		property("nextScreenLoaded", false);

		component(new FieldsReflectionComponent("nextScreenComponent") {

			@EntityProperty
			Integer timeToNextScreen;
			
			@EntityProperty
			Boolean nextScreenLoaded;

			@Handles
			public void update(Message message) {
				if (nextScreenLoaded)
					return;
				
				Integer delta = Properties.getValue(message, "delta");
				timeToNextScreen -= delta;
				if (timeToNextScreen <= 0) {
					Entity newScene = templateProvider.getTemplate("zombierockers.scenes.scene").instantiate(entity.getId());
					messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(newScene, entity.getRoot()));
					messageQueue.enqueueDelay(new Message("resume"));
					nextScreenLoaded = true;
				}
			}

			@Handles(ids = { "continue" })
			public void continueHandler(Message message) {
				Entity newScene = templateProvider.getTemplate("zombierockers.scenes.scene").instantiate(entity.getId());
				messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(newScene, entity.getRoot()));
				messageQueue.enqueueDelay(new Message("resume"));
				nextScreenLoaded = true;
			}

		});

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("return", "continue");
						press("space", "continue");
						press("escape", "continue");
					}
				});

				mouse(new MouseMappingBuilder() {
					@Override
					public void build() {
						press("left", "continue");
						press("right", "continue");
					}
				});
			}

		}));

	}
}