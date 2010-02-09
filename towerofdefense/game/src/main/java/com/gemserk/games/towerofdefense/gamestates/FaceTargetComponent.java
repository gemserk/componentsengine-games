package com.gemserk.games.towerofdefense.gamestates;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.towerofdefense.components.Component;

public class FaceTargetComponent extends Component {

	public FaceTargetComponent(String name) {
		super(name);
	}

	@Override
	public void update(Entity entity, int delta) {

		Entity targetEntity = (Entity) Properties.property(id, "targetEntity")
				.getValue(entity, null);

		if (targetEntity == null)
			return;

		Vector2f position = (Vector2f) Properties.property("position")
				.getValue(entity);
		Vector2f targetPosition = (Vector2f) Properties.property("position")
				.getValue(targetEntity);

		Vector2f newDirection = targetPosition.copy().sub(position).normalise();
		Properties.property("direction").setValue(entity, newDirection);

	}
}