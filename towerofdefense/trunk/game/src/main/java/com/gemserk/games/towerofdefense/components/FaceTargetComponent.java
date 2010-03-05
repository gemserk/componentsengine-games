package com.gemserk.games.towerofdefense.components;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;

public class FaceTargetComponent extends FieldsReflectionComponent {

	@EntityProperty(readOnly=true)
	Vector2f position;
	
	@EntityProperty
	Vector2f direction;

	@EntityProperty
	Float turnRate = 0.01f;

	@EntityProperty
	Entity targetEntity;
	
	public FaceTargetComponent(String id) {
		super(id);
	}

	public void handleMessage(UpdateMessage message) {

		if (targetEntity == null)
			return;

		Vector2f targetPosition = (Vector2f) Properties.property("position").getValue(targetEntity);
		Vector2f desiredDirection = targetPosition.copy().sub(position).normalise();
		
		int delta = message.getDelta();

		turnToAngle(delta, direction.getTheta(), desiredDirection.getTheta());
		
	}

	protected void turnToAngle(int delta, double currentAngle, double desiredAngle) {
		direction.setTheta(calculateNextAngle(delta, currentAngle, desiredAngle));
	}

	double calculateNextAngle(int delta, double currentAngle, double desiredAngle) {
		double theta = currentAngle;
		
		double diffAngle = new AngleUtils().angleDifference(currentAngle, desiredAngle);
		
		if (diffAngle > 0)
			// turn left
			theta = currentAngle + turnRate * delta;
		else 
			// turn right
			theta = currentAngle - turnRate * delta;
		
		// truncate to desiredAngle
		if (desiredAngle > currentAngle && desiredAngle < theta)
			theta = desiredAngle;
		else if (desiredAngle < currentAngle && desiredAngle > theta)
			theta = desiredAngle;
		
		return theta;
	}

}