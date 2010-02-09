package com.gemserk.games.todh.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class FollowEntityComponent extends Component {

	PropertyLocator<Vector2f> directionProperty = property("movement.direction");

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Entity> targetProperty = property("targetEntity");

	public FollowEntityComponent(String name) {
		super(name);
	}

	@Override
	public void update(Entity entity, int delta) {

		Entity targetEntity = targetProperty.getValue(entity);

		Vector2f position = positionProperty.getValue(entity);
		Vector2f targetPosition = positionProperty.getValue(targetEntity);

		Vector2f direction = targetPosition.copy().sub(position);
		direction.normalise();

		directionProperty.setValue(entity, direction);
	}

}