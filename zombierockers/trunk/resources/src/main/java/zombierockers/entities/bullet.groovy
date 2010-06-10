package zombierockers.entities

import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;



builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("ball",parameters.ball)
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("radius", {entity.ball.finalRadius});
	
	component(new SuperMovementComponent("movement")){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {entity.ball.animation.currentFrame})
		property("color",{entity.ball.color})
		propertyRef("position", "position")
		property("direction", utils.vector(0,-1))
	}
	
	component(new GenericHitComponent("bullethitComponent")){
		property("targetTag", "ball")
		property("predicate",{Predicates.and(EntityPredicates.isNear(entity.position, (float)entity.radius+1),{ball -> ball.alive} as Predicate)})
		property("trigger", utils.custom.triggers.genericMessage("bulletHit") { 
			def source = message.source
			def target = message.targets[0]
			message.targets = [target]
			log.info("Bullet hit ball bullet.id: $source.id - bullet.color: $source.ball.color - bullet.ball.id: $source.ball.id - targets.id: $target.id - target.color: $target.color")
		})
		
	}
	
	component(utils.components.genericComponent(id:"bulletHitHandler", messageId:["bulletHit"]){ message ->
		if(message.source != entity)
			return
		
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.removeEntity(entity))
	})
}
