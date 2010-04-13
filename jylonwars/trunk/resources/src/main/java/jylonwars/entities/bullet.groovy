package jylonwars.entities;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage;
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
		property("image", parameters.image)
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	component(new GenericHitComponent("bullethit")){
		property("targetTag", "critter")
		property("predicate",{EntityPredicates.isNear(entity.position, entity.radius)})
		property("trigger", utils.custom.triggers.genericMessage("hit") { 
			def source = message.source
			def damage = source.damage
			message.damage = damage;
			
			def targets = message.targets
			message.targets = [targets[0]]
		})
	}
	
	component(utils.components.genericComponent(id:"hithandler", messageId:"hit"){ message ->
		if (message.getProperty("source").get() == entity)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
	
}
