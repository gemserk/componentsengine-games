package grapplinghookus.entities

import com.gemserk.commons.collisions.QuadTreeRenderObject;

builder.entity {
	
	tags("debug")
	
	property("enabled", false)
	property("quadtree", parameters.quadtree)
	
	component(utils.components.genericComponent(id:"toggleDebugHandler", messageId:"toggleQuadtreeDebug"){ message ->
		entity.enabled = !entity.enabled
	})
	
	component(utils.components.genericComponent(id:"renderQuadtreeNodes", messageId:"render"){ message ->
		if (!entity.enabled)
			return
		
		def quadtree = entity.quadtree
		
		def renderer = message.renderer
		
		def nodes = quadtree.leafs
		
		// TODO: color and widths as parameters.
		renderer.enqueue(new QuadTreeRenderObject(99, quadtree));
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"2",eventId:"toggleQuadtreeDebug")
		}
	}	
}
