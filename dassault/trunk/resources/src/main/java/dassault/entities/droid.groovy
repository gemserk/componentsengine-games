package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.gemserk.componentsengine.utils.OpenGlUtils;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates;
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.opengl.SlickCallable;


builder.entity {
	
	tags("droid", "nofriction")
	
	property("position", parameters.position.copy())
	property("newPosition", parameters.position)
	property("direction",utils.vector(1,0))
	property("size", 1.0f)
	property("speed", parameters.speed ?: 0.1f)
	
	property("bounds", utils.rectangle(-15, -15, 30, 30))
	
	// walk animation
	
	property("headPosition", utils.vector(0,0))
	
	def headAnimation = new PropertyAnimation("headPosition");
	
	headAnimation.addKeyFrame 0, utils.vector(0,0)
	headAnimation.addKeyFrame 150, utils.vector(0,-3)
	headAnimation.addKeyFrame 300, utils.vector(0,0)
	headAnimation.addKeyFrame 450, utils.vector(0,3)
	headAnimation.addKeyFrame 600, utils.vector(0,0)
	
	property("walkAnimations", [headAnimation])
	
	component(utils.components.genericComponent(id:"startWalkAnimationHandler", messageId:"startWalkAnimation"){ message ->
		if(!entity.id.equals(message.animationId))
			return
		entity.walkAnimations.each { animation -> 
			animation.restart()
		}
	})
	
	component(utils.components.genericComponent(id:"stopWalkAnimationHandler", messageId:"stopWalkAnimation"){ message ->
		if(!entity.id.equals(message.animationId))
			return
		entity.walkAnimations.each { animation -> 
			animation.stop()
		}
	})
	
	component(utils.components.genericComponent(id:"updateAnimationsHandler", messageId:"update"){ message ->
		entity.walkAnimations.each { animation ->
			if (animation.paused)
				return
			animation.animate(entity, message.delta)
			if (animation.finished)
				animation.restart()
		}
	})
	
	property("shadowImage", utils.resources.image("droidshadow"))
	
	// render type
	
	// weapon type
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		//		println entity.headPosition
		//		println entity.isMoving
		
		def size = entity.size
		def layer = 0
		def color = utils.color(1f,1f,1f,1f)
		def shape = utils.rectangle(-14, -14, 28, 28)
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			g.translate((float) position.x + entity.headPosition.x, (float)position.y + entity.headPosition.y)
			g.scale(size, size)
			g.fill(shape)
			g.popTransform()
		}))
		
		position = entity.position.copy()
		def image = entity.shadowImage
		def shadowSize = (float) size * 0.6f
		
		renderer.enqueue( new ClosureRenderObject(layer-1, { Graphics g ->
			g.setColor(utils.color(1f, 1f, 1f, 0.3f))
			g.pushTransform()
			g.translate((float) position.x, (float)position.y + 20)
			g.scale(shadowSize, shadowSize)
			g.drawImage(image, (float)-(image.getWidth() / 2), (float)-(image.getHeight() / 2))
			g.popTransform()
		}))
		
	})
	
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
		
		entity.moveDirection = utils.vector(0,0)
	})
	
	component(utils.components.genericComponent(id:"updatePositionHandler", messageId:"update"){ message ->
		// check collisions
		
		// update collision bounds
		entity.bounds.centerX = entity.newPosition.x
		entity.bounds.centerY = entity.newPosition.y 
		
		obstacles = entity.root.getEntities(Predicates.and(EntityPredicates.withAnyTag("obstacle"), { obstacle -> obstacle.bounds.intersects(entity.bounds) } as Predicate))
		
		if (!obstacles.empty) {
			entity."movementComponent.velocity".set(0,0)
			entity.newPosition = entity.position.copy()
			
			entity.bounds.centerX = entity.position.x
			entity.bounds.centerY = entity.position.y 
			
			return
		}
		
		entity.position = entity.newPosition
		
	})
	
	property("hitpoints", parameters.hitpoints ?: utils.container(100f,100f))
	
	component(utils.components.genericComponent(id:"droidHittedHandler", messageId:"droidHitted"){ message ->
		
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
	
	property("isMoving", false)
	
	component(utils.components.genericComponent(id:"animationsHandler", messageId:"update"){ message ->
		def isMoving = entity.isMoving
		
		if (!isMoving) {
			if (entity."movementComponent.velocity".lengthSquared() > 0.001f) {
				entity.isMoving = true
				//				println "starting walk animation"
				utils.custom.messageQueue.enqueue(utils.genericMessage("startWalkAnimation"){ newMessage ->
					newMessage.animationId = entity.id
				})
			}
		} else {
			if (entity."movementComponent.velocity".lengthSquared() > 0.001f) 
				return
			//			println "stop walking"
			utils.custom.messageQueue.enqueue(utils.genericMessage("stopWalkAnimation"){ newMessage ->
				newMessage.animationId = entity.id
			})
			entity.isMoving = false
		}
	})
	
	// transfer component
	
	property("ownerId", parameters.ownerId)
	
	property("selectedDroid", null)
	property("transfering", false)
	property("totalTransferTime", parameters.transferTime ?: 500)
	property("transferTime", 0)
	
	component(utils.components.genericComponent(id:"startTransferingHandler", messageId:"startTransfering"){ message ->
		if (entity.id != message.droidId )
			return
			
			// check preconditions like transfer points, etc
			
		entity.selectedDroid = message.selectedDroid
		entity.transferTime = entity.totalTransferTime
		entity.transfering = true
	})
	
	component(utils.components.genericComponent(id:"transferComponent", messageId:"update"){ message ->
		
		if (!entity.transfering)
			return
		
		entity.transferTime = entity.transferTime - message.delta
		
		if (entity.transferTime > 0)
			return
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("changeControlledDroid"){ newMessage ->
			newMessage.controlledDroid = entity.selectedDroid
			newMessage.ownerId = entity.ownerId
		})
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("stopTransfering"){ newMessage ->
			newMessage.droidId = entity.id
		})
		
	})
	
	component(utils.components.genericComponent(id:"stopTransferingHandler", messageId:"stopTransfering"){ message ->
		if (entity.id != message.droidId )
			return
		
		entity.selectedDroid = null
		entity.transfering = false
	})
	
	component(utils.components.genericComponent(id:"transferRenderer", messageId:"render"){ message ->
		
		if (!entity.transfering)
			return
		
		def renderer = message.renderer
		
		def selectedDroid = entity.selectedDroid
		
		def start = entity.position.copy()
		def end = selectedDroid.position.copy()
		
		def layer = 0
		def color = utils.color(0.2f,0.2f,1f,0.6f)
		
		renderer.enqueue( new ClosureRenderObject(layer+2, { Graphics g ->
			
			SlickCallable.enterSafeBlock();
			
			OpenGlUtils.renderLine(start, end, 5.0f, color)
			
			SlickCallable.leaveSafeBlock();
		}))
	})
	
	component(utils.components.genericComponent(id:"changeOwnerHandler", messageId:"changeOwner"){ message ->
		if (entity != message.controlledDroid)
			return
		entity.ownerId = message.ownerId
	})
	
}
