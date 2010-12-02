package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.commons.animation.components.UpdateTimeProviderComponent;
import com.gemserk.commons.animation.properties.InterpolatedPropertyTimeProvider;
import com.gemserk.commons.animation.timeline.LinearInterpolatorFactory;
import com.gemserk.commons.animation.timeline.Timeline;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineBuilder;
import com.gemserk.commons.animation.timeline.TimelineValue;
import com.gemserk.commons.animation.timeline.TimelineValueBuilder;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
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
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.resources.Resource;
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
	MessageQueue messageQueue;

	@Inject
	ResourceManager resourceManager;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) globalProperties.getProperties().get("screenResolution");
		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("fontResource", resourceManager.get("FontDialogMessage2"));
		property("font", new FixedProperty(entity) {
			@Override
			public Object get() {
				Resource fontResource = Properties.getValue(getHolder(), "fontResource");
				return fontResource.get();
			}
		});

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						return resourceManager.get("background").get();
					}
				});
			}
		});

		child(javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {

				property("color", slick.color(1, 1, 1, 1));

				component(new ImageRenderableComponent("fadeImage")).withProperties(new ComponentProperties() {
					{
						property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
						propertyRef("color", "color");
						property("direction", slick.vector(1, 0));
						property("layer", 10);
						property("image", new FixedProperty(entity) {
							@Override
							public Object get() {
								return resourceManager.get("background").get();
							}
						});
					}
				});

				final Integer time = (Integer) parameters.get("time");

				final Timeline animationTimeline = new TimelineBuilder() {
					{
						value("color", new TimelineValueBuilder<Color>() {
							{
								interpolator(LinearInterpolatorFactory.linearInterpolatorColor());

								keyFrame(0, new Color(1f, 1f, 1f, 1f));
								keyFrame(time, new Color(1f, 1f, 1f, 0f));
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
		}).instantiate("fadeInImage", new HashMap<String, Object>() {
			{
				put("time", 2000);
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("titleLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenResolution.getCenterX(), 40f));
				put("color", slick.color(0.5f, 0.2f, 0.2f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "¿Zombie Rockers?");
				put("font", new FixedProperty(entity) {
					@Override
					public Object get() {
						return resourceManager.get("FontTitle2").get();
					}
				});
			}
		}));

		final InterpolatedPropertyTimeProvider timeProvider = new InterpolatedPropertyTimeProvider();

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("playButton", new HashMap<String, Object>() {
			{
				put("font", new ReferenceProperty<Object>("font", entity));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() - 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Play");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("settingsButton", new HashMap<String, Object>() {
			{
				put("font", new ReferenceProperty<Object>("font", entity));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY()));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Settings");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate("exitButton", new HashMap<String, Object>() {
			{
				put("font", new ReferenceProperty<Object>("font", entity));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() + 50f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Exit");
			}
		}));

		component(new ReferencePropertyComponent("guiHandler") {

			@Handles
			public void buttonReleased(Message message) {
				String id = Properties.getValue(message, "buttonId");

				if ("playButton".equals(id)) {
					System.out.println("play button");

					messageQueue.enqueue(new Message("resume"));
					messageQueue.enqueueDelay(new Message("restartLevel"));

					return;
				}

				if ("settingsButton".equals(id)) {
					System.out.println("settings button");
					return;
				}

				if ("exitButton".equals(id)) {
					System.out.println("exit button");
					System.exit(0);
					return;
				}
			}

		});

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {

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