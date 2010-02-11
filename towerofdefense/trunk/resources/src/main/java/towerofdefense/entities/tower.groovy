package towerofdefense.entities;

builder.entity("tower-${Math.random()}") {
	
	tags("tower")
	
	property("position", parameters.position)
	property("direction", parameters.direction)
	property("radius", parameters.radius)
	
	property("targetEntity", null)
	
	component("circlerenderer"){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
	}
	component("faceTarget"){
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		propertyRef("targetEntity", "targetEntity")
	}
	
	component("selectTarget")	{
		property("targetTag", "critter")
		propertyRef("targetEntity", "targetEntity")
		propertyRef("radius", "radius")
		propertyRef("position", "position")
	}
	
	component("imagerenderer")
	property("image", image("towerofdefense.images.tower1"))
	property("color", parameters.color)
	
	component("shooter"){
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
	}
	
}
