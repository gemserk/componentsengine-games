package dudethatsmybullet.entities


import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 

builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("collisionDistance", parameters.radius);
	property("color",parameters.color)
	//property("collisionLine",new Line)
	
	component(new SuperMovementComponent("movement")){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", parameters.image)
		propertyRef("color", "color")
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
//	
//	component(utils.components.genericComponent(id:"collisionLineGenerator", messageId:["update"]){ message ->
//		def origin = entity.position
//		def end = origin.copy().add(entity."movement.velocity".copy().scale(-message.delta))
//		
//		def line = new Line(origin,end)
//		
//		entity.collisionLine = line
//		
//	})
	
	
	component(new GenericHitComponent("bullethit")){
		property("targetTag", "hittable")
		property("predicate",{EntityPredicates.isNear(entity.position, entity.collisionDistance)})
		property("trigger", utils.custom.triggers.genericMessage("hit") { 
			def source = message.source
			def damage = source.damage
			message.damage = damage;
			
			def targets = message.targets
			message.targets = [targets[0]]
		})
	}
	
	component(utils.components.genericComponent(id:"hithandler", messageId:"hit"){ message ->
		if (message.source == entity){
			log.info("Bullet hitted target $entity.id - ${message.targets[0]}")
			messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
				newMessage.explosion =EffectFactory.explosionEffect(100, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f, entity.color, entity.color)
				newMessage.layer = 1
			})
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
		}
	})
	
}
