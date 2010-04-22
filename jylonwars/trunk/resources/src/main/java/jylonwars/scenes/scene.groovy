package jylonwars.scenes;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;

import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	component(new LabelComponent("fpslabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,20))
		property("message", "FPS: {0}")
		property("value",{
			utils.custom.gameContainer.getFPS()
		})
	}
	
	child(entity("world"){
		
		property("enabled", {entity.parent.gameState == "playing" })
		property("crittersdead",0)
		property("bombs",3)
		
		property("bounds",utils.rectangle(0,0,800,600))
		
		component(new ProcessingDisablerComponent("disableStateComponent")){
			propertyRef("enabled","enabled")
			property("exclusions",[SlickRenderMessage.class])
		}
		
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
			//			entity.parent.dead = true
			entity.parent.gameState = "gameover"
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
		})
	})
	
	child(entity("gameover"){
		
		def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
		
		property("enabled", {entity.parent.gameState == "gameover" })
		property("playtime", {
			(float)(entity.parent.playtime/1000f)
		})
		
		component(new ProcessingDisablerComponent("disableStateComponent")){  propertyRef("enabled", "enabled") }
		
		def labelRectangle = utils.rectangle(-240,-50,480,100)
		
		component(new RectangleRendererComponent("background")) {
			property("position",utils.vector(400,300))
			property("rectangle", labelRectangle)
			property("cornerRadius", 3)
			property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
			property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
		}
		
		child(entity("deadLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(400f, 300f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:labelRectangle,
			align:"center",
			valign:"center"
			])
			
			property("playtime", {entity.parent.playtime })
			property("message", {"Your time was: ${entity.playtime} seconds".toString() })
		})
		
		component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
			//			backgroundMusic.stop();
			utils.custom.game.loadScene("jylonwars.scenes.scene");
		})
		
		input("inputmapping"){
			keyboard {
				press(button:"return",eventId:"restart")
				press(button:"space",eventId:"restart")
			}
			mouse {
				press(button:"left", eventId:"restart")
				press(button:"right", eventId:"restart")
			}
		}
	})
	
	child(entity("paused"){
		
		def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
		
		property("enabled", {entity.parent.gameState == "paused" })
		
		component(new ProcessingDisablerComponent("disableStateComponent")){  propertyRef("enabled", "enabled") }
		
		def labelRectangle = utils.rectangle(-240,-50,480,100)
		
		component(new RectangleRendererComponent("background")) {
			property("position",utils.vector(0,0))
			property("rectangle", utils.rectangle(0,0, 800, 600))
			property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
			property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
		}
		
		child(entity("deadLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(400f, 300f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:labelRectangle,
			align:"center",
			valign:"center"
			])
			
			property("message", "Paused, press click to continue...")
		})
		
		component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
			entity.parent.gameState = "playing"
		})
		
		input("inputmapping"){
			keyboard {
				press(button:"return",eventId:"resumeGame")
			}
			mouse {
				press(button:"left", eventId:"resumeGame")
				press(button:"right", eventId:"resumeGame")
			}
		}
	})
}
