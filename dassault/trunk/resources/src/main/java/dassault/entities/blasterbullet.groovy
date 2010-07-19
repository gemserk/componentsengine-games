package dassault.entities


import static org.lwjgl.opengl.GL11.*;


import com.gemserk.commons.slick.geom.ShapeUtils;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 


builder.entity {
	
	tags("blasterbullet", "collidable")
	
	property("position", parameters.position)
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
		
		def obstacles = entity.root.getEntities(Predicates.and(EntityPredicates.withAnyTag("collidable"), // 
				{ collidable -> new ShapeUtils(collidable.bounds).collides(entity.bounds) } as Predicate, // 
				{ collidable -> entity.owner != collidable } as Predicate, // 
				{ collidable -> entity != collidable && !collidable.tags.contains("blasterbullet") } as Predicate))
		
		if (obstacles.empty) {
			entity.position = entity.newPosition
			return
		}
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("bulletCollision"){ newMessage ->
			newMessage.bullet = entity
			newMessage.target = obstacles[0]
		})
		
		entity.collisionDetected = true
	})
	
	component(utils.components.genericComponent(id:"bulletCollisionHandler", messageId:"bulletCollision"){ message ->
		
		// call hit
		
		if (entity != message.bullet)
			return 
		
		def target = message.target
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("collidableHitted"){ newMessage ->
			newMessage.bullet = entity
			newMessage.target = target
			newMessage.damage = entity.damage
		})
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
			newMessage.bullet = entity
		})
	})
	
	component(utils.components.genericComponent(id:"collidableHittedHandler", messageId:"collidableHitted"){ message ->
		if (entity != message.target)
			return 
		utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
			newMessage.bullet = entity
		})
	})
	
	component(utils.components.genericComponent(id:"bulletDeadHandler", messageId:"bulletDead"){ message ->
		if (entity != message.bullet)
			return
		
		def startColor = utils.color(1f, 1f, 1f, 1f)
		def endColor = utils.color(1f, 1f, 1f, 0.2f)
		
		def position = entity.position
		
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(30, (int) position.x, (int) position.y, 0f, 360f, 400, 5.0f, 20f, 60f, 1f, startColor, endColor) 
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
