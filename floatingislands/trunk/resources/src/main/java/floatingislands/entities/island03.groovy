package floatingislands.entities;

builder.entity {
	
	parameters.islandImage = utils.resources.image("island03")
	parameters.startPosition = utils.vector(0f, 80f)
	parameters.bounds = utils.rectangle(-50, -60, 110, 20)
	
	parent("floatingislands.entities.island", parameters)
	
}
