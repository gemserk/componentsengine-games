
package zombierockers.entities
import com.gemserk.componentsengine.commons.components.DisablerComponent;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 


builder.entity("ball-${Math.random()}") {
	
	tags("ball", "nofriction")
	
	property("color",parameters.color ?: utils.color(1,0,0))
	property("direction", utils.vector(1,0))
	property("radius",parameters.radius)
	property("state",parameters.state)
	
	property("pathTraversal", null)
	property("position", {entity.pathTraversal.position})
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("lineColor", utils.color(0,0,0,1))
		property("fillColor", parameters.color)
	}
	
}

