package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.commons.animation.interpolators.FloatInterpolator;
import com.gemserk.componentsengine.commons.components.BarRendererComponent 
import com.gemserk.componentsengine.commons.components.CursorOverDetector;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.gemserk.games.dassault.components.AnimationComponent;
import com.gemserk.games.dassault.components.TransferComponent;
import com.gemserk.games.dassault.components.TransferRendererComponent;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates;
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 


builder.entity(entityName ?: "droid-${Math.random()}") {
	
	tags("droid", "nofriction")
	
	// TODO: use owner instead of id
	property("ownerId", parameters.ownerId)
	
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
	headAnimation.addKeyFrame 60, utils.vector(0,-2)
	headAnimation.addKeyFrame 120, utils.vector(0,-4)
	headAnimation.addKeyFrame 180, utils.vector(0,-2)
	headAnimation.addKeyFrame 240, utils.vector(0, 0)
	headAnimation.addKeyFrame 300, utils.vector(0,-2)
	headAnimation.addKeyFrame 360, utils.vector(0,-4)
	headAnimation.addKeyFrame 420, utils.vector(0,-2)
	headAnimation.addKeyFrame 480, utils.vector(0,0)
	
	property("leftLegPosition", utils.vector(0,0))
	
	def leftLegAnimation = new PropertyAnimation("leftLegPosition");
	
	leftLegAnimation.addKeyFrame 0, utils.vector(0,0)
	leftLegAnimation.addKeyFrame 60, utils.vector(0,-2)
	leftLegAnimation.addKeyFrame 120, utils.vector(0,-4)
	leftLegAnimation.addKeyFrame 180, utils.vector(0,-2)
	leftLegAnimation.addKeyFrame 240, utils.vector(0,0)
	leftLegAnimation.addKeyFrame 480, utils.vector(0,0)
	
	property("rightLegPosition", utils.vector(0,0))
	
	def rightLegAnimation = new PropertyAnimation("rightLegPosition");
	
	rightLegAnimation.addKeyFrame 0, utils.vector(0,0)
	rightLegAnimation.addKeyFrame 240, utils.vector(0,0)
	rightLegAnimation.addKeyFrame 300, utils.vector(0,-2)
	rightLegAnimation.addKeyFrame 360, utils.vector(0,-4)
	rightLegAnimation.addKeyFrame 420, utils.vector(0,-2)
	rightLegAnimation.addKeyFrame 480, utils.vector(0,0)
	
	component(new AnimationComponent("walkAnimationComponent") ) {
		property("id", "walk")
		property("animations", [headAnimation, leftLegAnimation, rightLegAnimation])
	}
	
	property("shadowImage", utils.resources.image("droidshadow"))
	
	// render type
	
	// weapon type
	
	property("droidBackground",  utils.resources.image("droid_background"))
	property("droidEyes",  utils.resources.image("droid_eyes"))
	property("droidEyesBlur",  utils.resources.image("droid_eyes_blur"))
	property("droidBorder",  utils.resources.image("droid_border"))
	property("droidLeftLeg",  utils.resources.image("droid_left_leg"))
	property("droidRightLeg",  utils.resources.image("droid_right_leg"))
	property("droidShadow",  utils.resources.image("droid_shadow"))
	
	def imagerRenderableObject = { layer, image, x, y, size, color -> 
		return new ClosureRenderObject(layer, { Graphics g ->
			g.pushTransform()
			g.translate(x, y)
			g.scale(size, size)
			g.drawImage(image, (float)-(image.getWidth() / 2), (float)-(image.getHeight() / 2), color)
			g.popTransform()
		})
	}
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def owner = entity.root.getEntityById(entity.ownerId)
		
		//		def size = entity.size
		def size = 0.75f
		
		def layer = 0
		def color = owner.color
		def shape = utils.rectangle(-14, -14, 28, 28)
		
		def headPosition = position.copy().add(entity.headPosition).add(utils.vector(0,2f))
		def rightLegPosition = position.copy().add(entity.rightLegPosition)
		def leftLegPosition = position.copy().add(entity.leftLegPosition)
		
		renderer.enqueue(imagerRenderableObject(layer-2, entity.droidBackground, headPosition.x, //
				headPosition.y, size, Color.white))
		renderer.enqueue(imagerRenderableObject(layer-1, entity.droidBorder, headPosition.x, //
				headPosition.y, size, color))
		renderer.enqueue(imagerRenderableObject(layer-1, entity.droidEyesBlur, headPosition.x, //
				headPosition.y, size, Color.red))
		renderer.enqueue(imagerRenderableObject(layer, entity.droidEyes, headPosition.x, //
				headPosition.y, size, Color.white))
		renderer.enqueue(imagerRenderableObject(layer-3, entity.droidLeftLeg, leftLegPosition.x, //
				leftLegPosition.y, size, color))
		renderer.enqueue(imagerRenderableObject(layer-3, entity.droidRightLeg, rightLegPosition.x, //
				rightLegPosition.y, size, color))
		renderer.enqueue(imagerRenderableObject(layer-4, entity.droidShadow, position.x, //
				position.y, size, Color.white))			
		
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
		
		entity.shouldBeMoving = moveDirection.length() > 0f
		
		entity.moveDirection = utils.vector(0,0)
	})
	
	component(utils.components.genericComponent(id:"updatePositionHandler", messageId:"update"){ message ->
		// check collisions
		
		// update collision bounds
		entity.bounds.centerX = entity.newPosition.x
		entity.bounds.centerY = entity.newPosition.y 
		
		obstacles = entity.root.getEntities(Predicates.and(EntityPredicates.withAnyTag("obstacle"), { obstacle ->
			obstacle.bounds.intersects(entity.bounds)
		} as Predicate))
		
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
		
		def shouldBeMoving = entity.shouldBeMoving ?: false
		
		if (!isMoving) {
			if (shouldBeMoving) {
				entity.isMoving = true
				//				println "starting walk animation"
				utils.custom.messageQueue.enqueue(utils.genericMessage("startAnimation"){ newMessage ->
					newMessage.animationId = "walk"
				})
			}
		} else {
			if (shouldBeMoving) 
				return
			//			println "stop walking"
			utils.custom.messageQueue.enqueue(utils.genericMessage("stopAnimation"){ newMessage ->
				newMessage.animationId = "walk"
			})
			entity.isMoving = false
		}
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
	
	component(new CursorOverDetector("detectMouseOver")) {
		propertyRef("position", "position")
		property("bounds", utils.rectangle(-25, -25, 50, 50))
		propertyRef("cursorPosition", "pointerposition")
		property("onEnterTrigger", utils.custom.triggers.closureTrigger {
			if (entity.visible == 1.0f)
				return
			entity.interpolator = new FloatInterpolator(100, entity.visible, 1.0f)
		})
		property("onLeaveTrigger", utils.custom.triggers.closureTrigger { 
			if (entity.visible == 0.0f)
				return
			entity.interpolator = new FloatInterpolator(1000, entity.visible, 0.0f)
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
	
}
