package floatingislands.entities;

builder.entity {
	
	parameters.islandImage = utils.resources.image("island02")
	parameters.startPosition = utils.vector(0f, 50f)
	parameters.bounds = utils.rectangle(-50, -25, 103, 20)
	
	parent("floatingislands.entities.island", parameters)
	
}
