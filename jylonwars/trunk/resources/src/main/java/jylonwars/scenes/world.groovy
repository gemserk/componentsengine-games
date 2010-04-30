package jylonwars.scenes;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.games.jylonwars.data.Data;



builder.entity("world") {
	
	
	property("crittersdead",0)
	property("bombs",3)
	
	property("bounds",utils.rectangle(0,0,800,600))
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("background"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
	}
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	child(entity("ship1"){
		parent("jylonwars.entities.ship",[player:"player1",position:utils.vector(400,300), bounds:utils.rectangle(20,20,760,560)])
	})
	
	property("ship",{entity.children["ship1"] })
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
	
	component(utils.components.genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		entity.crittersdead+=1
		if(entity.crittersdead % 50 == 0)
			entity.bombs +=1
	})
	
	component(utils.components.genericComponent(id:"shipcollisionhandler", messageId:"shipcollision"){ message ->
		def dataStore = utils.custom.gameStateManager.gameProperties.dataStore
		
		dataStore.submit(new Data(tags:["score"], values:[name:"yo", playtime:entity.playtime, crittersdead:entity.crittersdead]))
		
		entity.parent.gameState = "gameover"
		
		messageQueue.enqueue(utils.genericMessage("refreshScores"){})	
	})
	
	component(new ComponentFromListOfClosures("playtimecomponent",[{ UpdateMessage message ->
		entity.parent.playtime+=message.delta
	}]))
	
	child(entity("critterspawner"){
		
		property("interval",utils.interval(800,1200))
		
		property("timer",new CountDownTimer(1000))
		
		entity.timer.reset()
		
		component(new TimerComponent("spawnertimer")){
			propertyRef("timer","timer")
			property("trigger", utils.custom.triggers.genericMessage("spawntriggered") {
			})
		}
		
		component(utils.components.genericComponent(id:"spawntriggeredhandler", messageId:"spawntriggered"){ message ->
			
			entity.timer.reset()
			
			def ship = entity.parent.ship
			def bounds = entity.parent.bounds
			def critterType = []
			critterType << "jylonwars.entities.followercritter"
			critterType << "jylonwars.entities.wanderercritter"
			critterType << "jylonwars.entities.avoidercritter"
			def critter = entity("critter-${Math.random()}",{
				parent(critterType[(random.nextInt(critterType.size()))],[position:newCritterPosition(ship.position,200),color:utils.color(0,1,0),speed:0.1f,image:utils.resources.image("ship"),bounds:bounds])
			})
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(critter,entity.parent))
		})
		
	})
	
	
	component(utils.components.genericComponent(id:"deployBombHandler", messageId:["tryDeployBomb"]){ message ->
		if(entity.bombs <= 0)
			return
		
		messageQueue.enqueue(utils.genericMessage("deployBomb") {})
		
		entity.bombs -=1
		
	})
	
	component(utils.components.genericComponent(id:"cheat", messageId:["addBombs"]){ message ->	
		entity.bombs +=1
	})
	
	
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
			
			
			//cheats
			hold(button:"b",eventId:"addBombs")
			hold(button:"l",eventId:"addLives")
			
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			
		}
		mouse {
			press(button:"right", eventId:"tryDeployBomb")
			move(eventId:"lookAt") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
	
	property("playtime", {
		(float)(entity.parent.playtime/1000f)
	})
	
	child(entity("playTimeLabel"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
		position:utils.vector(700f, 50f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("playtime", {entity.parent.playtime })
		property("message", {"Time: ${entity.playtime}".toString() })
	})
	
	child(entity("crittersDeadLabel"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
		position:utils.vector(60f, 50f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("crittersDead", {entity.parent.crittersdead })
		property("message", {"CrittersDead: ${entity.crittersDead}".toString() })
	})
	
	child(entity("bombsLabel"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
		position:utils.vector(60f, 75f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("value", {entity.parent.bombs })
		property("message","Bombs: {0}")
		
	})
	
	child(entity("fpsLabel"){
		
		parent("gemserk.gui.label", [
		//			font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.vector(60f, 30f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("message", {"FPS: ${utils.custom.gameContainer.getFPS()}".toString() })
	})
	
	component(new ExplosionComponent("explosions")) {
	}
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		entity.parent.gameState = "paused"
		messageQueue.enqueue(utils.genericMessage("refreshScores"){})	
	})
}
