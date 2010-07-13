package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.gemserk.games.dassault.components.AnimationComponent 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity(entityName ?: "droid-${Math.random()}") {
	
	tags("floatingdroid")
	
	// call with animations and images?
	parent("dassault.entities.droid", parameters)
	
	// walk animation
	
	property("headPosition", utils.vector(0,0))
	
	def headAnimation = new PropertyAnimation("headPosition");
	
	headAnimation.addKeyFrame 0, utils.vector(0,0)
	headAnimation.addKeyFrame 320, utils.vector(0,-4)
	headAnimation.addKeyFrame 640, utils.vector(0,0)
	
	component(new AnimationComponent("walkAnimationComponent") ) {
		property("id", "walk")
		property("animations", [headAnimation])
	}
	
	property("shadowImage", utils.resources.image("droidshadow"))
	
	// render type
	
	property("droidBackground",  utils.resources.image("droid_background"))
	property("droidEyes",  utils.resources.image("droid_eyes"))
	property("droidEyesBlur",  utils.resources.image("droid_eyes_blur"))
	property("droidBorder",  utils.resources.image("droid_border"))
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
		
		renderer.enqueue(imagerRenderableObject(layer-2, entity.droidBackground, headPosition.x, //
				headPosition.y, size, Color.white))
		renderer.enqueue(imagerRenderableObject(layer-1, entity.droidBorder, headPosition.x, //
				headPosition.y, size, color))
		renderer.enqueue(imagerRenderableObject(layer-1, entity.droidEyesBlur, headPosition.x, //
				headPosition.y, size, Color.red))
		renderer.enqueue(imagerRenderableObject(layer, entity.droidEyes, headPosition.x, //
				headPosition.y, size, Color.white))
		renderer.enqueue(imagerRenderableObject(layer-4, entity.droidShadow, position.x, //
				position.y, size, Color.white))			
		
	})
}
