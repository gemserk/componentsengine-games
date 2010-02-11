package towerofdefense.entities;

builder.entity("bullet-${Math.random()}") {

	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("radius", parameters.radius);

	component("movement")
		property("movement.velocity", parameters.direction.scale(parameters.maxVelocity))
		property("movement.maxVelocity", parameters.maxVelocity)
		propertyRef("movement.position", "position")
	
	component("imagerenderer")
		property("image", image("towerofdefense.images.bullet"))
		property("color", parameters.color)
		
	component("bullethit")
		property("bullethit.targetTag", "critter")
		propertyRef("bullethit.position", "position")
		propertyRef("bullethit.radius", "radius")
		
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
//		def entity = message.entity		
//		if (message.getProperty("source").get() == entity)
//			world.queueRemoveEntity(entity)
	}
		
}
