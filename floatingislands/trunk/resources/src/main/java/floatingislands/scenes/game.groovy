package floatingislands.scenes
import com.gemserk.componentsengine.timers.CountDownTimer;

import com.gemserk.componentsengine.commons.components.TimerComponent;


import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 



import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import floatingislands.GroovyBootstrapper 

builder.entity("game") {
	
	new GroovyBootstrapper();
	
	def resetGameProperties = { properties ->
		properties.currentScene = 0
		properties.lives = 5
		properties.jumpCount = 0
	}
	
	def gameProperties = utils.custom.gameStateManager.gameProperties
	
	gameProperties.currentScene = gameProperties.currentScene ?: 0
	gameProperties.lives = gameProperties.lives ?: 5
	gameProperties.jumpCount = gameProperties.jumpCount ?: 0
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	def font2 = utils.resources.fonts.font([italic:false, bold:false, size:48])
	
	def scenesDef = ["scenes/scene01.xml", "scenes/scene02.xml", "scenes/scene03.xml"]
	
	def loadScene = { scenes, number ->
		def scene = [islands:[]]
		
		def sceneDef = scenes[number]
		def sceneStream = this.getClass().getClassLoader().getResourceAsStream(sceneDef)
		def sceneXml = new XmlSlurper().parse(sceneStream)
		
		scene.startPosition = utils.vector(sceneXml.startPosition.x.toFloat(), sceneXml.startPosition.y.toFloat())
		sceneXml.island.each { islandXml ->
			scene.islands << [type:islandXml.type.text(), position:utils.vector(islandXml.position.x.toFloat(), islandXml.position.y.toFloat())]
		}
		
		return scene
	}
	
	property("gamestate", "playing")
	
	property("endSceneTimer", new CountDownTimer(2000))
	property("gameOverTimer", new CountDownTimer(2000))
	
	component(new TimerComponent("endSceneTimer")) {
		propertyRef("timer", "endSceneTimer")
		property("trigger", utils.custom.triggers.genericMessage("changeGameState") {
			// not used like the others ( message -> )
			message.gameState = "sceneFinished"
		})
	}
	
	component(new TimerComponent("gameOverTimer")) {
		propertyRef("timer", "gameOverTimer")
		property("trigger", utils.custom.triggers.genericMessage("changeGameState") { 
			message.gameState = "gameover" 
		})
	}
	
	component(utils.components.genericComponent(id:"lastIslandReachedHandler", messageId:"lastIslandReached"){ message ->
		entity.endSceneTimer.reset()
	})
	
	component(utils.components.genericComponent(id:"jumperDeadHandler", messageId:"jumperDead"){ message ->
		entity.gameOverTimer.reset()
	})
	
	component(utils.components.genericComponent(id:"changeGameStateHandler", messageId:"changeGameState"){ message ->
		entity.gamestate = message.gameState
	})
	
	child(entity("world") {
		
		property("windSound", utils.resources.sounds.sound("wind"))
		
		component(utils.components.genericComponent(id:"changeGameStateHandler", messageId:"changeGameState"){ message ->
			entity.windSound.stop()
		})
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "playing" })
			property("exclusions", [SlickRenderMessage.class])
		}
		
		parent("floatingislands.scenes.world", [scene:loadScene(scenesDef, gameProperties.currentScene), lives:gameProperties.lives, jumpCount:gameProperties.jumpCount])
		
	})
	
	child(entity("sceneFinishedState") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "sceneFinished" })
		}
		
		child(entity("gameOverLabel"){
			
			parent("gemserk.gui.label", [
			font:font2,
			position:utils.vector(320, 240),
			fontColor:utils.color(0.3f,0.7f,0.3f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "Scene completed")
		})
		
		component(utils.components.genericComponent(id:"nextSceneHanlder", messageId:"nextScene"){ message ->
			gameProperties.currentScene = gameProperties.currentScene+1
			
			gameProperties.lives = entity.parent.children["world"].lives
			gameProperties.jumpCount = entity.parent.children["world"].jumpCount
			
			if (gameProperties.currentScene>= scenesDef.size()) {
				
				// move to YOU FINISH THE GAME
				// resetGameProperties(gameProperties) next time...
				
				gameProperties.currentScene = 0
			}
			// lose current game state?
			utils.custom.game.loadScene("floatingislands.scenes.game");
		})
		
		input("inputmapping"){
			keyboard {
				press(button:"return", eventId:"nextScene")
				press(button:"space", eventId:"nextScene")
			}
			mouse {
				press(button:"left", eventId:"nextScene")
			}
		}
		
	})
	
	child(entity("gameOverState") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "gameover" })
		}
		
		child(entity("gameOverLabel"){
			
			parent("gemserk.gui.label", [
			font:font2,
			position:utils.vector(320, 240),
			fontColor:utils.color(0.8f,0.2f,0.2f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "Game Over")
		})
		
		component(utils.components.genericComponent(id:"restartGameHandler", messageId:"restartGame"){ message ->
			resetGameProperties(gameProperties)
			utils.custom.game.loadScene("floatingislands.scenes.game");
		})
		
		input("inputmapping"){
			keyboard {
				press(button:"return", eventId:"restartGame")
				press(button:"space", eventId:"restartGame")
			}
			mouse {
				press(button:"left", eventId:"restartGame")
			}
		}
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"up", eventId:"jump")
		}
		mouse {
			press(button:"left", eventId:"startJump")
			release(button:"left", eventId:"jump")
			move(eventId:"jumpDirectionChanged") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
}
