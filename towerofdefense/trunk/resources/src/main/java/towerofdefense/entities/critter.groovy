package towerofdefense.entities;

import com.gemserk.componentsengine.messages.RemoveEntityMessage 

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("health", parameters.health)
	propertyRef("direction", "movement.velocity")
	
	component("movement"){
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component("followpath"){
		property("pathEntityId", parameters.pathEntityId);
		property("path", parameters.pathProperty);
		property("pathindex", 0);
		propertyRef("force", "movement.force");
		propertyRef("position", "position");
	}
	
	component("imagerenderer")
	property("image", image("towerofdefense.images.critter1"))
	property("color", parameters.color)
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def entity = message.entity
		if (message.targets.contains(entity)) {
			println "health: $entity.health"
			entity.health.remove(message.damage)
			if (entity.health.isEmpty())
				messageQueue.enqueue(new RemoveEntityMessage(entity))
		}
		
	}
	
}

