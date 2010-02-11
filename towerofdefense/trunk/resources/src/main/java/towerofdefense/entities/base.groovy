package towerofdefense.entities;

builder.entity {
	
	tags("base")
	
	property("position", parameters.position)
	property("radius", parameters.radius)
	property("direction", parameters.direction)
	
	component("circlerenderer"){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
	}
	
	component("remover"){
		propertyRef("position", "position")
		propertyRef("range", "radius")
	}
	
}
