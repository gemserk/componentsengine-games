package towerofdefense.entities;
import com.gemserk.games.towerofdefense.FollowPathComponent;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.messages.ChildMessage;

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("health", parameters.health)
	property("reward",parameters.reward)
	property("points",parameters.points)
	propertyRef("direction", "movement.velocity")
	
	component(new SuperMovementComponent("movement")){
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new FollowPathComponent("followpath")){
		property("pathEntityId", parameters.pathEntityId);
		property("path", parameters.pathProperty);
		property("pathindex", 0);
		propertyRef("force", "movement.force");
		propertyRef("position", "position");
	}
	
	component(new ImageRenderableComponent("imagerenderer"))
	property("image", utils.resources.image("towerofdefense.images.critter1"))
	property("color", parameters.color)
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def sourceEntity = message.source
		
		if (!sourceEntity.tags.contains("bullet"))
			return;
		
		if (entity.health.isEmpty())
			return;
		
		if (message.targets.contains(entity)) {
			entity.health.remove(message.damage)
			if (entity.health.isEmpty()){
				messageQueue.enqueue(utils.genericMessage("critterdead"){ deadMessage ->
					deadMessage.critter = entity
				})
			}
			
			entity.color.a = entity.health.percentage
		}
		
	}
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		if(message.critter == entity)
			messageQueue.enqueue(ChildMessage.removeEntity(entity,"world"))
	}
	
	genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		if(message.critter == entity)
			messageQueue.enqueue(ChildMessage.removeEntity(entity,"world"))
	}
	
}

