package dassault.entities

import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("blasterbullet")
	
	property("position", utils.vector(0,0))
	property("newPosition", parameters.position)
	property("moveDirection", parameters.moveDirection)
	property("speed", parameters.speed)
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
		
		if (!obstacles.empty) {
			
			// collides, should do something about it
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("bulletCollision"){ newMessage ->
				newMessage.bulletId = entity.id
				newMessage.obstacles = obstacles
			})
			
			entity.collisionDetected = true
			
			return
		}
		
		entity.position = entity.newPosition
		
	})
	
	component(utils.components.genericComponent(id:"bulletCollisionHandler", messageId:"bulletCollision"){ message ->
		
		// call hit
	
		if (entity.id != message.bulletId)
			return 
			
		def obstacles = message.obstacles
		def firstObstacle = obstacles[0]
	
		println "bullet collided with ${firstObstacle.id}"
		
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
		
	})
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def size = 1.0f
		def layer = 0
		def color = utils.color(1f,1f,1f,1f)
		def shape = utils.rectangle(-2, -2, 4, 4)
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			g.translate((float) position.x, (float)position.y)
			g.scale(size, size)
			g.fill(shape)
			g.popTransform()
		}))
		
	})
}
