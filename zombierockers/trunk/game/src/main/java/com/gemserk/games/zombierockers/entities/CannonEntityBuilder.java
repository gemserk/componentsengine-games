package com.gemserk.games.zombierockers.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.WeaponComponent;
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.genericproviders.GenericProvider;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.componentsengine.triggers.Trigger;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class CannonEntityBuilder extends EntityBuilder {

	@Inject
	TemplateProvider templateProvider;

	@Inject
	MessageQueue messageQueue;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Inject
	ResourceManager resourceManager;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	SlickUtils slick;

	Random random = new Random();

	<T> T getRandomItem(List<T> items) {
		return items.get(random.nextInt(items.size()));
	}

	List<String> getPosibleTypes(Entity cannon) {
		Map<String, Map<String, Object>> ballDefinitions = Properties.getValue(cannon, "ballDefinitions");

		List<String> availableBallTypes = new LinkedList<String>();

		Collection<Entity> limbosNotEmpty = cannon.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("limbo"), new Predicate<Entity>() {
			@Override
			public boolean apply(Entity limbo) {
				Boolean isEmpty = Properties.getValue(limbo, "isEmpty");
				return !isEmpty.booleanValue();
			}
		}));

		if (limbosNotEmpty.size() == 0) {

			Collection<Entity> segments = cannon.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), new Predicate<Entity>() {
				@Override
				public boolean apply(Entity segment) {
					Boolean isEmpty = Properties.getValue(segment, "isEmpty");
					return !isEmpty.booleanValue();
				}
			}));

			for (Entity segment : segments) {
				Collection<Entity> balls = Properties.getValue(segment, "balls");
				for (Entity ball : balls) {
					String type = Properties.getValue(ball, "type");
					if (!availableBallTypes.contains(type))
						availableBallTypes.add(type);
				}
			}

		}

		if (availableBallTypes.isEmpty()) {
			for (String key : ballDefinitions.keySet())
				availableBallTypes.add(key);
		}

		return availableBallTypes;
	}

	void replaceBall(final Entity cannon, int index) {
		List<String> availableBallTypes = getPosibleTypes(cannon);
		String ballType = getRandomItem(availableBallTypes);

		Map<String, Map<String, Object>> ballDefinitions = Properties.getValue(cannon, "ballDefinitions");
		final Map<String, Object> ballDefinition = ballDefinitions.get(ballType);

		InstantiationTemplate instantiationTemplate = Properties.getValue(cannon, "ballTemplate");
		Entity ball = instantiationTemplate.get(new HashMap<String, Object>() {
			{
				put("ballDefinition", ballDefinition);
			}
		});

		List<Entity> balls = Properties.getValue(cannon, "balls");
		if (index >= balls.size())
			balls.add(ball);
		else
			balls.set(index, ball);
	}

	void generateBallHandlerMethod(Entity cannon) {
		Property<Object> currentBallIndex = cannon.getProperty("currentBallIndex");
		Integer index = (Integer) currentBallIndex.get();
		replaceBall(cannon, index);
		currentBallIndex.set((index + 1) % 2);
	}

	@Override
	public void build() {

		tags("cannon");

		// property("yaxisConstraint", 570f);
		property("position", new Vector2f(400f, 570f)); // TODO: use screen values... or level values...
		property("direction", new Vector2f(0, -1));

		property("bulletPosition", new FixedProperty(entity) {
			@Override
			public Object get() {
				Vector2f position = Properties.getValue(getHolder(), "position");
				return position.copy().add(new Vector2f(0, -10f));
			}
		});

		property("nextBulletPosition", new FixedProperty(entity) {
			@Override
			public Object get() {
				Vector2f position = Properties.getValue(getHolder(), "position");
				return position.copy().add(new Vector2f(0, 30f));
			}
		});

		property("fireTriggered", false);
		property("canFire", true);

		property("bounds", parameters.get("bounds"));

		property("balls", new LinkedList<Entity>());
		property("currentBallIndex", 0);

		property("currentBall", new FixedProperty(entity) {
			@Override
			public Object get() {
				List<Entity> balls = Properties.getValue(getHolder(), "balls");
				Integer currentBallIndex = Properties.getValue(getHolder(), "currentBallIndex");
				return balls.get(currentBallIndex);
			}
		});

		property("nextBall", new FixedProperty(entity) {
			@Override
			public Object get() {
				List<Entity> balls = Properties.getValue(getHolder(), "balls");
				Integer currentBallIndex = Properties.getValue(getHolder(), "currentBallIndex");
				return balls.get((currentBallIndex + 1) % 2);
			}
		});

		property("ballDefinitions", parameters.get("ballDefinitions"));
		property("collisionMap", parameters.get("collisionMap"));

		property("fireRate", 300);

		property("ballTemplate", new InstantiationTemplateImpl(templateProvider.getTemplate("zombierockers.entities.ball"), new GenericProvider() {

			@SuppressWarnings( { "unchecked", "serial" })
			@Override
			public <T> T get(Object... objects) {
				final Map<String, Object> data = (Map<String, Object>) objects[0];
				return (T) new HashMap<String, Object>() {
					{
						put("radius", 0.1f);
						put("finalRadius", 16f);
						put("definition", data.get("ballDefinition"));
						put("state", "inWorld");
						put("fired", "true");
					}
				};
			}

			@Override
			public <T> T get() {
				throw new RuntimeException("must never be called");
			}
		}));

		property("bulletTemplate", new InstantiationTemplateImpl(templateProvider.getTemplate("zombierockers.entities.bullet"), new GenericProvider() {

			@SuppressWarnings( { "unchecked", "serial" })
			@Override
			public <T> T get(Object... objects) {
				final Map<String, Object> cannon = (Map<String, Object>) objects[0];
				return (T) new HashMap<String, Object>() {
					{
						put("ball", cannon.get("ball"));
						put("position", ((Vector2f) cannon.get("position")).copy());
						put("direction", ((Vector2f) cannon.get("direction")).copy());
						put("maxVelocity", 0.7f);
						put("collisionMap", cannon.get("collisionMap"));
					}
				};
			}

			@Override
			public <T> T get() {
				throw new RuntimeException("must never be called");
			}
		}));

		component(new ImageRenderableComponent("currentBallRenderer")).withProperties(new ComponentProperties() {
			{
				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						Entity ball = Properties.getValue(getHolder(), "currentBall");
						return Properties.getValue(ball, "currentFrame");
					}
				});
				property("color", slick.color(1f, 1f, 1f, 1f));
				propertyRef("position", "bulletPosition");
				property("direction", new Vector2f(0, -1));
			}
		});

		component(new ImageRenderableComponent("nextBallRenderer")).withProperties(new ComponentProperties() {
			{
				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						Entity ball = Properties.getValue(getHolder(), "nextBall");
						return Properties.getValue(ball, "currentFrame");
					}
				});
				property("color", slick.color(1f, 1f, 1f, 1f));
				propertyRef("position", "nextBulletPosition");
				property("direction", new Vector2f(0, -1));
			}
		});

		component(new ImageRenderableComponent("imagerenderer")).withProperties(new ComponentProperties() {
			{
				property("color", new Color(1f, 1f, 1f, 1f));
				propertyRef("position", "position");
				propertyRef("direction", "direction");
				property("image", resourceManager.get("ship"));
			}
		});

		component(new ReferencePropertyComponent("controllerComponent") {

			@EntityProperty
			Property<Vector2f> position;

			@EntityProperty
			Property<Boolean> canFire;

			@EntityProperty
			Property<Boolean> fireTriggered;

			@EntityProperty
			Property<Integer> currentBallIndex;

			@Handles
			public void movemouse(Message message) {
				Float x = Properties.getValue(message, "x");
				position.get().x = x;
			}

			@Handles
			public void leftmouse(Message message) {
				if (canFire.get())
					fireTriggered.set(true);
			}

			@Handles
			public void rightmouse(Message message) {
				Integer index = currentBallIndex.get();
				currentBallIndex.set((index + 1) % 2);
			}
		}).withProperties(new ComponentProperties() {
			{
				propertyRef("position");
				propertyRef("canFire");
				propertyRef("fireTriggered");
				propertyRef("currentBallIndex");
			}
		});

		component(new WorldBoundsComponent("bounds")).withProperties(new ComponentProperties() {
			{
				propertyRef("bounds", "bounds");
				propertyRef("position", "position");
			}
		});

		component(new WeaponComponent("shooter")).withProperties(new ComponentProperties() {
			{
				propertyRef("reloadTime", "fireRate");
				propertyRef("position", "position");
				propertyRef("shouldFire", "fireTriggered");
				property("trigger", new FixedProperty(entity) {
					public Object get() {

						final Entity cannon = (Entity) getHolder();

						final Vector2f position = Properties.getValue(cannon, "position");
						final Vector2f direction = Properties.getValue(cannon, "direction");

						return new Trigger() {

							@Override
							public void trigger(Object... parameters) {
								Properties.setValue(cannon, "fireTriggered", false);
								InstantiationTemplate bulletInstantiationTemplate = Properties.getValue(cannon, "bulletTemplate");
								// parameters
								Map<String, Object> values = new HashMap<String, Object>() {
									{
										put("position", position.copy());
										put("direction", direction.copy());
										put("ball", cannon.getProperty("currentBall").get());
										put("collisionMap", cannon.getProperty("collisionMap").get());
									}
								};
								Entity bullet = bulletInstantiationTemplate.get(values);
								// log

								messageQueue.enqueue(childrenManagementMessageFactory.addEntity(bullet, entity.getParent()));
								messageQueue.enqueue(new Message("generateBall"));
								messageQueue.enqueue(new Message("bulletFired"));
							}

							@Override
							public void trigger() {

							}
						};

					};
				});
			}
		});

		component(new FieldsReflectionComponent("bulletFiredSound") {

			@EntityProperty
			Resource<Sound> bulletFiredSound;

			@Handles
			public void bulletFired(Message message) {
				bulletFiredSound.get().play();
			}

		}).withProperties(new ComponentProperties() {
			{
				property("bulletFiredSound", resourceManager.get("BulletFired"));
			}
		});

		generateBallHandlerMethod(entity);
		generateBallHandlerMethod(entity);

		component(new ReflectionComponent("generateBallHandler") {
			@Handles
			public void generateBall(Message message) {
				generateBallHandlerMethod(entity);
			}
		});

		component(new ReferencePropertyComponent("baseReachedHandler") {

			@EntityProperty
			Property<Boolean> canFire;

			@Handles
			public void baseReached(Message message) {
				canFire.set(false);
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("canFire");
			}
		});

		component(new ReferencePropertyComponent("restrictBallsToExisting") {

			@EntityProperty
			Property<List<Entity>> balls;

			@Handles
			public void explodeBall(Message message) {
				List<String> posibleTypes = getPosibleTypes(entity);
				List<Integer> toReplace = new ArrayList<Integer>();
				List<Entity> ballsList = balls.get();
				for (int i = 0; i < ballsList.size(); i++) {
					if (!posibleTypes.contains(Properties.getValue(ballsList.get(i), "type"))) {
						toReplace.add(i);
					}
				}
				for (Integer index : toReplace) {
					replaceBall(entity, index);
				}
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("balls");
			}
		});

		final FixedProperty colorProperty = new FixedProperty(entity) {
			@Override
			public Object get() {
				Entity currentBall = Properties.getValue(getHolder(), "currentBall");
				return Properties.getValue(currentBall, "color");
			}
		};

		child(templateProvider.getTemplate("zombierockers.entities.cursor").instantiate("cursor", new HashMap<String, Object>() {
			{
				put("color", colorProperty);
				put("position", new Vector2f(400, 300));
				put("bounds", new Rectangle(20, 20, 760, 520));
				put("layer", 10);
				put("image", resourceManager.get("cursor"));
			}
		}));

	}
}
