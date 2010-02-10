package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class FollowPathComponent extends TodComponent {

	PropertyLocator<Vector2f> directionProperty = property("movement.direction");

	PropertyLocator<ArrayList<Vector2f>> pathProperty = property("followpath.path");

	PropertyLocator<Integer> currentTargetProperty = property("followpath.currentTarget");

	PropertyLocator<Vector2f> positionProperty = property("position");

	public FollowPathComponent(String name) {
		super(name);
	}

	@Override
	public void onAdd(Entity entity) {
		super.onAdd(entity);

		currentTargetProperty.setValue(entity, 0);
		pathProperty.setValue(entity, new ArrayList<Vector2f>());
	}

	@Override
	public void update(Entity entity, int delta) {

		Integer currentTargetIndex = currentTargetProperty.getValue(entity);
		ArrayList<Vector2f> path = pathProperty.getValue(entity);

		if (path.size() == 0)
			return;

		Vector2f target = path.get(currentTargetIndex);
		Vector2f position = positionProperty.getValue(entity);

		if (position.distance(target) < 5.0f) {
			currentTargetIndex++;
		}

		if (currentTargetIndex >= path.size()) {
			currentTargetIndex = 0;
			positionProperty.setValue(entity, path.get(currentTargetIndex)
					.copy());
		}

		currentTargetProperty.setValue(entity, currentTargetIndex);

		Vector2f direction = target.copy().sub(position);
		direction.normalise();

		directionProperty.setValue(entity, direction);
	}

}