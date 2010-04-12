package jylonwars.scenes;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.SlickRenderMessage;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent;
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
	
	Random random = new Random()
	
	10.times {		
		child(entity("enemy-$it".toString()){
			parent("jylonwars.entities.critter",[position:utils.vector(random.nextInt(800),random.nextInt(600)),color:utils.color(0,1,0),speed:0.1f,image:utils.resources.image("ship")])
		})
	}
	
	
	
	component(utils.components.genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		message.critter.position = utils.vector(random.nextInt(800),random.nextInt(600))
	})
	
	component(new ComponentFromListOfClosures("moveHandler",[{ GenericMessage message ->
		if(!message.id.startsWith("player1.move"))
			return
			
		println message.id
	}]))
	
	
	
	
	
	input("inputmapping"){
		keyboard {
			press(button:"left",eventId:"player1.move.test.left.press")
			release(button:"left",eventId:"player1.move.test.left.release")
			hold(button:"left",eventId:"player1.move.left")
			hold(button:"right",eventId:"player1.move.right")
			hold(button:"up",eventId:"player1.move.up")
			hold(button:"down",eventId:"player1.move.down")
			hold(button:"a",eventId:"player1.move.left")
			hold(button:"d",eventId:"player1.move.right")
			hold(button:"w",eventId:"player1.move.up")
			hold(button:"s",eventId:"player1.move.down")
		}
		mouse {
			move(eventId:"lookAt") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
	component(new LabelComponent("fps")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,20))
		property("message", "FPS: {0}")
		property("value",{utils.custom.gameContainer.getFPS()})
	}
	
}
