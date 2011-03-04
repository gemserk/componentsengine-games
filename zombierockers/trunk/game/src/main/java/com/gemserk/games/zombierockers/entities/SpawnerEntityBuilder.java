package com.gemserk.games.zombierockers.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.path.PathTraversal;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.genericproviders.GenericProvider;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class SpawnerEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(SpawnerEntityBuilder.class);

	@Inject
	MessageQueue messageQueue;

	Random random = new Random();

	<K, V> V getRandomItemFromMap(Map<K, V> items) {
		Set<K> keySet = items.keySet();
		ArrayList<K> keyArray = new ArrayList<K>(keySet);
		return items.get(keyArray.get(random.nextInt(keyArray.size())));
	}

	@Override
	public void build() {

		tags("spawner");

		property("ballDefinitions", parameters.get("ballDefinitions"));
		property("subPathDefinitions", parameters.get("subPathDefinitions"));

		property("spawnQuantity", parameters.get("ballsQuantity"));
		property("fired", false);

		property("path", parameters.get("path"));
		property("pathEntity", parameters.get("pathEntity"));

		property("pathProperties", parameters.get("pathProperties"));

		property("ballTemplate", new InstantiationTemplateImpl(templateProvider.getTemplate("zombierockers.entities.ball"), new GenericProvider() {

			@SuppressWarnings( { "unchecked", "serial" })
			@Override
			public <T> T get(Object... objects) {
				final Map<String, Object> data = (Map<String, Object>) objects[0];
				return (T) new HashMap<String, Object>() {
					{
						put("direction", new Vector2f(0, 1));
						put("radius", 16f);
						put("definition", data.get("ballDefinition"));

						put("collisionMap", data.get("collisionMap"));
						// put("subPathDefinitions", data.get("subPathDefinitions"));
					}
				};
			}

			@Override
			public <T> T get() {
				throw new RuntimeException("must never be called");
			}
		}));

		property("segmentTemplate", new InstantiationTemplateImpl(templateProvider.getTemplate("zombierockers.entities.segment"), new GenericProvider() {

			@SuppressWarnings( { "unchecked", "serial" })
			@Override
			public <T> T get(Object... objects) {
				final Map<String, Object> data = (Map<String, Object>) objects[0];
				return (T) new HashMap<String, Object>() {
					{
						put("pathTraversal", data.get("pathTraversal"));
						put("acceleratedSpeed", data.get("acceleratedSpeed"));
						put("accelerationStopPoint", data.get("accelerationStopPoint"));
						put("minSpeedFactor", data.get("minSpeedFactor"));
						put("maxSpeed", data.get("maxSpeed"));
						put("speedWhenReachBase", data.get("speedWhenReachBase"));
						put("speed", 0.04f);
						put("accelerated", true);
						put("pathLength", data.get("pathLength"));
						
						put("pathEntity", data.get("pathEntity"));
					}
				};
			}

			@Override
			public <T> T get() {
				throw new RuntimeException("must never be called");
			}
		}));

		component(new ReferencePropertyComponent("spawnHandler") {

			@EntityProperty
			Property<Boolean> fired;

			@EntityProperty
			Property<InstantiationTemplate> ballTemplate;

			@EntityProperty
			Property<InstantiationTemplate> segmentTemplate;

			@EntityProperty
			Property<Map<String, Map<String, Object>>> ballDefinitions;

			@EntityProperty
			Property<Integer> spawnQuantity;

			@EntityProperty
			Property<Map<String, Object>> pathProperties;

			@EntityProperty
			Property<Path> path;

			@Handles
			public void spawn(Message message) {

				if (fired.get())
					return;

				fired.set(true);

				final List<Entity> balls = new ArrayList<Entity>();

				for (int i = 0; i < spawnQuantity.get(); i++) {

					final Map<String, Object> ballDefinition = getRandomItemFromMap(ballDefinitions.get());

					Entity ball = ballTemplate.get().get(new HashMap<String, Object>() {
						{
							put("ballDefinition", ballDefinition);
							// put("subPathDefinitions", Properties.getValue(entity, "subPathDefinitions"));
						}
					});

					balls.add(ball);

				}

				final Path pathValue = path.get();
				final PathTraversal pathTraversal = new PathTraversal(pathValue, 0, 0);

				pathTraversal.getDistanceFromOrigin(); // so that it is calculated, and propagated when segment split

				final float pathLength = new PathTraversal(pathValue, pathValue.getPoints().size() - 1).getDistanceFromOrigin();

				if (logger.isDebugEnabled())
					logger.debug("pathLength: " + pathLength);

				HashMap<String, Object> properties = new HashMap<String, Object>() {
					{
						put("pathTraversal", pathTraversal);
						put("pathLength", pathLength);
						put("pathEntity", new ReferenceProperty<Object>("pathEntity", entity));
					}
				};
				properties.putAll(pathProperties.get());
				final Entity segment = segmentTemplate.get().get(properties);
				
//				final Entity segment = templateProvider.getTemplate("zombierockers.entities.segment").instantiate(null, properties);

				messageQueue.enqueue(new Message("spawnedSegment", new PropertiesMapBuilder() {
					{
						property("path", pathValue);
						property("balls", balls);
						property("segment", segment);
					}
				}.build()));
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("fired", "fired");
				propertyRef("ballTemplate", "ballTemplate");
				propertyRef("segmentTemplate", "segmentTemplate");
				propertyRef("ballDefinitions", "ballDefinitions");
				propertyRef("spawnQuantity", "spawnQuantity");
				propertyRef("pathProperties", "pathProperties");
				propertyRef("path", "path");
			}
		});

	}
}
