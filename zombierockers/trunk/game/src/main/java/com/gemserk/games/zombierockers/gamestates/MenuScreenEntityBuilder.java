package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
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

	private static final Logger logger = LoggerFactory.getLogger(MenuScreenEntityBuilder.class);

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
		final Rectangle labelRectangle = slick.rectangle(-220, -50, 440, 100);
		
		property("fontResource", resourceManager.get("FontDialogMessage2", Font.class));
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
						return resourceManager.get("background", Image.class).get();
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
								return resourceManager.get("background", Image.class).get();
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
				put("time", 3000);
			}
		}));
		
		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("titleLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenResolution.getCenterX(), 40f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Main Menu");
				put("font", new ReferenceProperty("font", entity));
			}
		}));
		
		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("playLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() - 50f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Play");
				put("font", new ReferenceProperty("font", entity));
			}
		}));
		
		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("settingsLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY()));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Settings");
				put("font", new ReferenceProperty("font", entity));
			}
		}));
		
		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("exitLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() + 50f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Exit");
				put("font", new ReferenceProperty("font", entity));
			}
		}));

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