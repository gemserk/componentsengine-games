package towerofdefense.entities;

builder.entity("tower-${Math.random()}") {

	tags("tower", "dragable", "ranged")

	component("radiusrenderer")
	component("shoot")
	component("imagerenderer")
	
	component("faceTargetComponent")
	propertyRef("faceTargetComponent.targetEntity", "selectTargetWithinRangeComponent.targetEntity")	
	
	component("selectTargetWithinRangeComponent")	
	property("selectTargetWithinRangeComponent.targetTag", "hero")
	property("selectTargetWithinRangeComponent.targetEntity", null)
	propertyRef("selectTargetWithinRangeComponent.visibleRadius", "radius")
	
	property("image", image("todh.images.tower1"))
	
	property("color", parameters.color)

	property("position", parameters.position)
	property("direction", parameters.direction)
	property("size", parameters.size)
	property("radius", parameters.radius)

	property("laser.enabled", false)
	property("reloadTime", parameters.reloadTime)
	property("template", parameters.template)
	property("damage", parameters.damage)

	property("targetId", "hero")

	genericComponent(id:"changePositionOnEvent", messageId:"move"){ message ->				
		message.entity.getProperty("position").set(message.getProperty("value").get())		
	}

	genericComponent(id:"changeRadiusOnEvent", messageId:"changeradius"){ message ->
		message.entity.getProperty("radius").set(message.getProperty("value").get())
	}
	
}
