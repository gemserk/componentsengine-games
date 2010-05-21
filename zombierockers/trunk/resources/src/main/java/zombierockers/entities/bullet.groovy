package zombierockers.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 



builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("collisionDistance", parameters.radius);
	
	component(new SuperMovementComponent("movement")){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		property("radius", parameters.radius)
		property("lineColor", utils.color(0,0,0,0))
		property("fillColor", parameters.color)
	}
	
//	component(new ImageRenderableComponent("imagerenderer")) {
//		property("image", utils.resources.image("ship"))
//		property("color", parameters.color)
//		propertyRef("position", "position")
//		propertyRef("direction", "direction")
//	}
	
}
