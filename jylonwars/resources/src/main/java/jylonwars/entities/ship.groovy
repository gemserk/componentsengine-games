package jylonwars.entities
import com.gemserk.componentsengine.messages.UpdateMessage;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 


builder.entity("ship") {
	
	tags("ship","nofriction")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	property("desiredDirection",utils.vector(0,0))
	
	component(new ComponentFromListOfClosures("directionToForceComponent",[ {UpdateMessage message ->
		entity.direction = entity.target.copy().sub(entity.position)
		
		def desiredDirection = entity.desiredDirection
		if(desiredDirection.lengthSquared() > 0){
			entity."movement.force".add(desiredDirection.copy().normalise().scale(0.1f))
			desiredDirection.set(0,0)
		}else {
			entity."movement.force".add(entity."movement.velocity".copy().negate().scale(0.01f))
		}
		
	}
	]))
	
	component(new SuperMovementComponent("movement")){
		property("maxVelocity", (float)(300/1000))
		propertyRef("position", "position")
	}
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(0,0,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	component(utils.components.genericComponent(id:"lookAtHandler", messageId:["lookAt"]){ message ->
		def target = utils.vector(message.x,message.y)
		entity.target = target
	})
	
	
	component(utils.components.genericComponent(id:"moveHandler", messageId:["move.left","move.right","move.up","move.down"]){ message ->
		def moveDirection
		switch(message.id){
			case "move.left":
			moveDirection = utils.vector(-1,0)
			break;
			case "move.right":
			moveDirection = utils.vector(1,0)
			break;
			case "move.up":
			moveDirection = utils.vector(0,-1)
			break;
			case "move.down":
			moveDirection = utils.vector(0,1)
			break;
		}
		
		entity.desiredDirection.add(moveDirection)
		
		
	})
}
