package com.gemserk.games.towerofdefense.components;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class FaceTargetComponent extends TodComponent {

	private PropertyLocator<Vector2f> directionProperty;

	private PropertyLocator<Entity> targetEntityProperty;

	private PropertyLocator<Vector2f> positionProperty;

	public FaceTargetComponent(String id) {
		super(id);
		directionProperty = Properties.property(id, "direction");
		positionProperty = Properties.property(id, "position");
		targetEntityProperty = Properties.property(id, "targetEntity");
	}

	@Override
	public void update(Entity entity, int delta) {

		Entity targetEntity = targetEntityProperty.getValue(entity, null);

		if (targetEntity == null)
			return;

		Vector2f position = positionProperty.getValue(entity);
		Vector2f targetPosition = (Vector2f) Properties.property("position").getValue(targetEntity);

		Vector2f newDirection = targetPosition.copy().sub(position).normalise();

		directionProperty.setValue(entity, newDirection);

	}
}