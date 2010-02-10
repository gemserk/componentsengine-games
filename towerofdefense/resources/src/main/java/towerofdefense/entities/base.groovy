package towerofdefense.entities;

builder.entity {

	tags("base")

	property("position", parameters.position)
	property("radius", parameters.radius)
	property("direction", parameters.direction)
	
	component("circlerenderer")
		property("circle.lineColor", parameters.lineColor)
		property("circle.fillColor", parameters.fillColor)
		propertyRef("circle.position", "position")
		propertyRef("circle.radius", "radius")
		propertyRef("circle.radius", "radius")

}
