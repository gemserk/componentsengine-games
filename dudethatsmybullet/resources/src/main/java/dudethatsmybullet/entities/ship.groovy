package dudethatsmybullet.entities


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 


builder.entity("ship") {
	
	tags("ship","nofriction","hittable")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	property("desiredDirection",utils.vector(0,0))
	
	property("radius",20)
	
	
	property("target",utils.vector(1,0))
	property("bounds",parameters.bounds)
	
	
	property("rotationValue",0f)
	
	component(new IncrementValueComponent("rotator")) {
		propertyRef("value", "rotationValue")
		property("maxValue", 360f)
		property("increment", 0.2f)
	}
	
	
	component(utils.components.genericComponent(id:"directionToForceComponent", messageId:["update"]){ message ->
		def distanceVector = entity.target.copy().sub(entity.position)
		
		
		if(distanceVector.lengthSquared() > 0){
			entity."movement.force".add(distanceVector.copy().normalise().scale(0.1f))
		}else {
			entity."movement.force".add(entity."movement.velocity".copy().negate().scale(0.01f))
		}
		
	})
	
	component(new SuperMovementComponent("movement")){
		property("maxVelocity", (float)(500/1000))
		propertyRef("position", "position")
		
	}
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", {utils.vector(1,0).add(entity.rotationValue)})
	}
	
	
	component(utils.components.genericComponent(id:"lookAtHandler", messageId:["lookAt"]){ message ->
		def target = utils.vector(message.x,message.y)
		entity.target = target
	})
	
	
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
	
}
