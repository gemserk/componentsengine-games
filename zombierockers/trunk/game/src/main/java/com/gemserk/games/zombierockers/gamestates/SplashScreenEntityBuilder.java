package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.animation.Animation;
import com.gemserk.commons.animation.handlers.AnimationEventHandler;
import com.gemserk.commons.animation.handlers.AnimationHandlerManager;
import com.gemserk.commons.animation.timeline.LinearInterpolatorFactory;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineBuilder;
import com.gemserk.commons.animation.timeline.TimelineValueBuilder;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Property;
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

	@Inject
	AnimationHandlerManager animationHandlerManager;

	@Override
	public void build() {

		final Rectangle screenBounds = (Rectangle) globalProperties.getProperties().get("screenResolution");

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

		component(new ImageRenderableComponent("logo")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 1);
				property("image", resourceManager.get("gemserklogo"));
			}
		});

		final Animation fadeInAnimation = new TimelineAnimation(new TimelineBuilder() {
			{
				delay(0);
				value("color", new TimelineValueBuilder<Color>() {
					{
						interpolator(LinearInterpolatorFactory.linearInterpolatorColor());
						keyFrame(0, slick.color(1f, 1f, 1f, 1f));
						keyFrame(1000, slick.color(1f, 1f, 1f, 0f));
					}
				});
			}
		}.build(), true);

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenBounds);
				put("animation", fadeInAnimation);
			}
		}));

		final Animation fadeOutAnimation = new TimelineAnimation(new TimelineBuilder() {
			{
				delay(2000);
				value("color", new TimelineValueBuilder<Color>() {
					{
						interpolator(LinearInterpolatorFactory.linearInterpolatorColor());
						keyFrame(0, slick.color(1f, 1f, 1f, 0f));
						keyFrame(1000, slick.color(1f, 1f, 1f, 1f));
					}
				});
			}
		}.build(), true);

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenBounds);
				put("animation", fadeOutAnimation);
			}
		}));

		property("fadeInAnimation", fadeInAnimation);
		property("fadeOutAnimation", fadeOutAnimation);

		property("nextScreenLoaded", false);

		component(new ReferencePropertyComponent("nextScreenComponent") {

			@EntityProperty
			Property<Boolean> nextScreenLoaded;

			@EntityProperty
			Property<Animation> fadeOutAnimation;

			@Handles(ids = { "continue" })
			public void continueHandler(Message message) {
				if (nextScreenLoaded.get())
					return;
				messageQueue.enqueueDelay(new Message("menu"));
				nextScreenLoaded.set(true);
			}

			@Handles
			public void enterNodeState(Message message) {
				animationHandlerManager.with(new AnimationEventHandler() {
					@Override
					public void onAnimationFinished(Animation animation) {
						messageQueue.enqueueDelay(new Message("menu"));
					}
				}).handleChangesOf(fadeOutAnimation.get());
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