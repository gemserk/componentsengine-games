package dudethatsmybullet.entities


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 


builder.entity("ship") {
	
	tags("ship","nofriction")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	property("desiredDirection",utils.vector(0,0))
	
	property("radius",20)
	
	
	property("target",utils.vector(1,0))
	property("bounds",parameters.bounds)
	
	component(utils.components.genericComponent(id:"directionToForceComponent", messageId:["update"]){ message ->
		entity.direction = entity.target.copy().sub(entity.position)
		
		def desiredDirection = entity.desiredDirection
		if(desiredDirection.lengthSquared() > 0){
			entity."movement.force".add(desiredDirection.copy().normalise().scale(0.1f))
			desiredDirection.set(0,0)
		}else {
			entity."movement.force".add(entity."movement.velocity".copy().negate().scale(0.01f))
		}
		
	})
	
	component(new SuperMovementComponent("movement")){
		property("maxVelocity", (float)(300/1000))
		propertyRef("position", "position")
		
	}
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	component(utils.components.genericComponent(id:"lookAtHandler", messageId:["lookAt"]){ message ->
		def target = utils.vector(message.x,message.y)
		entity.target = target
	})
	
	
	component(utils.components.genericComponent(id:"moveHandler", messageId:["move"]){ message ->
		def moveDirection = message.target
		
		entity.desiredDirection.add(moveDirection)
	})
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
	
}
