package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.LabelComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 



builder.entity {
	
	tags("button")
	
	property("position", parameters.position)
	property("direction", utils.vector(1f,0f))
	property("fillColor", parameters.fillColor)
	property("bounding", parameters.rectangle)
	
	component(new RectangleRendererComponent("background")) {
		propertyRef("position", "position")
		propertyRef("direction","direction")
		propertyRef("rectangle", "bounding")
//		property("position", {entity.position})
//		property("direction",{entity.direction})
//		property("rectangle",{entity.bounding})
		property("lineColor", parameters.lineColor != null ? parameters.lineColor : utils.color(0f,0f,0f,0f))
		propertyRef("fillColor", "fillColor")
		property("fillColor", {entity.fillColor})
	}
	
	if (parameters.label != null) {
		component(new LabelComponent("textComponent")) {
			propertyRef("position","position")
			property("message","{0}")
			property("value", parameters.label)
			property("font", parameters.font)
		}
	}

}
