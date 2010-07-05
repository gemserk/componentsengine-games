package dassault.entities
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity {
	
	property("owner", parameters.owner)
	property("screen", parameters.screen)
	
	property("followMouse", parameters.followMouse)
	
	property("position", utils.vector(0,0))
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:["update"]){ message ->
		
		def screen = entity.screen
		def centerPosition = utils.vector((float)screen.width * 0.5f, (float)screen.height * 0.5f)
		
		def owner = entity.root.getEntityById(entity.owner)
		
		def targetPosition = owner.position.copy().sub(centerPosition).negate()
		
		entity.position =  targetPosition
		
		if (!entity.followMouse)
			return
		
		// if followMouse then mid point between mouse and owner position
		def input = utils.custom.gameContainer.input
		
		def mousePosition = utils.vector((float) input.getMouseX(), (float) input.getMouseY())
		mousePosition.sub(centerPosition).negateLocal()
		
		targetPosition = targetPosition.add(mousePosition).scale(0.5f)
		entity.position = targetPosition
		
//		entity.position = mousePosition
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
			//						g.translate((float)screen.width * 0.5f, (float)screen.height * 0.5f)
			//						g.scale(2.0f, 2.0f)
//									g.translate((float)screen.width * -0.5f, (float)screen.height * -0.5f)
			g.translate(position.x, position.y)
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
