package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates;
import org.newdawn.slick.Graphics 
import org.newdawn.slick.Input 


builder.entity {
	
	tags("droid", "nofriction")
	
	property("position", parameters.position.copy())
	property("newPosition", parameters.position)
	property("direction",utils.vector(1,0))
	property("size", 1.0f)
	
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
		property("maxVelocity", 0.1f)
	}
	
	component(utils.components.genericComponent(id:"updateMoveDirection", messageId:"update"){ message ->
		
		Input input = utils.custom.gameContainer.input
		
		def moveDirection = utils.vector(0,0)
		
		if (input.isKeyDown(Input.KEY_LEFT)) {
			moveDirection.x = -1
		}
		
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			moveDirection.x = 1
		}
		
		if (input.isKeyDown(Input.KEY_UP)) {
			moveDirection.y = -1
		}
		
		if (input.isKeyDown(Input.KEY_DOWN)) {
			moveDirection.y = 1
		}
		
		if (moveDirection.lengthSquared() > 0f) {
			def desiredDirection = moveDirection.normalise().scale(0.01f)
			entity."movementComponent.force".add(desiredDirection)
		} else {
			entity."movementComponent.force".add(entity."movementComponent.velocity".copy().negate().scale(0.01f))
		}
		
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
	
}
