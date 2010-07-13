package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.gemserk.games.dassault.components.AnimationComponent 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity(entityName ?: "droid-${Math.random()}") {
	
	tags("basicdroid")
	
	// call with animations and images?
	parent("dassault.entities.droid", parameters)
	
	// walk animation
	
	property("headPosition", utils.vector(0,0))
	
	def headAnimation = new PropertyAnimation("headPosition");
	
	headAnimation.addKeyFrame 0, utils.vector(0,0)
	headAnimation.addKeyFrame 120, utils.vector(0,-4)
	headAnimation.addKeyFrame 240, utils.vector(0, 0)
	headAnimation.addKeyFrame 360, utils.vector(0,-4)
	headAnimation.addKeyFrame 480, utils.vector(0,0)
	
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
		
		def size = entity.size
		
		def layer = 0
		def color = owner.color
		
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
}
