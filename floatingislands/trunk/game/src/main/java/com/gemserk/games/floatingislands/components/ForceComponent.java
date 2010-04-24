package com.gemserk.games.floatingislands.components;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.messages.UpdateMessage;

public class ForceComponent extends FieldsReflectionComponent {
	
	@EntityProperty
	Vector2f force;

	@EntityProperty
	Vector2f acceleration;
	
	@EntityProperty
	float mass;
	
	public ForceComponent(String id) {
		super(id);
	}

	public void handleMessage(UpdateMessage message) {
		int delta = message.getDelta();
		
		float deltaf = delta / 1000.0f;
		
		Vector2f acceleration = this.acceleration.copy().scale(deltaf);
		Vector2f gravityForce = acceleration.copy().scale(1f/mass);
						
		force.add(gravityForce);
	}

}
