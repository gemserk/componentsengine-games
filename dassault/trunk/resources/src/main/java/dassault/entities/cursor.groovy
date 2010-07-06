package dassault.entities

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

builder.entity {
	
	tags("cursor")
	
	property("position", utils.vector(0,0))
	property("camera", parameters.camera)
	
	component(utils.components.genericComponent(id:"updateCameraPosition", messageId:["update"]){ message ->
		entity.position = entity.camera.mousePosition.copy()
	})
	
	component(new ImageRenderableComponent("renderCursor")) {
		propertyRef("position", "position")
		property("image", utils.resources.image("cursor"))
		property("direction", utils.vector(1,0))
		property("layer", 50)
	}
	
}
