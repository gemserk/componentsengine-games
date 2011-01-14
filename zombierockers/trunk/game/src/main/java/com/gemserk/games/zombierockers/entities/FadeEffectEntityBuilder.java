package com.gemserk.games.zombierockers.entities;

import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.Animation;
import com.gemserk.commons.animation.handlers.AnimationHandlerManager;
import com.gemserk.commons.animation.timeline.Timeline;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineValue;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickImageRenderObject;
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

	@Inject
	MessageQueue messageQueue;

	@Inject
	AnimationHandlerManager animationHandlerManager;

	@Override
	public void build() {

		property("screenResolution", parameters.get("screenResolution"));

		final Rectangle screenResolution = Properties.getValue(entity, "screenResolution");

		property("color", slick.color(1, 1, 1, 1));
		property("layer", parameters.get("layer"));
		property("image", parameters.get("image"));

		property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
		property("direction", slick.vector(1, 0));
		property("size", slick.vector(1, 1));

		property("animation", parameters.get("animation"));

		component(new FieldsReflectionComponent("renderer") {

			@EntityProperty
			Vector2f position;

			@EntityProperty
			Vector2f size;

			@EntityProperty
			Vector2f direction;

			@EntityProperty
			Color color;

			@EntityProperty
			Integer layer;

			@EntityProperty
			Animation animation;

			@EntityProperty
			Resource<Image> image;

			@Handles
			public void render(Message message) {
				// if (animation.isFinished() || !animation.isStarted())
				// return;
				if (!animation.isStarted())
					return;
				RenderQueue renderQueue = Properties.getValue(message, "renderer");
				renderQueue.enqueue(new SlickImageRenderObject(layer, image.get(), position, size, (float) direction.getTheta(), color));
			}

		});

		component(new SynchronizeAnimationPropertiesComponent("animationComponent")).withProperties(new ComponentProperties() {
			{
				propertyRef("timelineAnimation", "animation");
			}
		});

	}
}
