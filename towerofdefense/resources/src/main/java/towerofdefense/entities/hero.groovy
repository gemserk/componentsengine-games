builder.entity("${entityName}") {

	tags("hero")

	// component("renderer")
	component("movement")
	component("followpath")
	
	component("defense.trigger")

	component("defense1")
		property("defense1.enabled", false)
		property("defense1.size", parameters.defense1Size)
		property("defense1.color", parameters.defense1Color)
		property("defense1.energyRate", 0.01f)
		propertyRef("defense1.energy", "hitpoints")
	
	component("defense2")
		property("defense2.enabled", false)
		property("defense2.size", parameters.defense2Size)
		property("defense2.color", parameters.defense2Color)
		property("defense2.energyRate", 0.01f)
		propertyRef("defense2.energy", "hitpoints")
	
	component("bulletcollision")
	component("followpath-renderer")
	
	component("imagerenderer")
	property("image", image("todh.images.hero"))
	
	property("movement.friction", 0.5f)
	property("movement.constSpeed", 0.5f)
	property("movement.turnRate", 1.0f)
	property("movement.speed", 0.1f)

	property("position", parameters.position)
	propertyRef("direction", "movement.direction")
	property("size", 15.0f)
	property("color", parameters.color)
	property("hitpoints", parameters.hitpoints)
	
	property("followpath.path", parameters.followPath)

	component("containerrenderer")
		property("containerrenderer.position", parameters.hitpointsPosition);
		propertyRef("containerrenderer.container", "hitpoints");
	
	// properties of components
	propertyRef("defense1.position", "position")
	propertyRef("defense2.position", "position")
	
	propertyRef("defensetrigger1.enabled", "defense1.enabled")
	propertyRef("defensetrigger1.key", "defense1.key")

	propertyRef("defensetrigger2.enabled", "defense2.enabled")
	propertyRef("defensetrigger2.key", "defense2.key")
	
	genericComponent(id:"changePositionOnEvent", messageId:"move"){ message ->				
		message.entity.getProperty("position").set(message.getProperty("value").get())		
	}

}
