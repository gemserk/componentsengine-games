package dassault.entities
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity {
	
	property("position", parameters.position)
	property("enabled", false)
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:["update"]){ message ->
		if (!entity.enabled)
			return
		
		def input = utils.custom.gameContainer.input
		entity.position = utils.vector((float) input.getMouseX() - 400, (float) input.getMouseY() - 300)
	})
	
	component(utils.components.genericComponent(id:"enableCamera", messageId:["enableCamera"]){ message ->
		entity.enabled = true
	})

	component(utils.components.genericComponent(id:"disableCamera", messageId:["disableCamera"]){ message ->
		entity.enabled = false
	})
	
	component(utils.components.genericComponent(id:"changePerspective", messageId:["render"]){ message ->
		def renderer = message.renderer
		
		def position = entity.position
		
		renderer.enqueue( new ClosureRenderObject(-100, { Graphics g ->
			g.pushTransform()
			g.translate(position.x, position.y)
		}))
		
		renderer.enqueue( new ClosureRenderObject(10, { Graphics g ->
			g.popTransform()
		}))
		
	})
	
	input("inputmapping"){
		keyboard {
//			press(button:"e",eventId:"enableCamera")
//			release(button:"e",eventId:"disableCamera")
		}
		mouse {
			press(button:"left",eventId:"enableCamera")
			release(button:"left",eventId:"disableCamera")
		}
	}
	
}
