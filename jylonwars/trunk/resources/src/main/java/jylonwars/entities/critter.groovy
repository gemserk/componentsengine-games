package jylonwars.entities

import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.commons.components.BarRendererComponent 
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.FollowPathComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates;

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("color",parameters.color)
	propertyRef("direction", "movement.velocity")
	
	property("speed", parameters.speed)
	
	component(new SuperMovementComponent("movement")){
		property("velocity",utils.vector(entity.speed,0))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "position")
	}
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", parameters.image)
		propertyRef("color", "color")
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		property("size", utils.vector(0.7f, 0.7f))
	}
	
	component(new ComponentFromListOfClosures("steering",[ {UpdateMessage message ->
		def target = entity.parent.getEntities(EntityPredicates.withAllTags("ship")).first();
		
		if(target == null)
		return
		
		def direction = target.position.copy().sub(entity.position).normalise()
		
		entity."movement.force".add(direction.scale(1))
	}
	]))
	
	
	component(utils.components.genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def sourceEntity = message.source
		
		if (!sourceEntity.tags.contains("bullet"))
			return;
		
		if (message.targets.contains(entity)) {
			def deadMessage = new GenericMessage("critterdead")
			deadMessage.critter = entity
			
			messageQueue.enqueue(deadMessage)
		}
		
	})
	
}

