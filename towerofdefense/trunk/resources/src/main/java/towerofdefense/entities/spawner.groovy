package towerofdefense.entities;

builder.entity {
	
	tags("spawner")
	
	property("position", parameters.position)
	
	component("circlerenderer"){
		property("radius", 10.0f)
		propertyRef("position", "position")
	}
	
	component("creator"){
		property("spawnDelay", parameters.spawnDelay)
		property("template", parameters.template)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
	}
	
	
}
