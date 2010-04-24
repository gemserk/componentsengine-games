package floatingislands.entities;

builder.entity {
	
	parameters.islandImage = utils.resources.image("island01") 
	parameters.startPosition = utils.vector(0, 100f)
	parameters.bounds = utils.rectangle(-45, -50, 95, 20)
	
	parent("floatingislands.entities.island", parameters)
	
}
