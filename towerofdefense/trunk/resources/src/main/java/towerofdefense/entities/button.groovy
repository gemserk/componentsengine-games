package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.games.towerofdefense.LabelComponent;
import com.gemserk.games.towerofdefense.components.render.RectangleRendererComponent;

builder.entity {
	
	tags("button")
	
	property("position", parameters.position)
	property("direction", utils.vector(1f,0f))
	property("fillColor", parameters.mouseNotOverFillColor)
	property("mouseNotOverFillColor", parameters.mouseNotOverFillColor)
	property("mouseOverFillColor", parameters.mouseOverFillColor)
	property("bounding", parameters.rectangle)
	
	property("enabled", true)
	property("disabledFillColor", utils.color(1.0f, 1.0f, 1.0f, 0.4f))
	
	property("mouseOver", false)
	
	property("trigger", parameters.trigger)
	
	genericComponent(id:"mouseOverHandler", messageId:"move"){ message ->
		def x = (float)(message.x - entity.position.x)
		def y = (float)(message.y - entity.position.y)
		
		if (entity.bounding.contains(x, y)) {
			entity.fillColor = entity.mouseOverFillColor
			entity.mouseOver = true
		}
		else {
			entity.fillColor = entity.mouseNotOverFillColor
			entity.mouseOver = false
		}
	}
	
	genericComponent(id:"mouseClickHandler", messageId:"click"){ message ->
		if (!entity.enabled)
			return
		
		if (! entity.mouseOver )
			return
		
		entity.trigger.trigger([:])
	}
	
	component(new RectangleRendererComponent("background")) {
		propertyRef("position", "position")
		propertyRef("rectangle", "bounding")
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

	if (parameters.label != null) {
		component(new LabelComponent("textComponent")) {
			propertyRef("position","position")
			property("message","{0}")
			property("value", parameters.label)
			property("font", parameters.font)
		}
	}

}
