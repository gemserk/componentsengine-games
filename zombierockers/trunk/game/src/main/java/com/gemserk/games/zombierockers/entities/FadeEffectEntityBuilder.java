package com.gemserk.games.zombierockers.entities;

import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

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
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;

@SuppressWarnings("unchecked")
public class FadeEffectEntityBuilder extends EntityBuilder {

	public static class SynchronizeAnimationPropertiesComponent extends FieldsReflectionComponent {

		@EntityProperty(readOnly = true)
		TimelineAnimation timelineAnimation;

		private SynchronizeAnimationPropertiesComponent(String id) {
			super(id);
		}

		@Handles
		public void update(Message message) {
			Integer delta = Properties.getValue(message, "delta");
			timelineAnimation.update(delta);
			Timeline timeline = timelineAnimation.getTimeline();
			Map<String, TimelineValue> timelineValues = timeline.getTimelineValues();
			for (String propertyName : timelineValues.keySet())
				entity.getProperty(propertyName).set(timelineAnimation.getValue(propertyName));
		}
	}

	@Inject
	SlickUtils slick;

	@Inject
	ResourceManager resourceManager;

	@Override
	public void build() {

		final Rectangle screenResolution = (Rectangle) parameters.get("screenResolution");
		final Integer offset = (Integer) (parameters.get("offset") != null ? parameters.get("offset") : 0);

		property("color", slick.color(1, 1, 1, 1));
		property("layer", parameters.get("layer"));
		property("time", parameters.get("time"));
		property("offset", parameters.get("offset"));
		property("image", parameters.get("image"));

		property("effect", parameters.get("effect"));

		component(new ImageRenderableComponent("fadeImage")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
				property("direction", slick.vector(1, 0));
				propertyRef("color", "color");
				propertyRef("layer", "layer");
				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						Resource resource = Properties.getValue(getHolder(), "image");
						return resource.get();
					}
				});
			}
		});

		String effect = (String) parameters.get("effect");

		final Integer time = (Integer) parameters.get("time");

		boolean fadeInEffect = "fadeIn".equals(effect);

		final Color startColor = fadeInEffect ? slick.color(1f, 1f, 1f, 1f) : slick.color(1f, 1f, 1f, 0f);
		final Color endColor = fadeInEffect ? slick.color(1f, 1f, 1f, 0f) : slick.color(1f, 1f, 1f, 1f);

		Timeline timeline = new TimelineBuilder() {
			{
				value("color", new TimelineValueBuilder<Color>() {
					{
						interpolator(LinearInterpolatorFactory.linearInterpolatorColor());
						keyFrame(0 + offset, startColor);
						keyFrame(time + offset, endColor);
					}
				});
			}
		}.build();

		final TimelineAnimation timelineAnimation = new TimelineAnimation(timeline, true);

		component(new SynchronizeAnimationPropertiesComponent("animationComponent")).withProperties(new ComponentProperties() {
			{
				property("timelineAnimation", timelineAnimation);
			}
		});

	}
}
