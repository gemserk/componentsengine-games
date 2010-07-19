package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("debug")
	
	property("enabled", false)
	property("collidables", [])
	
	component(utils.components.genericComponent(id:"toggleDebugHandler", messageId:"toggleDebug"){ message ->
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
		
		def renderer = message.renderer
		
		def collidables = entity.collidables
		
		collidables.each { collidable ->
			def bounds = collidable.bounds
			def position = entity.position
			
			def layer = entity.layer
			def shape = entity.bounds
			
			renderer.enqueue( new ClosureRenderObject(99, { Graphics g ->
				g.setColor(Color.white)
				g.pushTransform()
				g.scale(1f, 1f)
				g.draw bounds
				g.popTransform()
			}))
		}
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"1",eventId:"toggleDebug")
		}
	}	
}
