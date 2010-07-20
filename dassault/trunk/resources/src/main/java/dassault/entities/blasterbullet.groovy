package dassault.entities


import static org.lwjgl.opengl.GL11.*;


import com.gemserk.commons.collisions.EntityCollidableImpl 
import com.gemserk.commons.slick.geom.ShapeUtils;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 
import com.google.common.collect.Collections2;


builder.entity {
	
	tags("bullet", "blasterbullet", "collidable")
	
	property("position", parameters.position)
	property("moveDirection", parameters.moveDirection)
	property("speed", parameters.speed)
	property("damage", parameters.damage)
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
		entity.collidable.aabb.setCenter(entity.position.x, entity.position.y)
	})
	
	component(utils.components.genericComponent(id:"updateCollisionsHandler", messageId:"update"){ message ->

		def collisionTree = entity.collidable.quadTree
		
		if (collisionTree == null)
			return
			
		def collidables = collisionTree.getCollidables(entity.collidable)
		
		collidables = Collections2.filter(collidables, Predicates.and({collidable -> entity != collidable.entity } as Predicate, //
				{ collidable -> entity.owner != collidable.entity } as Predicate,//
				{ collidable -> collidable.entity != null } as Predicate,//
				{ collidable -> entity.collidable.aabb.collide(collidable.aabb) } as Predicate, // 
				{ collidable -> new ShapeUtils(collidable.entity.bounds).collides(entity.bounds) } as Predicate, //
				{ collidable -> !collidable.entity.tags.contains("bullet") } as Predicate))
		
		entity.collisions = new ArrayList(collidables)
		
		// trigger collision detected! ? source = entity, targets = collisions, 
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
