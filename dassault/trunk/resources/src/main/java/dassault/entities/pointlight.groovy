package dassault.entities

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("light", "pointlight")
	
	property("position", parameters.position)
	property("layer", parameters.layer ?: 10)
	property("size", utils.vector(parameters.size, parameters.size))
	property("color", parameters.startColor)
	
	component(new ImageRenderableComponent("renderer")) {
		propertyRef("position", "position")
		property("image", utils.resources.image("circular_light"))
		property("direction", utils.vector(1,0))
		propertyRef("layer", "layer")
		propertyRef("size", "size")
		propertyRef("color", "color")
	}
	
	def colorAnimation = new PropertyAnimation("color");
	
	colorAnimation.addKeyFrame 0, parameters.startColor
	colorAnimation.addKeyFrame parameters.time, parameters.endColor
	colorAnimation.addKeyFrame parameters.time*2, parameters.startColor
	
	colorAnimation.play()
	
	parent("dassault.entities.animation", [animations:[colorAnimation]])
	
	// remove this to other entity
	component(utils.components.genericComponent(id:"lightRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		def position = entity.position
		
		def size = entity.size.x 
		def color = entity.color
		def layer = -5
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			g.translate(position.x, position.y)
			g.scale(size, size)
			g.fillRect(-1, -1, 2, 2)
			g.popTransform()
		}))
		
	})
}
