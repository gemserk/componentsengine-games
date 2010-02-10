package towerofdefense.entities;

builder.entity {

	tags("path")

	property("path", parameters.path)
	
	component("pathrenderer")
		property("path.lineColor", parameters.lineColor)
		propertyRef("path.path", "path")		
	
	

}

