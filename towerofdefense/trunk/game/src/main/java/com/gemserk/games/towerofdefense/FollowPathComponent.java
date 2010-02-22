package com.gemserk.games.towerofdefense;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.google.inject.Inject;

public class FollowPathComponent extends ReflectionComponent {

	@Inject
	@Root	Entity rootEntity;
	
	PropertyLocator<String> pathEntityIdProperty;
	
	PropertyLocator<String> pathProperty;
	
	PropertyLocator<Integer> pathIndexProperty;
	
	PropertyLocator<Vector2f> forceProperty;
	
	PropertyLocator<Vector2f> positionProperty;

	public FollowPathComponent(String id) {
		super(id);
		pathEntityIdProperty = Properties.property(id, "pathEntityId");
		pathProperty = Properties.property(id, "path");
		pathIndexProperty = Properties.property(id, "pathindex");
		forceProperty = Properties.property(id, "force");
		positionProperty = Properties.property(id, "position");
	}

	public void handleMessage(UpdateMessage updateMessage) {

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
		Entity pathEntity = rootEntity.getEntityById(pathEntityId);

		String pathPropertyString = pathProperty.getValue(entity);
		Path path = (Path) Properties.property(pathPropertyString).getValue(pathEntity);
		return path;
	}

}