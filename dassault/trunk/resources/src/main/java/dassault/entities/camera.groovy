package dassault.entities
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity {
	
	property("ownerId", parameters.ownerId)
	property("screen", parameters.screen)
	
	property("followMouse", parameters.followMouse)
	property("zoom", false)
	
	property("position", utils.vector(0,0))
	property("mouseRelativePosition", utils.vector(0,0))
	property("mousePosition", utils.vector(0,0))
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:["update"]){ message ->
		
		def screen = entity.screen
		
		// center screen, could be considered as start camera position
		def centerPosition = utils.vector((float)screen.width * 0.5f, (float)screen.height * 0.5f)
		
		def player = entity.root.getEntityById(entity.ownerId)
		def droid = entity.root.getEntityById(player.controlledDroidId)
		
		if (!droid) {
			// lost control of main droid
			entity.position = utils.vector(0,0)
			return
		}
		
		def input = utils.custom.gameContainer.input
		
		def targetPosition = droid.position.copy().sub(centerPosition).negate()
		entity.position =  targetPosition
		
		// if followMouse then mid point between mouse and owner position
		
		// current position of the mouse, based on current camera
		def mousePosition = utils.vector((float) input.getMouseX(), (float) input.getMouseY())
		entity.mouseRelativePosition = mousePosition.copy().sub(centerPosition)
		
		def mouseNewRelative = utils.vector((float) input.getMouseX(), (float) input.getMouseY())
		
		def transformation = entity.position
		def mouseAbsolutePosition = mouseNewRelative.copy().sub(transformation)
		
		entity.mousePosition = mouseNewRelative.copy().sub(transformation)
		
		if (!entity.followMouse)
			return
		
		//		println "oldCenter = $oldCenterPosition"
		//		println "mousePositionRelativoAlNuevoZero = $mousePositionRelativoAlNuevoZero"
		//		println "transformation = $transformation"
		//		println "mouseAbsolutePosition = $mouseAbsolutePosition"
		
		mouseAbsolutePosition.sub(centerPosition)
		mouseAbsolutePosition.negateLocal()
		
		targetPosition = targetPosition.add(mouseAbsolutePosition).scale(0.5f)
		
		//		println "targetPosition = $targetPosition"
		
		entity.position = targetPosition
		
		entity.mousePosition = mouseNewRelative.copy().sub(transformation)
	})
	
	component(utils.components.genericComponent(id:"toggleFollow", messageId:["toggleFollowMouse"]){ message ->
		entity.followMouse = !entity.followMouse
	})
	
	component(utils.components.genericComponent(id:"toggleZoom", messageId:"toggleZoom"){ message ->
		entity.zoom = !entity.zoom
	})
	
	component(utils.components.genericComponent(id:"changePerspective", messageId:["render"]){ message ->
		def renderer = message.renderer
		
		def screen = entity.screen
		
		def position = entity.position
		
		renderer.enqueue( new ClosureRenderObject(-100, { Graphics g ->
			g.pushTransform()
			if (entity.zoom) {
				g.translate((float)screen.width * 0.5f, (float)screen.height * 0.5f)
				g.scale(2.0f, 2.0f)
				g.translate((float)screen.width * -0.5f, (float)screen.height * -0.5f)
			}
			g.translate(position.x, position.y)
		}))
		
		renderer.enqueue( new ClosureRenderObject(100, { Graphics g ->
			g.popTransform()
		}))
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"e",eventId:"toggleFollowMouse")
			press(button:"z",eventId:"toggleZoom")
		}
		mouse {
			press(button:"right",eventId:"enableCamera")
			release(button:"right",eventId:"disableCamera")
		}
	}
	
}
