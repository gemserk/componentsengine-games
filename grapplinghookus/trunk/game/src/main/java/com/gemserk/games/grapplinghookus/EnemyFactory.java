package com.gemserk.games.grapplinghookus;

import java.util.Collection;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.slick.geom.ShapeUtils;
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
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.games.grapplinghookus.EntityBuilderFactory.ComponentProperties;
import com.gemserk.games.grapplinghookus.EntityBuilderFactory.EntityBuilder;
import com.gemserk.games.grapplinghookus.components.blasterbullet.UpdateCollisionsComponent;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class EnemyFactory {

	private final Injector injector;

	private final BuilderUtils builderUtils;

	public EnemyFactory(Injector injector, BuilderUtils builderUtils) {
		this.injector = injector;
		this.builderUtils = builderUtils;
	}

	public class DetectCollisionsWithEnemiesComponent extends FieldsReflectionComponent {

		@EntityProperty
		Shape bounds;

		@Inject
		MessageQueue messageQueue;

		private DetectCollisionsWithEnemiesComponent(String id) {
			super(id);
		}

		@Handles
		public void update(Message message) {

			final ShapeUtils shapeUtils = new ShapeUtils(bounds);

			Collection<Entity> collidingEnemies = entity.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("enemy"), new Predicate<Entity>() {

				@Override
				public boolean apply(Entity innerEntity) {
					Shape entityBounds = Properties.getValue(innerEntity, "bounds");
					return shapeUtils.collides(entityBounds);
				}

			}));

			if (collidingEnemies.isEmpty())
				return;

			final Entity enemy = (Entity) collidingEnemies.toArray()[0];
			
			final Entity sourceEnemy = Properties.getValue(entity, "enemy");

			messageQueue.enqueue(new Message("enemyKilled", new PropertiesMapBuilder() {
				{
					property("bullet", entity);
					property("enemy", enemy);
					property("sourceEnemy", sourceEnemy);
				}
			}.build()));

		}

		@Handles(ids = { "enemyKilled" })
		public void removeWhenEnemyKilled(Message message) {
			Entity targetBullet = Properties.getValue(message, "bullet");

			if (entity != targetBullet)
				return;

			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity));
		}
	}

	public class RotateWhileFlyingComponent extends FieldsReflectionComponent {

		@EntityProperty
		Vector2f direction;

		@EntityProperty
		Float speed;

		public RotateWhileFlyingComponent(String id) {
			super(id);
		}

		@Handles
		public void update(Message message) {

			Integer delta = Properties.getValue(message, "delta");

			float angle = speed * delta;

			direction.add(angle);

		}
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

	public Entity enemy(String id, final Map<String, Object> parameters) {
		return new EntityBuilderFactory().entity(id).with(new EntityBuilder(injector) {

			@Override
			public void build() {

				tags("enemy");

				property("position", parameters.get("position"));
				property("moveDirection", parameters.get("moveDirection"));
				property("speed", parameters.get("speed"));
				property("points", parameters.get("points"));

				property("bounds", new Rectangle(-20, -20, 40, 40));

				property("image", builderUtils.getResources().image("enemy01"));

				property("force", new Vector2f());
				
				property("targeted", false);

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

				component(new FieldsReflectionComponent("removeEnemyWhenKilled") {

					@Inject
					MessageQueue messageQueue;

					@Handles(ids = { "enemyKilled" })
					public void removeWhenEnemyKilled(Message message) {
						Entity targetEnemy = Properties.getValue(message, "enemy");

						if (entity != targetEnemy)
							return;

						messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity));

						final Vector2f position = Properties.getValue(entity, "position");

						final Color startColor = new Color(1f, 1f, 1f, 1f);
						final Color endColor = new Color(0.7f, 0.7f, 0.7f, 1f);

						messageQueue.enqueue(new Message("explosion", new PropertiesMapBuilder() {
							{
								property("explosion", EffectFactory.explosionEffect(50, (int) position.x, (int) position.y, 0f, 360f, 400, 5.0f, 100f, 300f, 3f, startColor, endColor));
								property("layer", 5);
							}
						}.build()));
					}

				});

			}
		});
	}

	public Entity enemybullet(String id, final Map<String, Object> parameters) {
		return new EntityBuilderFactory().entity(id).with(new EntityBuilder(injector) {

			@Override
			public void build() {

				tags("enemybullet");

				property("position", parameters.get("position"));
				property("moveDirection", parameters.get("moveDirection"));
				property("speed", parameters.get("speed"));

				property("enemy", parameters.get("enemy"));

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

				property("renderDirection", new Vector2f(1f, 0f));
				property("rotationSpeed", 0.12f);

				component(new RotateWhileFlyingComponent("rotateWhileFlying")).withProperties(new ComponentProperties() {
					{
						propertyRef("direction", "renderDirection");
						propertyRef("speed", "rotationSpeed");
					}
				});

				component(new DetectCollisionsWithEnemiesComponent("detectCollisionWithEnemies")).withProperties(new ComponentProperties() {
					{
						propertyRef("bounds", "bounds");
					}
				});

				component(new ImageRenderableComponent("imageRenderer")).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
						propertyRef("image", "image");
						propertyRef("direction", "renderDirection");
						property("layer", 5);
						property("color", Color.white);
					}
				});
				
				component(new FieldsReflectionComponent("respawnEnemyWhenScreenLimitReached"){
					
					@Inject
					MessageQueue messageQueue;
					
					@EntityProperty
					Vector2f position;
					
					@Handles
					public void update(Message message) {
						
						if (position.y < 10 ||  position.x < 20 ||  position.x > 620f) {
							
							Entity enemy = Properties.getValue(entity, "enemy");
							Properties.setValue(enemy, "position", position.copy());
							Properties.setValue(enemy, "targeted", false);
							
							messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(enemy, entity.getParent()));
							messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity));
							
						}
						
					}
					
				}).withProperties(new ComponentProperties() {
					{
						propertyRef("position", "position");
					}
				});

			}
		});
	}

}
