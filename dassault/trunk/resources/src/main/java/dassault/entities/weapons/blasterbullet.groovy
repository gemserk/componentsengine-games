package dassault.entities.weapons

import com.gemserk.commons.collisions.EntityCollidableImpl 
import com.gemserk.commons.slick.geom.ShapeUtils;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.render.SlickImageRenderObject 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 
import com.google.common.collect.Collections2 
import org.newdawn.slick.Color 

builder.entity(entityName ?: "blasterbullet-${Math.random()}") {
	
	tags("bullet", "blasterbullet")
	
	property("position", parameters.position)
	property("moveDirection", parameters.moveDirection)
	property("speed", parameters.speed)
	property("damage", parameters.damage)
	
	property("player", parameters.player)
	property("owner", parameters.owner)
	
	component(new SuperMovementComponent("movementComponent")) {
		propertyRef("position", "position")
		propertyRef("maxVelocity", "speed")
	}
	
	component(utils.components.genericComponent(id:"updateMoveDirection", messageId:"update"){ message ->
		def moveDirection = entity.moveDirection
		def desiredDirection = moveDirection.normalise().scale(0.01f)
		entity."movementComponent.force".add(desiredDirection)
	})
	
	// collidable component
	
	property("collisionDetected", {!entity.collisions.isEmpty()})
	property("bounds", utils.rectangle(-2, -2, 4, 4))
	property("collisions", [])
	property("collidable", new EntityCollidableImpl(entity, new ShapeUtils(entity.bounds).getAABB() ))
	
	component(utils.components.genericComponent(id:"updateBoundsHandler", messageId:"update"){ message ->
		entity.bounds.centerX = entity.position.x
		entity.bounds.centerY = entity.position.y 
		
		entity.collidable.entity = entity
		entity.collidable.setCenter(entity.position.x, entity.position.y)
		entity.collidable.update()
	})
	
	component(utils.components.genericComponent(id:"updateCollisionsHandler", messageId:"update"){ message ->
		
		def collisionTree = entity.collidable.quadTree
		
		if (entity.collidable.outside) {
			utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
				newMessage.bullet = entity
			})
			return
		}
		
		if (collisionTree == null)
			return
		
		def collidables = collisionTree.getCollidables(entity.collidable)
		
		collidables = Collections2.filter(collidables, Predicates.and({collidable -> entity != collidable.entity } as Predicate, //
		{ collidable -> entity.owner != collidable.entity } as Predicate,//
		{ collidable -> collidable.entity != null } as Predicate,//
		{ collidable -> entity.collidable.aabb.collide(collidable.aabb) } as Predicate, // 
		{ collidable -> new ShapeUtils(collidable.entity.bounds).collides(entity.bounds) } as Predicate, //
		{ collidable -> collidable.entity.tags.contains("collidable") } as Predicate))
		
		entity.collisions = new ArrayList(collidables)
		
	})
	
	component(utils.components.genericComponent(id:"collisionDetectedHandler", messageId:"collisionDetected"){ message ->
		if (entity != message.target)
			return 
		utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
			newMessage.bullet = entity
		})
	})
	
	//
	
	component(utils.components.genericComponent(id:"hitWhenCollisionDetected", messageId:"update"){ message ->
		if (!entity.collisionDetected)
			return
		
		def collisions = entity.collisions
		def target = collisions[0].entity
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("collisionDetected"){ newMessage ->
			newMessage.bullet = entity
			newMessage.target = target
			newMessage.damage = entity.damage
		})
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
			newMessage.bullet = entity
		})
	})
	
	component(utils.components.genericComponent(id:"bulletDeadHandler", messageId:"bulletDead"){ message ->
		if (entity != message.bullet)
			return
		
		def playerColor = entity.player.color
			
		def startColor = utils.color(playerColor.r, playerColor.g, playerColor.b, 1f)
		def endColor = utils.color(playerColor.r, playerColor.g, playerColor.b, 0.2f)
		
		def position = entity.position
		
		entity.collidable.remove()
		
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(30, (int) position.x, (int) position.y, 0f, 360f, 400, 5.0f, 20f, 60f, 1f, startColor, endColor) 
			newMessage.layer = 1
		})
		
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
	
	property("headImage", utils.resources.image("blasterbullet_head"))
	property("bodyImage", utils.resources.image("blasterbullet_body"))
	property("auraImage", utils.resources.image("blasterbullet_aura"))
	
	component(utils.components.genericComponent(id:"bulletRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def player = entity.player
		
		def angle = (float) entity.moveDirection.theta
		
		def size = utils.vector(0.6f, 0.6f)
		
		def layer = -5
		def color = player.color
		
		renderer.enqueue(new SlickImageRenderObject(layer-2, entity.auraImage, position, //
				size, angle, Color.black))
		renderer.enqueue(new SlickImageRenderObject(layer-1, entity.bodyImage, position, //
				size, angle, color))
		renderer.enqueue(new SlickImageRenderObject(layer, entity.headImage, position, //
				size, angle, Color.white))
		
	})
	
}
