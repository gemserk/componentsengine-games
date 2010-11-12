package com.gemserk.games.zombierockers.entities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.slick.predicates.SlickEntityPredicates;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.ImageCollisionMap;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class BulletEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slickUtils;

	private static int bulletNumber = 1;

	@Override
	public String getId() {
		return MessageFormat.format("bullet-{0}", BulletEntityBuilder.bulletNumber);
	}

	@Override
	public void build() {

		BulletEntityBuilder.bulletNumber++;

		tags("bullet", "nofriction");

		property("ball", parameters.get("ball"));
		property("position", parameters.get("position"));
		propertyRef("direction", "movement.velocity");

		property("radius", new FixedProperty(entity) {
			@Override
			public Object get() {
				Entity ball = Properties.getValue(getHolder(), "ball");
				return Properties.getValue(ball, "finalRadius");
			}
		});

		property("collisionMap", parameters.get("collisionMap"));
		property("collisionMask", 1);

		final Vector2f direction = (Vector2f) parameters.get("direction");
		final Float maxVelocity = (Float) parameters.get("maxVelocity");

		component(new SuperMovementComponent("movement")).withProperties(new ComponentProperties() {
			{
				property("velocity", direction.scale(maxVelocity));
				property("maxVelocity", maxVelocity);
				propertyRef("position", "position");
			}
		});

		component(new ImageRenderableComponent("imagerenderer")).withProperties(new ComponentProperties() {
			{
				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						Entity ball = Properties.getValue(getHolder(), "ball");
						Animation animation = Properties.getValue(ball, "animation");
						return animation.getCurrentFrame();
					}
				});

				property("color", new FixedProperty(entity) {
					@Override
					public Object get() {
						Entity ball = Properties.getValue(getHolder(), "ball");
						return Properties.getValue(ball, "color");
					}
				});

				propertyRef("position", "position");
				property("direction", new Vector2f(0, -1));
				property("layer", 20);
			}
		});

		component(new FieldsReflectionComponent("updateCollisionMaskHandler") {

			@EntityProperty(readOnly = true)
			ImageCollisionMap collisionMap;

			@EntityProperty(readOnly = true)
			Vector2f position;

			@EntityProperty
			Integer collisionMask;

			@Handles
			public void update(Message message) {

				if (collisionMap == null)
					return;

				collisionMask = collisionMap.collides(position.x, position.y);
			}

		});

		component(new FieldsReflectionComponent("bulletHitComponent") {

			@EntityProperty
			Vector2f position;

			@EntityProperty
			Float radius;

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void update(Message message) {

				final Integer delta = Properties.getValue(message, "delta");

				Predicate<Entity> predicate = Predicates.and(EntityPredicates.withAllTags("ball"), new Predicate<Entity>() {
					@Override
					public boolean apply(Entity ball) {
						Integer collisionMask1 = Properties.getValue(entity, "collisionMask");
						Integer collisionMask2 = Properties.getValue(ball, "collisionMask");
						return collisionMask1.equals(collisionMask2);
					}
				}, new Predicate<Entity>() {
					@Override
					public boolean apply(Entity ball) {
						Boolean alive = Properties.getValue(ball, "alive");
						return alive.booleanValue();
					}
				}, SlickEntityPredicates.isNear(position, 2 * radius - 3));

				final Collection<Entity> balls = entity.getRoot().getEntities(predicate);

				if (balls.size() == 0)
					return;

				messageQueue.enqueue(new Message("bulletHit", new PropertiesMapBuilder() {
					{
						property("source", entity);
						property("targets", new ArrayList<Entity>(balls));
						property("delta", delta);
					}
				}.build()));
			}

		});

		component(new FieldsReflectionComponent("bulletHitHandler") {

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void bulletHit(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (source != entity)
					return;
				messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity));
			}

		});
	}
}
