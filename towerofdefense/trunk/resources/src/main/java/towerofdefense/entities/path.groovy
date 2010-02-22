package towerofdefense.entities;

import com.gemserk.games.towerofdefense.PathRendererComponent 

builder.entity {
	
	tags("path")
	
	property("path", parameters.path)
	
	component(new PathRendererComponent("pathrenderer")){
		property("lineColor", utils.color(0.4f, 0.4f, 0.4f, 1.0f))
		property("lineWidth", 20.0f)
		propertyRef("path", "path")		
	}
	
	
	
}

