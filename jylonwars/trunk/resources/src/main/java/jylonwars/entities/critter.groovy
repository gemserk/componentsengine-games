package jylonwars.entities

import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates;

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("color",parameters.color)
	propertyRef("direction", "movement.velocity")
	
	property("explosionSound",utils.resources.sounds.sound("explosion"))
	property("bounds",parameters.bounds)
	
	property("speed", parameters.speed)
	
	component(new SuperMovementComponent("movement")){
		property("velocity",utils.vector(entity.speed,0))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "position")
	}
	
	
	
	
	component(utils.components.genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def sourceEntity = message.source
		
		if (!sourceEntity.tags.contains("bullet"))
			return;
		
		if (message.targets.contains(entity)) {
			def deadMessage = new GenericMessage("critterdead")
			deadMessage.critter = entity
			
			messageQueue.enqueue(deadMessage)
			entity.explosionSound.play(1.0f, 0.01f)
		}
		
		
	})
	
}

