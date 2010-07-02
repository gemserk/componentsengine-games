package dassault.entities

import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("obstacle")
	
	property("position", parameters.position)
	property("bounds", parameters.bounds)
	
	component(utils.components.genericComponent(id:"obstacleRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def size = 1f
		def layer = 0
		def color = utils.color(1f,1f,1f,1f)
		def shape = entity.bounds
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			g.translate(position.x, position.y)
			g.scale(size, size)
			g.fill(shape)
			g.popTransform()
		}))
		
	})
}
