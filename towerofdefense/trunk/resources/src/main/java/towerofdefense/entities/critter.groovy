package towerofdefense.entities;

import com.gemserk.componentsengine.messages.UpdateMessage;


import com.gemserk.componentsengine.commons.components.BarRendererComponent 
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.FollowPathComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import towerofdefense.components.CritterHitHandler 

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("health", utils.container(parameters.health,parameters.health))
	property("reward",parameters.reward)
	property("points",parameters.points)
	property("color",parameters.color)
	propertyRef("direction", "movement.velocity")
	
	property("explosionSound", utils.resources.sounds.sound("towerofdefense.sounds.explosion"))
	
	property("speed", (Float)(parameters.speed / 1000f))
	
	component(new SuperMovementComponent("movement")){
		property("velocity",parameters.direction.copy().scale(entity.speed))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "position")
	}
	
	component(new FollowPathComponent("followpath")){
		property("path", {entity.parent.path});
		property("pathindex", 0);
		propertyRef("force", "movement.force");
		propertyRef("position", "position");
	}
	
	if (parameters.image) {
		component(new ImageRenderableComponent("imagerenderer")) {
			property("image", parameters.image)
			propertyRef("color", "color")
			propertyRef("position", "position")
			propertyRef("direction", "direction")
			property("size", utils.vector(0.7f, 0.7f))
		}
	}
	
	component(new BarRendererComponent("healthRenderer")) {
		property("position", {entity.position.copy().sub(utils.vector(5f,15f))})
		property("emptyColor", utils.color(1.0f, 0.0f, 0.0f, 0.5f))
		propertyRef("container", "health")
		property("width", 15.0f)
		property("height", 2.0f)
	}
	
	if (parameters.rotationImage) {
		
		property("rotationValue", 0f)
		
		component(new IncrementValueComponent("rotator")) {
			propertyRef("value", "rotationValue")
			property("maxValue", 360f)
			property("increment", parameters.rotationSpeed ? (float)(parameters.rotationSpeed / 10f) : 0.2f)
		}
		
		component(new ImageRenderableComponent("rotationImageRender")) {
			property("image", parameters.rotationImage)
			propertyRef("color", "color")
			propertyRef("position", "position")
			property("direction", {utils.vector(1,0).add(entity.rotationValue)})
			property("size", utils.vector(0.7f, 0.7f))
		}
		
	}
	
	component(new CritterHitHandler("hithandler"))
	
	component(utils.components.genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		if(message.critter == entity) {
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
			messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
				newMessage.explosion = EffectFactory.explosionEffect(50, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 300, 10.0f, 30f, 80f, 1f) 
			})
			entity.explosionSound.play()
		}
	})
	
	component(utils.components.genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		if(message.critter == entity)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
	
	property("forwardForce",utils.vector(1,0))
	component(new ComponentFromListOfClosures("forwardForce",[ {UpdateMessage message ->
		def direction = entity."movement.velocity".copy().normalise()
		def maxVelocity = entity."movement.maxVelocity"
		
		def forwardForce = direction.scale((float)(maxVelocity/1500f))
		entity.forwardForce = forwardForce
		entity."movement.force".add(forwardForce)
	}
	]))
	
}

