package jylonwars.scenes;
import org.lwjgl.opengl.Display;

import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	property("playtime",0)
	property("crittersdead",0)
	
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
		parent("jylonwars.entities.ship",[player:"player1",position:utils.vector(300,300), bounds:utils.rectangle(20,20,760,560)])
	})
	
	property("ship",{entity.children["ship1"]})
	//	child(entity("ship2"){
	//		parent("jylonwars.entities.ship",[player:"player2",position:utils.vector(500,300)])
	//	})
	
	Random random = new Random()
	
	def newCritterPosition = {def shipPosition, def distance ->
		def newPosition = shipPosition.copy()
		while(newPosition.distance(shipPosition) < distance)
			newPosition = utils.vector(random.nextInt(800),random.nextInt(600))
			
		return newPosition
		
	}
	
	
	10.times {		
		child(entity("enemy-$it".toString()){
			parent("jylonwars.entities.critter",[position:newCritterPosition(utils.vector(300,300),200),color:utils.color(0,1,0),speed:0.1f,image:utils.resources.image("ship")])
		})
	}
	
	
	
	component(utils.components.genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		message.critter.position = newCritterPosition(entity.ship.position,200)
		entity.crittersdead+=1
	})
	
	
	
	component(utils.components.genericComponent(id:"shipcollisionhandler", messageId:"shipcollision"){ message ->
		entity.collisions++
		
		message.targets.each { critter ->
			critter.position = newCritterPosition(entity.ship.position,200)
		}
	})
	
	property("collisions",0)
	
	
	
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"reloadScene"){ message ->
		if(utils.custom.gameStateManager.gameProperties.runningFromMain)
			utils.custom.game.loadScene("jylonwars.scenes.scene");
	})
	
	
	
	component(new ComponentFromListOfClosures("playtimecomponent",[{ UpdateMessage message ->
		entity.playtime+=message.delta
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
			press(button:"r",eventId:"reloadScene")
		}
		mouse {
			move(eventId:"lookAt") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
	component(new LabelComponent("fpslabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,20))
		property("message", "FPS: {0}")
		property("value",{utils.custom.gameContainer.getFPS()})
	}
	component(new LabelComponent("focuslabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,40))
		property("message", "Focus: {0}")
		property("value",{Display.isActive()})
	}
	component(new LabelComponent("collisionslabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,60))
		property("message", "Collisions: {0}")
		property("value",{entity.collisions})
	}
	component(new LabelComponent("playtimelabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,80))
		property("message", "Time: {0}")
		property("value",{(float)(entity.playtime/1000f)})
	}
	component(new LabelComponent("crittersdeadlabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,100))
		property("message", "CrittersDead: {0}")
		property("value",{entity.crittersdead})
	}
}
