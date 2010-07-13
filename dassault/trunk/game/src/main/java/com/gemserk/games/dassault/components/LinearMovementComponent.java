package com.gemserk.games.dassault.components;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.interpolators.Vector2fInterpolator;
import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;

public class LinearMovementComponent extends FieldsReflectionComponent {

	@EntityProperty(required = false)
	Vector2fInterpolator interpolator = null;

	@EntityProperty
	Vector2f position;

	public LinearMovementComponent(String id) {
		super(id);
	}

	@Handles
	public void moveTo(Message message) {
		String id = Properties.getValue(message, "entityId");
		
		if (!entity.getId().equals(id))
			return;
		
		if (isMoving())
			return;
		
		Integer time = Properties.getValue(message, "time");
		Vector2f target = Properties.getValue(message, "target");
		
		if (time == null) {
			position = target;
			return;
		}

		interpolator = new Vector2fInterpolator(time, position, target);
	}

	private boolean isMoving() {
		return interpolator != null;
	}

	@Handles
	public void update(Message message) {
		if (interpolator == null)
			return;
		
		int delta = (Integer)Properties.getValue(message, "delta");
		interpolator.update(delta);
		
		position = interpolator.getInterpolatedValue();
		
		if (!interpolator.isFinished())
			return;
		
		interpolator = null;
	}

}
