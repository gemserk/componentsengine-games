package towerofdefense.entities;

builder.entity("bullet-${Math.random()}") {

	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("radius", parameters.damageRadius);

	component("movement")
		property("movement.velocity", parameters.direction.scale(parameters.maxVelocity))
		property("movement.maxVelocity", parameters.maxVelocity)
		propertyRef("movement.position", "position")
	
	component("imagerenderer")
		property("image", image("towerofdefense.images.bullet"))
		property("color", parameters.color)
}
