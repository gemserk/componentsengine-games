package dassault.scenes;

import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	child(id:"button1", template:"dassault.entities.button") { 
		position = utils.vector(100,100)
	}
	
	child(id:"button2", template:"dassault.entities.button") { 
		position = utils.vector(400,100)
	}
	
	input("inputmapping"){
		keyboard {

		}
		mouse {
			
		}
	}
}
