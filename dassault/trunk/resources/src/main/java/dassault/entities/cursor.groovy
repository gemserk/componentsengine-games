package dassault.entities

import com.gemserk.commons.animation.interpolators.ColorInterpolator;
import com.gemserk.commons.animation.interpolators.FloatInterpolator 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

builder.entity {
	
	tags("cursor")
	
	property("position", utils.vector(0,0))
	property("camera", parameters.camera)
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:"update"){ message ->
		entity.position = entity.camera.mousePosition.copy()
	})
	
	component(new ImageRenderableComponent("renderCursor")) {
		propertyRef("position", "position")
		property("image", utils.resources.image("cursor"))
		property("direction", utils.vector(1,0))
		property("size", {utils.vector(entity.zoom, entity.zoom)})
		property("layer", 50)
		propertyRef("color", "color")
	}
	
	component(utils.components.genericComponent(id:"droidFocusedHandler", messageId:"droidFocused"){ message ->
		def droidId = message.droidId
		def droid = entity.root.getEntityById(droidId)
		def owner = entity.root.getEntityById(droid.ownerId)
		
		entity.droidId = droidId
		
		entity.zoomInterpolator = new FloatInterpolator(300, entity.zoom, 0.7f)
		entity.colorInterpolator = new ColorInterpolator(300, entity.color, owner.color)
	})
	
	component(utils.components.genericComponent(id:"droidLostFocusHandler", messageId:"droidLostFocus"){ message ->
		entity.droidId = null
		entity.zoomInterpolator = new FloatInterpolator(300, entity.zoom, 0.9f)
		entity.colorInterpolator = new ColorInterpolator(300, entity.color, utils.color(1f,1f,1f,1f))
	})
	
	property("zoom", 0.9f)
	property("color", utils.color(1f,1f,1f,1f))
	
	component(utils.components.genericComponent(id:"updateZoom", messageId:"update"){ message ->
		def zoomInterpolator = entity.zoomInterpolator
		if (zoomInterpolator == null)
			return
		zoomInterpolator.update(message.delta)
		entity.zoom = zoomInterpolator.interpolatedValue
		if (zoomInterpolator.finished) 
			entity.zoomInterpolator = null
	})
	
	component(utils.components.genericComponent(id:"updateColor", messageId:"update"){ message ->
		def colorInterpolator = entity.colorInterpolator
		if (colorInterpolator == null)
			return
		colorInterpolator.update(message.delta)
		entity.color = colorInterpolator.interpolatedValue
		if (colorInterpolator.finished) 
			entity.colorInterpolator = null
	})
	
	component(utils.components.genericComponent(id:"droidDeadHandler", messageId:"droidDead"){ message ->
		if (entity.droidId == null)
			return
			
		if (entity.droidId != message.droid.id)
			return
			
		messageQueue.enqueue(utils.genericMessage("droidLostFocus") { })
	})
}
