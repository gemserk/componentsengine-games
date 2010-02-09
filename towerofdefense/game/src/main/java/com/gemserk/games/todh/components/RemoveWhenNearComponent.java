package com.gemserk.games.todh.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.world.World;

public class RemoveWhenNearComponent extends Component {

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Entity> targetProperty = property("targetEntity");

	PropertyLocator<World> worldProperty = property("world");

	public RemoveWhenNearComponent(String name) {
		super(name);
	}

	@Override
	public void update(Entity entity, int delta) {

		Entity targetEntity = targetProperty.getValue(entity);
		Vector2f targetPosition = positionProperty.getValue(targetEntity);
		Vector2f position = positionProperty.getValue(entity);

		World world = worldProperty.getValue(entity);

		if (position.distance(targetPosition) < 5.0f)
			world.queueRemoveEntity(entity);

	}

}