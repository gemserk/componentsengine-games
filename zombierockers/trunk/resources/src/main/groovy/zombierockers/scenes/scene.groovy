package zombierockers.scenes;

builder.entity("game") { 
	
	parameters.levels = ScenesDefinitions.scenes(utils)
	
	parent("zombierockers.scenes.sceneimpl", parameters);
	
}