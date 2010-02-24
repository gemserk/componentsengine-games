package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

builder.entity {
	
	tags("button")
	
	property("position", parameters.position)
	property("direction", utils.vector(1f,0f))
	
	component(new ImageRenderableComponent("towerRenderer")) {
		property("image", parameters.towerImage)
		property("color", utils.color(1f,1f,1f,1f))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	component(new ImageRenderableComponent("cannonRenderer")) {
		property("image", parameters.cannonImage)
		property("color", utils.color(1f,1f,1f,1f))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
}
