package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 


builder.entity {
	
	tags("button")
	
	property("mouseNotOverFillColor", parameters.mouseNotOverFillColor)
	property("mouseOverFillColor", parameters.mouseOverFillColor)
	property("disabledFillColor", utils.color(1.0f, 1.0f, 1.0f, 0.4f))
	property("fillColor", {entity.cursorOver ? entity.mouseOverFillColor : entity.mouseNotOverFillColor})
	
	parameters.onReleasedTrigger = parameters.trigger
	parameters.bounds = parameters.rectangle
	
	component(new RectangleRendererComponent("background")) {
		propertyRef("position", "position")
		propertyRef("rectangle", "bounds")
		//property("cornerRadius", 3)
		def entity = entity
		property("lineColor", parameters.lineColor != null ? parameters.lineColor : utils.color(0f,0f,0f,0f))
		property("fillColor", {entity.enabled ? entity.fillColor : entity.disabledFillColor})
	}
	
	if (parameters.icon != null) {
		component(new ImageRenderableComponent("iconRenderer")) {
			property("image", parameters.icon)
			property("color", utils.color(1f,1f,1f,1f))
			propertyRef("position", "position")
			propertyRef("direction", "direction")
		}
	}
	
	parent("gemserk.gui.button", parameters)
}
