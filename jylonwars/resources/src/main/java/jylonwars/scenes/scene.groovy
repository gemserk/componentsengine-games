package jylonwars.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("background"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
	}
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		property("bounds", utils.rectangle(0,0,800,600));
	}
	
	
	child(entity("ship1"){
		parent("jylonwars.entities.ship",[player:"player1",position:utils.vector(300,300)])
	})
//	child(entity("ship2"){
//		parent("jylonwars.entities.ship",[player:"player2",position:utils.vector(500,300)])
//	})
	
	child(entity("enemy"){
		parent("jylonwars.entities.critter",[position:utils.vector(600,550),color:utils.color(0,1,0),speed:0.1f,image:utils.resources.image("ship")])
	})
	
	
	input("inputmapping"){
		keyboard {
			hold(button:"left",eventId:"player1.move.left")
			hold(button:"right",eventId:"player1.move.right")
			hold(button:"up",eventId:"player1.move.up")
			hold(button:"down",eventId:"player1.move.down")
			hold(button:"a",eventId:"player2.move.left")
			hold(button:"d",eventId:"player2.move.right")
			hold(button:"w",eventId:"player2.move.up")
			hold(button:"s",eventId:"player2.move.down")
		}
		mouse {
			move(eventId:"lookAt") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
}
