package grapplinghookus.entities

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

builder.entity {
	
	tags("cursor")
	
	property("position", utils.vector(0,0))
	
	component(new ImageRenderableComponent("renderCursor")) {
		propertyRef("position", "position")
		property("image", utils.resources.image("cursor"))
		property("direction", utils.vector(1,0))
		property("layer", 50)
	}
	
	component(utils.components.genericComponent(id:"updateCursorHandler", messageId:"updateCursor"){ message ->
		entity.position = utils.vector(message.x, message.y)
	})
	
	input("inputmapping"){
		mouse {
			move(eventId:"updateCursor") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
}
