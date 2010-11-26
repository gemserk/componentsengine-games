package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.animation.timeline.LinearInterpolatorFactory;
import com.gemserk.commons.animation.timeline.Timeline;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineBuilder;
import com.gemserk.commons.animation.timeline.TimelineValue;
import com.gemserk.commons.animation.timeline.TimelineValueBuilder;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
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
import com.gemserk.componentsengine.properties.FixedProperty;
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
				property("layer", -1);
				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						return resourceManager.get("background", Image.class).get();
					}
				});
			}
		});

		child(javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {

				property("color", slick.color(1, 1, 1, 1));

				component(new ImageRenderableComponent("logo")).withProperties(new ComponentProperties() {
					{
						property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
						propertyRef("color", "color");
						property("direction", slick.vector(1, 0));
						property("layer", 1);
						property("image", new FixedProperty(entity) {
							@Override
							public Object get() {
								return resourceManager.get("gemserklogo", Image.class).get();
							}
						});
					}
				});

				final Integer time = (Integer) parameters.get("time");

				final int part = time / 4;

				final Timeline animationTimeline = new TimelineBuilder() {
					{
						value("color", new TimelineValueBuilder<Color>() {
							{
								interpolator(LinearInterpolatorFactory.linearInterpolatorColor());

								keyFrame(0, new Color(1f, 1f, 1f, 0f));
								keyFrame(part, new Color(1f, 1f, 1f, 1f));
								keyFrame(part * 3, new Color(1f, 1f, 1f, 1f));
								keyFrame(part * 4, new Color(1f, 1f, 1f, 0f));
							}
						});

					}
				}.build();

				component(new FieldsReflectionComponent("animationComponent") {

					@EntityProperty(readOnly = true)
					TimelineAnimation timelineAnimation;

					@Handles
					public void update(Message message) {
						Integer delta = Properties.getValue(message, "delta");
						timelineAnimation.update(delta);

						// synchronize values...
						Timeline timeline = timelineAnimation.getTimeline();
						Map<String, TimelineValue> timelineValues = timeline.getTimelineValues();
						for (String propertyName : timelineValues.keySet()) {
							entity.getProperty(propertyName).set(timelineAnimation.getValue(propertyName));
						}

					}

				}).withProperties(new ComponentProperties() {
					{
						property("timelineAnimation", new TimelineAnimation(animationTimeline, true));
					}
				});

			}
		}).instantiate("gemserkLogo", new HashMap<String, Object>() {
			{
				put("time", 3500);
			}
		}));

		property("timeToNextScreen", 3500);
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