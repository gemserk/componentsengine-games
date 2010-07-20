package dassault.entities

import com.gemserk.commons.animation.interpolators.FloatInterpolator;
import com.gemserk.commons.collisions.EntityCollidableImpl 
import com.gemserk.commons.slick.geom.ShapeUtils 
import com.gemserk.componentsengine.commons.components.BarRendererComponent 
import com.gemserk.componentsengine.commons.components.CursorOverDetector;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.games.dassault.components.TransferComponent;
import com.gemserk.games.dassault.components.TransferRendererComponent;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2 
import org.newdawn.slick.Color 


builder.entity(entityName ?: "droid-${Math.random()}") {
	
	tags("droid", "nofriction", "collidable")
	
	// TODO: use owner instead of id
	property("ownerId", parameters.ownerId)
	
	property("position", parameters.position.copy())
	property("newPosition", parameters.position)
	property("direction",utils.vector(1,0))
	property("size", parameters.size ?: 1.0f)
	property("speed", parameters.speed ?: 0.1f)
	
	
	component(new SuperMovementComponent("movementComponent")) {
		propertyRef("position", "newPosition")
		propertyRef("maxVelocity", "speed")
	}
	
	component(utils.components.genericComponent(id:"updateMoveDirection", messageId:"update"){ message ->
		
		def moveDirection = entity.moveDirection ?: utils.vector(0,0)
		
		if (moveDirection.lengthSquared() > 0f) {
			def desiredDirection = moveDirection.normalise().scale(0.01f)
			entity."movementComponent.force".add(desiredDirection)
		} else {
			entity."movementComponent.force".add(entity."movementComponent.velocity".copy().negate().scale(0.01f))
		}
		
		entity.shouldBeMoving = moveDirection.length() > 0f
		
		entity.moveDirection = utils.vector(0,0)
	})
	
	property("collisionDetected", {!entity.collisions.isEmpty()})
	property("bounds", utils.rectangle(-15, -15, 30, 30))
	property("collisions", [])
	
	property("collidable", new EntityCollidableImpl(entity, new ShapeUtils(entity.bounds).getAABB()))
	
	component(utils.components.genericComponent(id:"updateCollisionsHandler", messageId:"update"){ message ->
		// check collisions
		
		// update collision bounds
		entity.bounds.centerX = entity.newPosition.x
		entity.bounds.centerY = entity.newPosition.y 
		
		entity.collidable.entity = entity
		entity.collidable.aabb.setCenter(entity.newPosition.x, entity.newPosition.y)
		
		def collisionTree = entity.collidable.quadTree
		
		if (collisionTree == null)
			return
		
		def collidables = collisionTree.getCollidables(entity.collidable)
		
		collidables = Collections2.filter(collidables, Predicates.and({collidable -> collidable.entity != null } as Predicate, //
				{collidable -> collidable.entity.tags.contains("obstacle")} as Predicate, // 
				{ collidable -> entity.collidable.aabb.collide(collidable.aabb) } as Predicate, // 
				{ collidable -> new ShapeUtils(collidable.entity.bounds).collides(entity.bounds) } as Predicate))
		
		//		obstacles = entity.root.getEntities(Predicates.and(EntityPredicates.withAnyTag("obstacle"), // 
		//				{ collidable ->	new ShapeUtils(collidable.bounds).collides(entity.bounds)} as Predicate))
		
		entity.collisions = new ArrayList(collidables)
		
		if (!entity.collisions.empty) {
			entity."movementComponent.velocity".set(0,0)
			entity.newPosition = entity.position.copy()
			entity.bounds.centerX = entity.position.x
			entity.bounds.centerY = entity.position.y 
			
			entity.collidable.aabb.setCenter(entity.position.x, entity.position.y)
		} else {
			entity.position = entity.newPosition.copy()
		}
		
	})
	
	property("hitpoints", parameters.hitpoints ?: utils.container(100f,100f))
	
	component(utils.components.genericComponent(id:"droidHittedHandler", messageId:"collisionDetected"){ message ->
		
		if (entity != message.target)
			return
		
		// delegate to component who recieve damage? another entity, a child?
		
		hitpoints = entity.hitpoints
		
		hitpoints.remove(message.damage)
		
		if (!hitpoints.empty)
			return
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("droidDead"){ newMessage ->
			newMessage.droid = entity
		})
		
	})
	
	component(utils.components.genericComponent(id:"droidDeadHandler", messageId:"droidDead"){ message ->
		if (entity != message.droid)
			return
		
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(30, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 800, 5.0f, 50f, 250f, 1f, Color.white, Color.white) 
			newMessage.layer = 1
		})
		
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
	
	property("energy", parameters.energy ?: utils.container(100f,100f))
	property("regenerationSpeed", parameters.regenerationSpeed ?: 0.02f)
	
	component(utils.components.genericComponent(id:"regenerateEnergy", messageId:"update"){ message ->
		regenerationSpeed = entity.regenerationSpeed
		entity.energy.add((float)regenerationSpeed * message.delta)
	})
	
	// transfer component
	
	property("selectedDroid", null)
	property("transfering", false)
	
	component(new TransferComponent("transferComponent")) {
		propertyRef("selectedDroid", "selectedDroid")
		propertyRef("transfering", "transfering")
		property("totalTransferTime", parameters.transferTime ?: 500)
	}
	
	component(new TransferRendererComponent("transferRendererComponent")) {
		propertyRef("selectedDroid", "selectedDroid")
		propertyRef("transfering", "transfering")
	}
	
	component(utils.components.genericComponent(id:"changeOwnerHandler", messageId:"changeOwner"){ message ->
		if (entity != message.controlledDroid)
			return
		log.info("Droid has new owner - droid.id : $entity.id - owner.id : $message.ownerId")
		entity.ownerId = message.ownerId
	})
	
	
	// component? require hitpoints on the entity
	
	property("visible", 0.0f)
	property("pointerposition", utils.vector(-10000f,-10000f))
	
	component(utils.components.genericComponent(id:"droidLostFocusHandler", messageId:"droidLostFocus"){ message ->
		if (entity.id != message.droidId)
			return
		if (entity.visible == 0.0f)
			return
		entity.interpolator = new FloatInterpolator(1000, entity.visible, 0.0f)
	})
	
	component(utils.components.genericComponent(id:"droidFocusedHandler", messageId:"droidFocused"){ message ->
		if (entity.id != message.droidId)
			return
		if (entity.visible == 1.0f)
			return
		entity.interpolator = new FloatInterpolator(100, entity.visible, 1.0f)
	})
	
	component(new CursorOverDetector("detectMouseOver")) {
		propertyRef("position", "position")
		property("bounds", utils.rectangle(-25, -25, 50, 50))
		propertyRef("cursorPosition", "pointerposition")
		
		property("onEnterTrigger", utils.custom.triggers.closureTrigger { 
			utils.custom.messageQueue.enqueue(utils.genericMessage("droidFocused"){ newMessage ->
				newMessage.droidId = entity.id
			})
		})
		
		property("onLeaveTrigger", utils.custom.triggers.closureTrigger { 
			utils.custom.messageQueue.enqueue(utils.genericMessage("droidLostFocus"){ newMessage ->
				newMessage.droidId = entity.id
			})
		})
	}
	
	component(utils.components.genericComponent(id:"updateHitpointsInterpolator", messageId:"update"){ message ->
		def interpolator = entity.interpolator
		if (!interpolator)
			return
		interpolator.update(message.delta)
		entity.visible = interpolator.interpolatedValue
		if (!interpolator.finished)
			return
		entity.interpolator = null
	})
	
	component(utils.components.genericComponent(id:"updateCursorPosition", messageId:"update"){ message ->
		def camera = entity.root.getEntityById("camera")
		if (!camera)
			return
		entity.pointerposition = camera.mousePosition.copy()
	})
	
	component(new BarRendererComponent("hitpointsRenderer") ){
		property("position", {entity.position.copy().add(utils.vector(-22f, 20f))})
		propertyRef("container", "hitpoints")
		property("width", 44f)
		property("height", 5f)
		property("fullColor", {utils.color(0.3f, 0.6f, 0.9f, entity.visible)})
		property("emptyColor", {utils.color(0.9f, 0.1f, 0.1f, entity.visible)})
		property("layer", 20)
	}
	
	//// temporal
	
	//	child(id:"${entity.id}-light1".toString(), template:"dassault.entities.pointlight") { 
	//		position = {entity.parent.position}
	//		layer = -4
	//		size = 5f
	//		time = 1500
	//		startColor = utils.color(1f,1f,1f,0.2f)
	//		endColor = utils.color(1f,1f,1f,0.8f)
	//	}
	
	//// 
	
}
