package dassault.entities


import static org.lwjgl.opengl.GL11.*;


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 


builder.entity {
	
	tags("blasterbullet")
	
	property("position", utils.vector(0,0))
	property("newPosition", parameters.position)
	property("moveDirection", parameters.moveDirection)
	property("speed", parameters.speed)
	property("damage", parameters.damage)
	property("owner", parameters.owner)
	
	component(new SuperMovementComponent("movementComponent")) {
		propertyRef("position", "newPosition")
		propertyRef("maxVelocity", "speed")
	}
	
	component(utils.components.genericComponent(id:"updateMoveDirection", messageId:"update"){ message ->
		def moveDirection = entity.moveDirection
		def desiredDirection = moveDirection.normalise().scale(0.01f)
		entity."movementComponent.force".add(desiredDirection)
	})
	
	property("collisionDetected", false)
	property("bounds", utils.rectangle(-2, -2, 4, 4))
	
	component(utils.components.genericComponent(id:"updatePositionHandler", messageId:"update"){ message ->
		
		if (entity.collisionDetected)
			return
		
		// update collision bounds
		entity.bounds.centerX = entity.newPosition.x
		entity.bounds.centerY = entity.newPosition.y 
		
		def obstacles = entity.root.getEntities(Predicates.and(EntityPredicates.withAnyTag("obstacle", "droid"), // 
				{ collidable -> collidable.bounds.intersects(entity.bounds) } as Predicate, // 
				{ collidable -> entity.owner != collidable } as Predicate))
		
		if (obstacles.empty) {
			entity.position = entity.newPosition
			return
		}
		
		
		// collides, should do something about it
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("bulletCollision"){ newMessage ->
			newMessage.bullet = entity
			newMessage.target = obstacles[0]
		})
		
		entity.collisionDetected = true
		
		return
		
	})
	
	component(utils.components.genericComponent(id:"bulletCollisionHandler", messageId:"bulletCollision"){ message ->
		
		// call hit
		
		if (entity != message.bullet)
			return 
		
		def target = message.target
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("droidHitted"){ newMessage ->
			newMessage.bullet = entity
			newMessage.target = target
			newMessage.damage = entity.damage
		})
		
		def startColor = utils.color(1f, 1f, 1f, 1f)
		def endColor = utils.color(1f, 1f, 1f, 0.2f)
		
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(30, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 400, 5.0f, 20f, 60f, 1f, startColor, endColor) 
			newMessage.layer = 1
		})
		
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
		
	})
	
	component(new ImageRenderableComponent("renderBullet")) {
		propertyRef("position", "position")
		property("image", utils.resources.image("blasterbullet"))
		propertyRef("direction", "moveDirection")
		property("layer", -5)
		property("size", utils.vector(0.6f, 0.6f))
		property("color", utils.color(1f,1,1,1f))
	}
	
}
