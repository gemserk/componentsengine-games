package towerofdefense.entities;
import com.gemserk.games.towerofdefense.HitComponent;


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;


import com.gemserk.componentsengine.commons.components.SuperMovementComponent;

import com.gemserk.componentsengine.messages.ChildMessage;

builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("radius", parameters.radius);
	
	component(new SuperMovementComponent("movement")){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("imagerenderer"))
	property("image", utils.resources.image("towerofdefense.images.bullet"))
	property("color", parameters.color)
	
	component(new HitComponent("bullethit")){
		property("targetTag", "critter")
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("messageBuilder", utils.custom.messageBuilderFactory.messageBuilder("hit") { 
			def source = message.source
			def damage = source.damage
			message.damage = damage;
		})
	}
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		if (message.getProperty("source").get() == entity)
			messageQueue.enqueue(ChildMessage.removeEntity(entity,"world"))
	}
	
	
}
