package towerofdefense.entities;

builder.entity {

	tags("spawner")

	property("position", parameters.position)
	
	component("circlerenderer")
		property("circle.radius", 10.0f)
		propertyRef("circle.position", "position")

	component("creator")
		property("creator.spawnDelay", parameters.spawnDelay)
		property("creator.template", parameters.template)
		property("creator.instanceParameters", parameters.instanceParameters)
		propertyRef("creator.position", "position")
	
	
}
