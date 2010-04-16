package jylonwars.scenes;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.games.jylonwars.ExplosionComponent 

import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	property("dead",false)
	property("playtime",0)
	
	
	component(new LabelComponent("fpslabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,20))
		property("message", "FPS: {0}")
		property("value",{utils.custom.gameContainer.getFPS()})
	}
	
	
	
	child(entity("world"){
		
		property("crittersdead",0)
		
		component(new ProcessingDisablerComponent("gameovercomponent")){
			property("enabled",{!entity.parent.dead})
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
			property("bounds", utils.rectangle(0,0,800,600));
		}
		
		child(entity("ship1"){
			parent("jylonwars.entities.ship",[player:"player1",position:utils.vector(400,300), bounds:utils.rectangle(20,20,760,560)])
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
		
		component(utils.components.genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
			//message.critter.position = newCritterPosition(entity.ship.position,200)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(message.critter))
			entity.crittersdead+=1
		})
		
		component(utils.components.genericComponent(id:"shipcollisionhandler", messageId:"shipcollision"){ message ->
			entity.parent.dead = true
		})
		
		component(new ComponentFromListOfClosures("playtimecomponent",[{ UpdateMessage message ->
			entity.parent.playtime+=message.delta
		}]))
		
		child(entity("critterspawner"){
			
			property("interval",utils.interval(800,1200))
			
			property("timer",new CountDownTimer(500))
			
			entity.timer.reset()
			
			component(new TimerComponent("spawnertimer")){
				propertyRef("timer","timer")
				property("trigger", utils.custom.triggers.genericMessage("spawntriggered") {})
			}
			
			component(utils.components.genericComponent(id:"spawntriggeredhandler", messageId:"spawntriggered"){ message ->
				
				entity.timer.reset()
				
				def ship = entity.parent.ship
				def critter = entity("critter-${Math.random()}",{
					parent("jylonwars.entities.critter",[position:newCritterPosition(ship.position,200),color:utils.color(0,1,0),speed:0.1f,image:utils.resources.image("ship")])
				})
				
				messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(critter,entity.parent))
			})
			
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
			}
			mouse {
				move(eventId:"lookAt") { message ->
					message.x = position.x
					message.y = position.y
				}
			}
		}
		
		
		property("playtime", {(float)(entity.parent.playtime/1000f)})
		
		child(entity("playTimeLabel"){
			
			parent("gemserk.gui.label", [
			font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
			position:utils.vector(700f, 50f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-50f, -20f, 100f, 40f),
			align:"left",
			valign:"top"
			])
			
			property("playtime", {entity.parent.playtime})
			property("message", {"Time: ${entity.playtime}".toString()})
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
			
			property("crittersDead", {entity.parent.crittersdead})
			property("message", {"CrittersDead: ${entity.crittersDead}".toString()})
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
			
			property("message", {"FPS: ${utils.custom.gameContainer.getFPS()}".toString()})
		})
		
		component(new ExplosionComponent("explosions")) {
			property("startColor", utils.color(0.8f, 0f, 0f, 1f))
			property("endColor", utils.color(1f, 0.5f, 0.5f, 0.6f))
			property("particlesCount", 100)
			property("time", 800)
		}
	})
	
	child(entity("gameover"){
		
		def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
		
		property("dead", {entity.parent.dead})
		property("playtime", {(float)(entity.parent.playtime/1000f)})
		
		component(new ProcessingDisablerComponent("gameovercomponent")){ propertyRef("enabled", "dead") }
		
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
			
			property("playtime", {entity.parent.playtime})
			property("message", {"Your time was: ${entity.playtime} seconds".toString()})
		})
		
		component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
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
}
