package floatingislands.entities;

builder.entity {
	
	parameters.islandImage = utils.resources.image("island04")
	parameters.startPosition = utils.vector(0f, 50f)
	parameters.bounds = utils.rectangle(-40, -30, 75, 20)
	
	parent("floatingislands.entities.island", parameters)
	
}
