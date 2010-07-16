package dassault.entities


import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("obstacle", "collidable")
	
	property("position", parameters.position)
	property("bounds", parameters.bounds)
	
	property("color", parameters.color ?: utils.color(1,1,1,1))
	property("layer", parameters.layer ?: 20)
	
	component(utils.components.genericComponent(id:"updateBoundsHandler", messageId:"update"){ message ->
		// update collision bounds
		entity.bounds.centerX = entity.position.x
		entity.bounds.centerY = entity.position.y 
	})
	
	component(utils.components.genericComponent(id:"obstacleRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		
		def size = 1f
		def layer = entity.layer
		def color = entity.color
		def shape = entity.bounds
		
		renderer.enqueue( new ClosureRenderObject(layer+2, { Graphics g ->
			g.setColor(Color.red)
			g.pushTransform()
			g.translate(position.x, position.y)
			g.scale(size, size)
			g.fillRect(-1f, -1f, 2f, 2f)
			g.popTransform()
		}))
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			// g.translate(position.x, position.y)
			g.scale(size, size)
			g.fill(shape)
			g.popTransform()
		}))
		
	})
}
