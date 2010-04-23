package jylonwars.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.games.jylonwars.WorldBoundsComponent 

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("color",parameters.color)
	propertyRef("direction", "movement.velocity")
	
	property("explosionSound",utils.resources.sounds.sound("explosion"))
	property("bounds",parameters.bounds)
	
	property("speed", parameters.speed)
	
	property("dead",false)
	
	component(new SuperMovementComponent("movement")){
		property("velocity",utils.vector(entity.speed,0))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "position")
	}
	
	
	
	
	component(utils.components.genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def sourceEntity = message.source
		
		if(entity.dead)
			return
		
		if (!sourceEntity.tags.contains("bullet"))
			return;
		
		if (message.targets.contains(entity)) {
			entity.dead = true
			def deadMessage = new GenericMessage("critterdead")
			deadMessage.critter = entity
			
			messageQueue.enqueue(deadMessage)
		}
		
		
	})
	
	component(utils.components.genericComponent(id:"critterdeadhandler", messageId:"critterdead"){ message ->
		if(message.critter != entity)
			return
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(100, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f) 
		})
		
		entity.explosionSound.play(1.0f, 0.01f)
		
	})
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
	
}

