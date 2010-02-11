/**
 * 
 */
package com.gemserk.games.towerofdefense;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.world.World;
import com.google.inject.Inject;

public class FollowPathComponent extends ReflectionComponent {

	@Inject
	World world;

	PropertyLocator<String> pathEntityIdProperty = Properties.property("followpath", "pathEntityId");

	PropertyLocator<String> pathProperty = Properties.property("followpath", "path");

	PropertyLocator<Integer> pathIndexProperty = Properties.property("followpath", "pathindex");

	PropertyLocator<Vector2f> forceProperty = Properties.property("followpath", "force");

	PropertyLocator<Vector2f> positionProperty = Properties.property("followpath", "position");

	public FollowPathComponent(String id) {
		super(id);
	}

	public void handleMessage(UpdateMessage updateMessage) {
		Entity entity = updateMessage.getEntity();

		Path path = getPath(entity);

		Vector2f position = positionProperty.getValue(entity);
		Integer pathIndex = pathIndexProperty.getValue(entity);

		int nextPathIndex = path.getNextIndex(position, pathIndex);
		pathIndexProperty.setValue(entity, nextPathIndex);

		Vector2f nextPosition = path.getPoint(nextPathIndex);

		Vector2f direction = nextPosition.copy().sub(position).normalise();
		Vector2f currentForce = forceProperty.getValue(entity).copy();

		currentForce.add(direction.scale(1000f));

		forceProperty.setValue(entity, currentForce);
	}

	private Path getPath(Entity entity) {
		String pathEntityId = pathEntityIdProperty.getValue(entity);
		Entity pathEntity = world.getEntityById(pathEntityId);

		String pathPropertyString = pathProperty.getValue(entity);
		Path path = (Path) Properties.property(pathPropertyString).getValue(pathEntity);
		return path;
	}

}