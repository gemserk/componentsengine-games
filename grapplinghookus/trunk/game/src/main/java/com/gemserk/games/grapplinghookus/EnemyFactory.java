package com.gemserk.games.grapplinghookus;

import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.collisions.Collidable;
import com.gemserk.commons.collisions.EntityCollidableImpl;
import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.effects.EffectFactory;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.games.grapplinghookus.EntityBuilderFactory.ComponentProperties;
import com.gemserk.games.grapplinghookus.EntityBuilderFactory.EntityBuilder;
import com.gemserk.games.grapplinghookus.components.blasterbullet.UpdateCollisionsComponent;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class EnemyFactory {

	private final Injector injector;

	private final BuilderUtils builderUtils;

	public EnemyFactory(Injector injector, BuilderUtils builderUtils) {
		this.injector = injector;
		this.builderUtils = builderUtils;
	}

	public class UpdateMoveDirection extends FieldsReflectionComponent {

		@EntityProperty(readOnly = true)
		Vector2f direction;

		@EntityProperty
		Vector2f force;

		public UpdateMoveDirection(String id) {
			super(id);
		}

		@Handles
		public void update(Message message) {
			Vector2f desiredDirection = direction.copy().normalise().scale(0.01f);
			force.add(desiredDirection);
		}
	}

	public class BulletDeadHandler extends FieldsReflectionComponent {

		@Inject
		MessageQueue messageQueue;

		@EntityProperty(readOnly = true)
		Collidable collidable;

		@EntityProperty(readOnly = true)
		Entity player;

		public BulletDeadHandler(String id) {
			super(id);
		}

		@Handles
		public void bulletDead(Message message) {
			if (entity != Properties.getValue(message, "bullet"))
				return;

			collidable.remove();

			Color playerColor = Properties.getValue(player, "color");

			final Color startColor = new Color(playerColor);
			startColor.a = 1f;

			final Color endColor = new Color(playerColor);
			endColor.a = 0.2f;

			final Vector2f position = Properties.getValue(entity, "position");

			messageQueue.enqueue(new Message("explosion", new PropertiesMapBuilder() {
				{
					property("explosion", EffectFactory.explosionEffect(30, (int) position.x, (int) position.y, 0f, 360f, 400, 5.0f, 20f, 60f, 1f, startColor, endColor));
				}
			}.build()));

			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity));
		}
	}

	public class HitWhenCollisionDetected extends FieldsReflectionComponent {

		@Inject
		MessageQueue messageQueue;

		@EntityProperty(readOnly = true)
		List<EntityCollidableImpl> collisions;

		@EntityProperty(readOnly = true)
		Float damage;

		public HitWhenCollisionDetected(String id) {
			super(id);
		}

		@Handles
		public void update(Message message) {
			if (collisions.isEmpty())
				return;

			final Entity target = collisions.get(0).getEntity();

			messageQueue.enqueue(new Message("collisionDetected", new PropertiesMapBuilder() {
				{
					property("source", entity);
					property("target", target);
					property("damage", damage);
				}
			}.build()));

			messageQueue.enqueue(new Message("bulletDead", new PropertiesMapBuilder() {
				{
					property("bullet", entity);
				}
			}.build()));
		}
	}

	public Entity enemy(String id, final Map<String, Object> parameters) {
		return new EntityBuilderFactory().entity(id).with(new EntityBuilder(injector) {

			@Override
			public void build() {

				tags("enemy");

				property("position", parameters.get("position"));
				property("moveDirection", parameters.get("moveDirection"));
				property("speed", parameters.get("speed"));

				property("bounds", new Rectangle(-10, -5, 20, 10));

				property("image", builderUtils.getResources().image("enemy01"));

				property("force", new Vector2f());

				component(new UpdateCollisionsComponent("updateCollisions")).withProperties(new ComponentProperties() {
					{
						propertyRef("bounds", "bounds");
						propertyRef("position", "position");
					}
				});

				component(new SuperMovementComponent("movementComponent")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("maxVelocity", "speed");
						propertyRef("force", "force");
					}
				});

				component(new UpdateMoveDirection("updateMoveDirection")).withProperties(new ComponentProperties() {
					{
						propertyRef("direction", "moveDirection");
						propertyRef("force", "force");
					}
				});

				component(new ImageRenderableComponent("imageRenderer")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("image", "image");
						property("direction", new Vector2f(1f, 0));
						property("layer", 5);
						property("color", Color.white);
					}
				});
				
			}
		});
	}

}
