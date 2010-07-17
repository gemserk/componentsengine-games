package dassault.entities
import com.gemserk.commons.animation.interpolators.FloatInterpolator;
import com.gemserk.commons.slick.util.SlickCameraTransformImpl;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import com.gemserk.games.dassault.components.LinearMovementComponent;

import org.newdawn.slick.Graphics 

builder.entity {
	
	property("ownerId", parameters.ownerId)
	property("screen", parameters.screen)
	
	property("followMouse", parameters.followMouse)
	
	property("position", utils.vector(0,0))
	property("mouseRelativePosition", utils.vector(0,0))
	property("mousePosition", utils.vector(0,0))
	
	property("targetedPosition", utils.vector(0,0))
	
	property("camera", new SlickCameraTransformImpl((float)parameters.screen.width/2f, (float)parameters.screen.height/2f))
	
	// linear movement component
	
	component (new LinearMovementComponent("linearMovementComponent")) { propertyRef("position", "targetedPosition") }
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:"update"){ message ->
		
		def camera = entity.camera
		
		def screen = entity.screen
		
		// center screen, could be considered as start camera position
		def input = utils.custom.gameContainer.input
		
		def centerPosition = utils.vector((float)screen.width * 0.5f, (float)screen.height * 0.5f)
		def mousePosition = utils.vector((float) input.getMouseX(), (float) input.getMouseY())
		
		def targetedPosition = entity.targetedPosition.copy()
		
		if (!entity.followMouse) {
			
			camera.moveTo(targetedPosition.copy())
			
			def mouseWorldPosition = camera.getWorldPositionFromScreenPosition(mousePosition)
			
			entity.mouseWorldPosition = mouseWorldPosition
			entity.mouseRelativePosition = mouseWorldPosition.copy().sub(targetedPosition)
			entity.mousePosition = mouseWorldPosition
			
		} else {
			
			def mouseWorldPosition = camera.getWorldPositionFromScreenPosition(mousePosition)
			def midPoint = targetedPosition.copy().add(mouseWorldPosition).scale(0.5f)
			
			camera.moveTo(midPoint)
			
			entity.mouseWorldPosition = mouseWorldPosition
			entity.mouseRelativePosition = mouseWorldPosition.copy().sub(targetedPosition)
			entity.mousePosition = mouseWorldPosition
			
		}
		
	})
	
	component(utils.components.genericComponent(id:"toggleFollow", messageId:["toggleFollowMouse"]){ message ->
		entity.followMouse = !entity.followMouse
	})
	
	component(utils.components.genericComponent(id:"zoomHandler", messageId:"zoom"){ message ->
		if (message.cameraId != entity.id)
			return
		
		def zoom = entity.camera.zoom.x
		
		entity.zoomInterpolator = new FloatInterpolator(message.time ?: 0, zoom, message.end)
	})
	
	component(utils.components.genericComponent(id:"updateZoom", messageId:"update"){ message ->
		def zoomInterpolator = entity.zoomInterpolator
		if (zoomInterpolator == null)
			return
		zoomInterpolator.update(message.delta)
		
		def zoom = zoomInterpolator.interpolatedValue
		entity.camera.zoomTo(utils.vector(zoom, zoom))
		
		if (zoomInterpolator.finished) 
			entity.zoomInterpolator = null
	})
	
	//
	
	component(utils.components.genericComponent(id:"changePerspective", messageId:["render"]){ message ->
		def renderer = message.renderer
		
		def screen = entity.screen
		
		def position = entity.position
		def zoom = entity.zoom
		
		def camera = entity.camera
		
		renderer.enqueue( new ClosureRenderObject(-100, { Graphics g ->
			camera.pushTransform(g)
		}))
		
		renderer.enqueue( new ClosureRenderObject(100, { Graphics g ->
			camera.popTransform(g)
		}))
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"e",eventId:"toggleFollowMouse")
		}
		mouse {
		}
	}
	
}
