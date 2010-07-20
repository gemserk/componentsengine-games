package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("debug")
	
	property("enabled", false)
	property("quadtree", parameters.quadtree)
	
	component(utils.components.genericComponent(id:"toggleDebugHandler", messageId:"toggleQuadtreeDebug"){ message ->
		entity.enabled = !entity.enabled
	})
	
	component(utils.components.genericComponent(id:"updateCollidables", messageId:"update"){ message ->
		if (!entity.enabled)
			return
		entity.collidables = entity.root.getEntities(EntityPredicates.withAllTags("collidable"))
	})
	
	component(utils.components.genericComponent(id:"renderCollidables", messageId:"render"){ message ->
		if (!entity.enabled)
			return
		
		def quadtree = entity.quadtree
		
		def renderer = message.renderer
		
		def nodes = quadtree.leafs
		
		nodes.each { node ->
			def color = Color.white
			
			if (!node.collidables.empty) {
				color = Color.red
			}
			
			def aabb = node.aabb
			def rectangle = utils.rectangle(aabb.getMinX(), aabb.getMinY(), aabb.getWidth(), aabb.getHeight())
			
			renderer.enqueue( new ClosureRenderObject(99, { Graphics g ->
				g.setColor(color)
				g.pushTransform()
				g.scale(1f, 1f)
				g.draw(rectangle);
				g.popTransform()
			}))
		}
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"2",eventId:"toggleQuadtreeDebug")
		}
	}	
}
