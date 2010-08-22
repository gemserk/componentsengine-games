package grapplinghookus.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.render.SlickImageRenderObject 
import com.gemserk.games.grapplinghookus.components.AnimationComponent 
import org.newdawn.slick.Color 

builder.entity(entityName ?: "droid-${Math.random()}") {
	
	tags("basicdroid")
	
	// call with animations and images?
	parent("dassault.entities.droid", parameters)
	
	// walk animation
	
	property("headPosition", utils.vector(0,0))
	
	def headAnimation = new PropertyAnimation("headPosition");
	
	headAnimation.addKeyFrame 0, utils.vector(0,2)
	headAnimation.addKeyFrame 120, utils.vector(0,-2)
	headAnimation.addKeyFrame 240, utils.vector(0, 2)
	headAnimation.addKeyFrame 360, utils.vector(0,-2)
	headAnimation.addKeyFrame 480, utils.vector(0,2)
	
	property("leftLegPosition", utils.vector(0,0))
	
	def leftLegAnimation = new PropertyAnimation("leftLegPosition");
	
	leftLegAnimation.addKeyFrame 0, utils.vector(0,0)
	leftLegAnimation.addKeyFrame 120, utils.vector(0,-4)
	leftLegAnimation.addKeyFrame 240, utils.vector(0,0)
	leftLegAnimation.addKeyFrame 480, utils.vector(0,0)
	
	property("rightLegPosition", utils.vector(0,0))
	
	def rightLegAnimation = new PropertyAnimation("rightLegPosition");
	
	rightLegAnimation.addKeyFrame 0, utils.vector(0,0)
	rightLegAnimation.addKeyFrame 240, utils.vector(0,0)
	rightLegAnimation.addKeyFrame 360, utils.vector(0,-4)
	rightLegAnimation.addKeyFrame 480, utils.vector(0,0)
	
	def animations = [
			idle:[], 
			walk:[headAnimation, leftLegAnimation, rightLegAnimation]
			]
	
	component(new AnimationComponent("walkAnimationComponent") ) {
		property("current", "idle")
		property("animations", animations)
	}
	
	property("shadowImage", utils.resources.image("droidshadow"))
	
	property("droidBackground",  utils.resources.image("droid_background"))
	property("droidEyes",  utils.resources.image("droid_eyes"))
	property("droidEyesBlur",  utils.resources.image("droid_eyes_blur"))
	property("droidBorder",  utils.resources.image("droid_border"))
	property("droidLeftLeg",  utils.resources.image("droid_left_leg"))
	property("droidRightLeg",  utils.resources.image("droid_right_leg"))
	property("droidShadow",  utils.resources.image("droid_shadow"))
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def player = entity.player
		
		def size = utils.vector(entity.size, entity.size)
		
		def layer = 0
		def color = player.color
		
		def headPosition = position.copy().add(entity.headPosition)
		def rightLegPosition = position.copy().add(entity.rightLegPosition)
		def leftLegPosition = position.copy().add(entity.leftLegPosition)
		
		renderer.enqueue(new SlickImageRenderObject(layer-2, entity.droidBackground, headPosition, //
				size, 0f, Color.white))
		renderer.enqueue(new SlickImageRenderObject(layer-1, entity.droidBorder, headPosition, //
				size, 0f, color))
		renderer.enqueue(new SlickImageRenderObject(layer-1, entity.droidEyesBlur, headPosition, //
				size, 0f, Color.red))
		renderer.enqueue(new SlickImageRenderObject(layer, entity.droidEyes, headPosition, //
				size, 0f, Color.white))
		renderer.enqueue(new SlickImageRenderObject(layer-3, entity.droidLeftLeg, leftLegPosition, //
				size, 0f, color))
		renderer.enqueue(new SlickImageRenderObject(layer-3, entity.droidRightLeg, rightLegPosition, //
				size, 0f, color))
		renderer.enqueue(new SlickImageRenderObject(layer-10, entity.droidShadow, position, //
				size, 0f, Color.white))			
		
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
					newMessage.entityId = entity.id
				})
			}
		} else {
			if (shouldBeMoving) 
				return
			//			println "stop walking"
			utils.custom.messageQueue.enqueue(utils.genericMessage("startAnimation"){ newMessage ->
				newMessage.animationId = "idle"
				newMessage.entityId = entity.id
			})
			entity.isMoving = false
		}
	})
}
