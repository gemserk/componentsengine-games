package dassault.entities
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity {
	
	property("owner", parameters.owner)
	property("screen", parameters.screen)
	property("enabled", false)
	
	property("mousePosition", utils.vector(0,0))
	property("followMouse", parameters.followMouse)
	
	property("position", utils.vector(0,0))
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:["update"]){ message ->
		def owner = entity.root.getEntityById(entity.owner)
		entity.position =  owner.position.copy()
		
		if (!entity.followMouse)
			return
		
		def position = entity.position
		
		// if followMouse then mid point between mouse and owner position
		def input = utils.custom.gameContainer.input
		entity.mousePosition = utils.vector((float) input.getMouseX(), (float) input.getMouseY())
		
		position = position.add(entity.mousePosition).scale(0.5f)
		
		entity.position = position
	})
	
	component(utils.components.genericComponent(id:"toggleFollow", messageId:["toggleFollowMouse"]){ message ->
		entity.followMouse = !entity.followMouse
	})
	
	component(utils.components.genericComponent(id:"changePerspective", messageId:["render"]){ message ->
		def renderer = message.renderer
		
		def screen = entity.screen
		
		def position = entity.position
		
		renderer.enqueue( new ClosureRenderObject(-100, { Graphics g ->
			g.pushTransform()
			g.translate((float)screen.width * 0.5f, (float)screen.height * 0.5f)
			//						g.translate((float)screen.width * 0.5f, (float)screen.height * 0.5f)
			//						g.scale(2.0f, 2.0f)
			//						g.translate((float)screen.width * -0.5f, (float)screen.height * -0.5f)
			g.translate(-position.x, -position.y)
		}))
		
		renderer.enqueue( new ClosureRenderObject(10, { Graphics g ->
			g.popTransform()
		}))
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"e",eventId:"toggleFollowMouse")
		}
		mouse {
			press(button:"right",eventId:"enableCamera")
			release(button:"right",eventId:"disableCamera")
		}
	}
	
}
