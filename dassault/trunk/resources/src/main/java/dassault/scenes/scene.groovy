package dassault.scenes;

import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	child(id:"button1", template:"dassault.entities.button") { 
		position = utils.vector(200,560)
	}
	
	child(id:"button2", template:"dassault.entities.button") { 
		position = utils.vector(600,560)
	}
	
	child(id:"droid1", template:"dassault.entities.droid") { 
		position = utils.vector(300,300)
	}
	
	child(id:"droid2", template:"dassault.entities.droid") { 
		position = utils.vector(200,200)
	}
	
	input("inputmapping"){
		keyboard {

		}
		mouse {
			
		}
	}
}
