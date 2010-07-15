package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.render.SlickImageRenderObject;
import com.gemserk.games.dassault.components.AnimationComponent 
import org.newdawn.slick.Color 

builder.entity(entityName ?: "droid-${Math.random()}") {
	
	tags("floatingdroid")
	
	// call with animations and images?
	parent("dassault.entities.droid", parameters)
	
	// walk animation
	
	property("headPosition", utils.vector(0,0))
	
	def headAnimation = new PropertyAnimation("headPosition");
	
	headAnimation.addKeyFrame 0, utils.vector(0,2)
	headAnimation.addKeyFrame 320, utils.vector(0,-2)
	headAnimation.addKeyFrame 640, utils.vector(0,2)
	
	def animations = [
			idle:[], 
			walk:[headAnimation]
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
	property("droidShadow",  utils.resources.image("droid_shadow"))
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def owner = entity.root.getEntityById(entity.ownerId)
		
		def size = utils.vector(entity.size, entity.size)
		
		def layer = 0
		def color = owner.color
		
		def headPosition = position.copy().add(entity.headPosition)
		
		renderer.enqueue(new SlickImageRenderObject(layer-2, entity.droidBackground, headPosition, //
				size, 0f, Color.white))
		renderer.enqueue(new SlickImageRenderObject(layer-1, entity.droidBorder, headPosition, //
				size, 0f, color))
		renderer.enqueue(new SlickImageRenderObject(layer-1, entity.droidEyesBlur, headPosition, //
				size, 0f, Color.red))
		renderer.enqueue(new SlickImageRenderObject(layer, entity.droidEyes, headPosition, //
				size, 0f, Color.white))
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
