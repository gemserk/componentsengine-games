package towerofdefense.entities;

builder.entity("tower-${Math.random()}") {

	tags("tower")

	property("position", parameters.position)
	property("direction", parameters.direction)
	property("radius", parameters.radius)
	
	property("targetEntity", null)
	
	component("circlerenderer")
		property("circle.lineColor", parameters.lineColor)
		property("circle.fillColor", parameters.fillColor)
		propertyRef("circle.position", "position")
		propertyRef("circle.radius", "radius")
		
	component("faceTarget")
		propertyRef("faceTarget.position", "position")
		propertyRef("faceTarget.direction", "direction")
		propertyRef("faceTarget.targetEntity", "targetEntity")
	
	component("selectTarget")	
		property("selectTarget.targetTag", "critter")
		propertyRef("selectTarget.targetEntity", "targetEntity")
		propertyRef("selectTarget.radius", "radius")
		propertyRef("selectTarget.position", "position")
	
	component("imagerenderer")
		property("image", image("towerofdefense.images.tower1"))
		property("color", parameters.color)
		
	component("shooter")
		property("shooter.template", parameters.template)
		property("shooter.reloadTime", parameters.reloadTime)
		property("shooter.instanceParameters", parameters.instanceParameters)
		propertyRef("shooter.position", "position")
		propertyRef("shooter.targetEntity", "targetEntity")
	
}
