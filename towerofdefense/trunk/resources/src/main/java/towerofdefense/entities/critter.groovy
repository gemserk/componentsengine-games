package towerofdefense.entities;

import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.RemoveEntityMessage 

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("health", parameters.health)
	property("reward",parameters.reward)
	property("points",parameters.points)
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
			entity.health.remove(message.damage)
			if (entity.health.isEmpty()){
				messageQueue.enqueue(utils.genericMessage("critterdead"){ deadMessage ->
					deadMessage.critter = entity
				})
			}

			entity.color.a = entity.health.percentage
			println "health: $entity.health"
		}
		
	}
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		def entity = message.entity
		if(message.critter == entity)
			messageQueue.enqueue(new RemoveEntityMessage(entity))
	}
	
}

