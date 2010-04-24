package floatingislands.entities;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 

builder.entity {
	
	tags("island")

	property("image", parameters.islandImage)
	property("position", parameters.position)
	property("direction", utils.vector(1,0))
	property("bounds", parameters.bounds)
	property("startPosition", parameters.startPosition)
	
	component(new ImageRenderableComponent("imageRender")) {
		propertyRef("image", "image")
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}

	// for debug
	component(new RectangleRendererComponent("boundsRenderer")) {
		propertyRef("position", "position")
		propertyRef("rectangle", "bounds")
		property("lineColor", utils.color(1,1,1,1))
	}
	
}
