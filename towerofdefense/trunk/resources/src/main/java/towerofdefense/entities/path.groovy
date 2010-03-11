package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.PathRendererComponent 



builder.entity {
	
	tags("path")
	
	property("path", parameters.path)
	
	component(new PathRendererComponent("pathrenderer")){
		property("lineColor", utils.color(0.2f, 0.2f, 0.7f, 1.0f))
		property("lineWidth", 20.0f)
		propertyRef("path", "path")		
	}
	
	
	
}

