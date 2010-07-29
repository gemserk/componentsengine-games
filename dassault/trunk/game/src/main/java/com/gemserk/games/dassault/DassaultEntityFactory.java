package com.gemserk.games.dassault;

import java.util.ArrayList;
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
import com.gemserk.games.dassault.EntityBuilderFactory.ComponentProperties;
import com.gemserk.games.dassault.EntityBuilderFactory.EntityBuilder;
import com.gemserk.games.dassault.InnerProperty.PropertyGetter;
import com.gemserk.games.dassault.components.blasterbullet.UpdateCollisionsComponent;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class DassaultEntityFactory {

	private final Injector injector;

	private final BuilderUtils builderUtils;

	public DassaultEntityFactory(Injector injector, BuilderUtils builderUtils) {
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

	public Entity blasterBullet(String id, final Map<String, Object> parameters) {
		return new EntityBuilderFactory().entity(id).with(new EntityBuilder(injector) {

			@Override
			public void build() {

				tags("bullet", "blasterbullet");

				property("position", parameters.get("position"));
				property("moveDirection", parameters.get("moveDirection"));
				property("speed", parameters.get("speed"));
				property("damage", parameters.get("damage"));

				property("player", parameters.get("player"));
				property("owner", parameters.get("owner"));

				property("bounds", new Rectangle(-2, -2, 4, 4));

				property("collisions", new ArrayList<EntityCollidableImpl>());

				property("headImage", builderUtils.getResources().image("blasterbullet_head"));
				property("bodyImage", builderUtils.getResources().image("blasterbullet_body"));
				property("auraImage", builderUtils.getResources().image("blasterbullet_aura"));

				property("force", new Vector2f());

				component(new UpdateCollisionsComponent("updateCollisions")).withProperties(new ComponentProperties() {
					{
						propertyRef("collidable", "collidable");
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

				component(new HitWhenCollisionDetected("hitWhenCollisionDetected")).withProperties(new ComponentProperties() {
					{
						propertyRef("collisions", "collisions");
						propertyRef("damage", "damage");
					}
				});

				component(new BulletDeadHandler("bulletDeadHandler")).withProperties(new ComponentProperties() {
					{
						propertyRef("collidable", "collidable");
						propertyRef("player", "player");
					}
				});

				component(new ImageRenderableComponent("headImageRenderer")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("image", "headImage");
						propertyRef("direction", "moveDirection");
						property("layer", -5);
						property("size", new Vector2f(0.6f, 0.6f));
						property("color", Color.white);
					}
				});
				
				component(new ImageRenderableComponent("auraImageRenderer")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("image", "auraImage");
						propertyRef("direction", "moveDirection");
						property("layer", -7);
						property("size", new Vector2f(0.6f, 0.6f));
						property("color", Color.black);
					}
				});
				
				component(new ImageRenderableComponent("bodyImageRenderer")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("image", "bodyImage");
						propertyRef("direction", "moveDirection");
						property("layer", -6);
						property("size", new Vector2f(0.6f, 0.6f));

						property("color", new InnerProperty(entity, new PropertyGetter() {
							@Override
							public Object get(Entity entity) {
								Entity player = Properties.getValue(entity, "player");
								return Properties.getValue(player, "color");
							}
						}));
					}
				});

			}
		});
	}

}
