package towerofdefense.entities;
import com.gemserk.games.towerofdefense.GenericHitComponent;


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;


import com.gemserk.componentsengine.commons.components.SuperMovementComponent;

import com.gemserk.componentsengine.messages.ChildMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates 

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
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("towerofdefense.images.blasterbullet"))
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	component(new GenericHitComponent("bullethit")){
		property("targetTag", "critter")
		property("predicate",{EntityPredicates.isNear(entity.position, entity.radius)})
		property("messageBuilder", utils.custom.messageBuilderFactory.messageBuilder("hit") { 
			def source = message.source
			def damage = source.damage
			message.damage = damage;
			
			def targets = message.targets
			message.targets = [targets[0]]
		})
	}
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		if (message.getProperty("source").get() == entity)
			messageQueue.enqueue(ChildMessage.removeEntity(entity,"world"))
	}
	
	
}
