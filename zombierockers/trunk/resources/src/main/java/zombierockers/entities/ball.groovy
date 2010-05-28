package zombierockers.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 


builder.entity("ball-${Math.random()}") {
	
	tags("ball", "nofriction")
	
	property("position", parameters.position)
	property("color",parameters.color ?: utils.color(1,0,0))
	property("direction", utils.vector(1,0))
	property("radius",parameters.radius)
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("lineColor", utils.color(0,0,0,1))
		property("fillColor", parameters.color)
	}
//	component(utils.components.genericComponent(id:"moveBallHandler", messageId:["moveBall"]){ message ->
//		if(message.ball != entity)
//			return
//		entity.position = message.position
//	})
	
	
//		component(new ImageRenderableComponent("imagerenderer")) {
//			property("image",utils.resources.image("ship"))
//			propertyRef("color", "color")
//			propertyRef("position", "position")
//			propertyRef("direction", "direction")
//			property("size", utils.vector(0.7f, 0.7f))
//		}
	
}

