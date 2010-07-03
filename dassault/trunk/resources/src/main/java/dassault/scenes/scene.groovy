package dassault.scenes;

import org.newdawn.slick.Input;

import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	child(id:"button1", template:"dassault.entities.button") { 
		position = utils.vector(200,560)
	}
	
	child(id:"button2", template:"dassault.entities.button") { 
		position = utils.vector(600,560)
	}
	
	child(id:"playerController", template:"dassault.entities.keyboardcontroller") {
		player = "player1"
		leftKey = Input.KEY_LEFT
		rightKey = Input.KEY_RIGHT
		upKey = Input.KEY_UP
		downKey = Input.KEY_DOWN
	}
	
	child(entity("droid1") {
		tags("player1")
		parent("dassault.entities.droid",[position:utils.vector(300,400)])
	} )
	
	child(id:"droid2", template:"dassault.entities.droid") { 
		position = utils.vector(200,200)
	}
	
	child(id:"obstacle1", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,100)
		bounds = utils.rectangle(-50, -10, 100, 20)
	}
	
	// scene limits as obstacles
	
	child(id:"sceneLimit1", template:"dassault.entities.obstacle") { 
		position = utils.vector(0,300)
		bounds = utils.rectangle(-10, -300, 20, 600)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit2", template:"dassault.entities.obstacle") { 
		position = utils.vector(800,300)
		bounds = utils.rectangle(-10, -300, 20, 600)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit3", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,0)
		bounds = utils.rectangle(-400, -10, 800, 20)
		color = utils.color(0,0,0,1)
	}

	child(id:"sceneLimit4", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,600)
		bounds = utils.rectangle(-400, -10, 800, 20)
		color = utils.color(0,0,0,1)
	}
	
	input("inputmapping"){
		keyboard {

		}
		mouse {
			
		}
	}
}
