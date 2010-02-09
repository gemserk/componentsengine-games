builder.entity("${entityName}") {

	tags("bullet")

	component("renderer")
	component("movement")
	component("simplepath")
	
	property("color", parameters.color)
	property("size", 5.0f)

	property("movement.friction", 0.0f);
	property("movement.constSpeed", 1.0f);
	property("movement.turnRate", 0.1f);
	property("movement.speed", 0.0f);

	property("position", parameters.position);
	property("targetEntity", parameters.targetEntity);
	property("damage", parameters.damage);
}
