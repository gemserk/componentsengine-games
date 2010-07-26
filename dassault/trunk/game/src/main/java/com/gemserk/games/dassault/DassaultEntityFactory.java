package com.gemserk.games.dassault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.collisions.Collidable;
import com.gemserk.commons.collisions.EntityCollidableImpl;
import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.effects.EffectFactory;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.render.Renderer;
import com.gemserk.componentsengine.render.SlickImageRenderObject;
import com.gemserk.games.dassault.EntityBuilderFactory.ComponentProperties;
import com.gemserk.games.dassault.EntityBuilderFactory.EntityBuilder;
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

		@EntityProperty
		Vector2f moveDirection;

		public UpdateMoveDirection(String id) {
			super(id);
		}

		@Handles
		public void update(Message message) {
			Vector2f desiredDirection = moveDirection.copy().normalise().scale(0.01f);
			Vector2f force = Properties.getValue(entity, "movementComponent.force");
			force.add(desiredDirection);
		}
	}

	public class BulletDeadHandler extends FieldsReflectionComponent {

		@Inject
		MessageQueue messageQueue;

		@EntityProperty
		Collidable collidable;

		@EntityProperty
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

		@EntityProperty
		List<EntityCollidableImpl> collisions;

		@EntityProperty
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
					property("bullet", entity);
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
	
	public class BulletRendererComponent extends FieldsReflectionComponent {
		
		@EntityProperty
		Image headImage;

		@EntityProperty
		Image bodyImage;

		@EntityProperty
		Image auraImage;
		
		@EntityProperty
		Vector2f position;
		
		@EntityProperty
		Entity player;

		public BulletRendererComponent(String id) {
			super(id);
		}

		@Handles
		public void render(Message message) {
			
			Renderer renderer = Properties.getValue(message, "renderer");
			
			Vector2f moveDirection = Properties.getValue(entity, "moveDirection");
			
			float angle = (float) moveDirection.getTheta();
			
			Vector2f size = new Vector2f(0.6f, 0.6f);
			
			int layer = -5;
			Color playerColor = Properties.getValue(player, "color");
			
			renderer.enqueue(new SlickImageRenderObject(layer-2, auraImage, position, size, angle, Color.black));
			renderer.enqueue(new SlickImageRenderObject(layer-1, bodyImage, position, size, angle, playerColor));
			renderer.enqueue(new SlickImageRenderObject(layer, headImage, position, size, angle, Color.white));
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

				component(new UpdateMoveDirection("updateMoveDirection")).withProperties(new ComponentProperties() {
					{
						propertyRef("moveDirection", "moveDirection");
					}
				});
				
				component(new BulletRendererComponent("bulletRenderer")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("player", "player");
						propertyRef("headImage", "headImage");
						propertyRef("bodyImage", "bodyImage");
						propertyRef("auraImage", "auraImage");
					}
				});
				

			}
		});
	}

}
