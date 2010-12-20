package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.resources.ResourceManager;
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

	@Inject
	ResourceManager resourceManager;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) globalProperties.getProperties().get("screenResolution");

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

		component(new ImageRenderableComponent("logo")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 1);
				property("image", resourceManager.get("gemserklogo"));
			}
		});

		child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("time", 1000);
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenResolution);
				put("effect", "fadeIn");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("delay", 2000);
				put("time", 1000);
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenResolution);
				put("effect", "fadeOut");
			}
		}));
		
		property("nextScreenLoaded", false);
		component(new FieldsReflectionComponent("nextScreenComponent") {

			@EntityProperty
			Boolean nextScreenLoaded;

			@Handles(ids = { "continue" })
			public void continueHandler(Message message) {
				if (nextScreenLoaded)
					return;
				messageQueue.enqueueDelay(new Message("menu"));
				nextScreenLoaded = true;
			}
			
			@Handles
			public void animationEnded(Message message) {
				if (nextScreenLoaded)
					return;
				String animationId = Properties.getValue(message, "entityId");
				if ("fadeOutEffect".equalsIgnoreCase(animationId)) {
					messageQueue.enqueueDelay(new Message("menu"));
					nextScreenLoaded = true;
				}
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