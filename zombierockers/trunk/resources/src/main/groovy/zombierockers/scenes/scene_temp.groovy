package zombierockers.scenes;

import gemserk.utils.GroovyBootstrapper;

builder.entity { 
	
	new GroovyBootstrapper();
	
	parameters.levels = ScenesDefinitions.scenes(utils)
	
	parent("zombierockers.scenes.sceneimpl", parameters);
	
}