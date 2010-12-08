package com.gemserk.games.zombierockers.entities;

import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.Animation;
import com.gemserk.commons.animation.handlers.AnimationEventHandler;
import com.gemserk.commons.animation.handlers.AnimationHandlerManager;
import com.gemserk.commons.animation.timeline.LinearInterpolatorFactory;
import com.gemserk.commons.animation.timeline.Timeline;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineBuilder;
import com.gemserk.commons.animation.timeline.TimelineValue;
import com.gemserk.commons.animation.timeline.TimelineValueBuilder;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickImageRenderObject;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;

@SuppressWarnings("unchecked")
public class FadeEffectEntityBuilder extends EntityBuilder {

	class AnimationHandler implements AnimationEventHandler {

		private final Entity entity;

		public AnimationHandler(Entity entity) {
			this.entity = entity;
		}

		@Override
		public void onAnimationStarted(Animation animation) {
			messageQueue.enqueue(new Message("animationStarted", new PropertiesMapBuilder() {
				{
					property("entity", entity);
					property("entityId", entity.getId());
				}
			}.build()));
		}

		@Override
		public void onAnimationFinished(Animation animation) {
			messageQueue.enqueue(new Message("animationEnded", new PropertiesMapBuilder() {
				{
					property("entity", entity);
					property("entityId", entity.getId());
				}
			}.build()));
		}
	}

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

	@Override
	public void build() {

		property("screenResolution", parameters.get("screenResolution"));

		final Rectangle screenResolution = Properties.getValue(entity, "screenResolution");

		final Integer delay = parameters.get("delay", 0);
		final Boolean started = parameters.get("started", true);

		property("color", slick.color(1, 1, 1, 1));
		property("layer", parameters.get("layer"));
		property("time", parameters.get("time"));
		property("delay", parameters.get("delay"));
		property("image", parameters.get("image"));

		property("effect", parameters.get("effect"));

		property("position", slick.vector((float) (screenResolution.getWidth() * 0.5f), (float) (screenResolution.getHeight() * 0.5f)));
		property("direction", slick.vector(1, 0));
		property("size", slick.vector(1, 1));

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
				if (animation.isFinished() || !animation.isStarted())
					return;

				RenderQueue renderQueue = Properties.getValue(message, "renderer");
				renderQueue.enqueue(new SlickImageRenderObject(layer, image.get(), position, size, (float) direction.getTheta(), color));
			}

		});

		String effect = parameters.get("effect");
		final Integer time = parameters.get("time");

		boolean fadeInEffect = "fadeIn".equals(effect);

		final Color startColor = fadeInEffect ? slick.color(1f, 1f, 1f, 1f) : slick.color(1f, 1f, 1f, 0f);
		final Color endColor = fadeInEffect ? slick.color(1f, 1f, 1f, 0f) : slick.color(1f, 1f, 1f, 1f);

		Timeline timeline = new TimelineBuilder() {
			{
				delay(delay);
				value("color", new TimelineValueBuilder<Color>() {
					{
						interpolator(LinearInterpolatorFactory.linearInterpolatorColor());
						keyFrame(0, startColor);
						keyFrame(time, endColor);
					}
				});
			}
		}.build();

		final TimelineAnimation timelineAnimation = new TimelineAnimation(timeline, started);

		component(new SynchronizeAnimationPropertiesComponent("animationComponent")).withProperties(new ComponentProperties() {
			{
				property("timelineAnimation", timelineAnimation);
			}
		});

		property("animation", timelineAnimation);

		AnimationHandlerManager animationHandlerManager = new AnimationHandlerManager();
		animationHandlerManager.handleAnimationChanges(entity.getId(), timelineAnimation);
		animationHandlerManager.addAnimationHandler(entity.getId(), new AnimationHandler(entity));

		property("animationHandlerManager", animationHandlerManager);

		component(new FieldsReflectionComponent("animationManagerComponent") {

			@EntityProperty
			AnimationHandlerManager animationHandlerManager;

			@Handles
			public void update(Message message) {
				animationHandlerManager.checkAnimationChanges();
			}

			@Handles
			public void restartAnimation(Message message) {
				String animationId = Properties.getValue(message, "animationId");
				Animation animation = animationHandlerManager.getAnimation(animationId);
				if (animation == null)
					return;
				animation.restart();
			}

		});

	}
}
