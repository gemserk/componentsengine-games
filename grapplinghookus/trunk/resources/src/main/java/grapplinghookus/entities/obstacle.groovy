package grapplinghookus.entities

import com.gemserk.commons.slick.geom.ShapeUtils;

import com.gemserk.commons.collisions.EntityCollidableImpl 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("obstacle", "collidable")
	
	property("position", parameters.position)
	property("color", parameters.color ?: utils.color(1,1,1,1))
	property("layer", parameters.layer ?: 20)
	
	// update collision bounds, should do nothing if the points are the same as before (inside shape class)
	
	// collision component
	
	property("bounds", parameters.bounds)
	property("collidable", new EntityCollidableImpl(null, new ShapeUtils(entity.bounds).getAABB() ))
	
	component(utils.components.genericComponent(id:"updateBoundsHandler", messageId:"update"){ message ->
		entity.bounds.centerX = entity.position.x
		entity.bounds.centerY = entity.position.y
		
		entity.collidable.entity = entity
		entity.collidable.setCenter(entity.position.x, entity.position.y)
		entity.collidable.update()
	})
	
	//
	
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
