package zombierockers.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 



builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("radius", parameters.radius);
	
	component(new SuperMovementComponent("movement")){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("lineColor", utils.color(0,0,0,0))
		property("fillColor", parameters.color)
	}
	
	//	component(new ImageRenderableComponent("imagerenderer")) {
	//		property("image", utils.resources.image("ship"))
	//		property("color", parameters.color)
	//		propertyRef("position", "position")
	//		propertyRef("direction", "direction")
	//	}
	component(new GenericHitComponent("bullethitComponent")){
		property("targetTag", "ball")
		property("predicate",{EntityPredicates.isNear(entity.position, entity.radius)})
		property("trigger", utils.custom.triggers.genericMessage("bulletHit") { 
			def source = message.source
			def targets = message.targets
			message.targets = [targets[0]]
		})
	}
	
	component(utils.components.genericComponent(id:"bulletHitHandler", messageId:["bulletHit"]){ message ->
		if(message.source != entity)
			return
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
}
