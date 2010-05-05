package floatingislands.scenes

import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;


import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 



import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 

import floatingislands.GroovyBootstrapper 

builder.entity("game") {
	
	new GroovyBootstrapper();
	
	def resetGameProperties = { properties ->
		properties.currentScene = 0
		properties.jumpCount = 0
	}
	
	def gameProperties = utils.custom.gameStateManager.gameProperties
	
	gameProperties.currentScene = gameProperties.currentScene ?: 0
	gameProperties.jumpCount = gameProperties.jumpCount ?: 0
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	def font2 = utils.resources.fonts.font([italic:false, bold:false, size:48])
	
	def scenesDef = ["scenes/scene01.xml", "scenes/scene02.xml", "scenes/scene03.xml", "scenes/scene04.xml", "scenes/scene05.xml", "scenes/scene06.xml"]
	
	def loadScene = { scenes, number ->
		def scene = [islands:[]]
		
		def sceneDef = scenes[number]
		def sceneStream = this.getClass().getClassLoader().getResourceAsStream(sceneDef)
		def sceneXml = new XmlSlurper().parse(sceneStream)
		
		scene.startPosition = utils.vector(sceneXml.startPosition.@x.toFloat(), sceneXml.startPosition.@y.toFloat())
		sceneXml.island.each { islandXml ->
			scene.islands << [type:islandXml.type.text(), position:utils.vector(islandXml.position.@x.toFloat(), islandXml.position.@y.toFloat())]
		}
		
		return scene
	}
	
	property("gamestate", "playing")
	
	component(utils.components.genericComponent(id:"changeGameStateHandler", messageId:"changeGameState"){ message ->
		gameProperties.jumpCount = entity.children["world"].jumpCount
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
		
		parent("floatingislands.scenes.world", [scene:loadScene(scenesDef, gameProperties.currentScene), 
				jumpCount:gameProperties.jumpCount,
				currentLevel:gameProperties.currentScene+1, 
				levelsCount:scenesDef.size()])
		
	})
	
	child(entity("sceneFinishedState") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "sceneFinished" })
		}
		
		child(entity("sceneFinishedLabel"){
			
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
		
		child(entity("pressClickLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(320, 440),
			fontColor:utils.color(0.0f,0.0f,0.0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "press click to continue...")
		})	
		
		component(utils.components.genericComponent(id:"nextSceneHanlder", messageId:"nextScene"){ message ->
			gameProperties.currentScene = gameProperties.currentScene+1
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
	
	child(entity("gameFinishedState") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "gameFinished" })
		}
		
		child(entity("gameFinishedLabel"){
			
			parent("gemserk.gui.label", [
			font:font2,
			position:utils.vector(320, 240),
			fontColor:utils.color(0.0f,0.0f,0.0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "The End: thanks for playing")
		})
		
		child(entity("resultsLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(320, 390),
			fontColor:utils.color(0.0f,0.0f,0.0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", {"you used $gameProperties.jumpCount jumps".toString()})
		})
		
		child(entity("pressClickLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(320, 440),
			fontColor:utils.color(0.0f,0.0f,0.0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "press click to restart game...")
		})		
		
		component(utils.components.genericComponent(id:"nextSceneHanlder", messageId:"nextScene"){ message ->
			resetGameProperties(gameProperties)
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
	
	child(entity("pausedGameState") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "paused" })
		}
		
		component(new RectangleRendererComponent("rectangle")) {
			property("position", utils.vector(0,0))
			property("rectangle", utils.rectangle(0,0, 640, 480))
			property("lineColor", utils.color(1,1,1,0f))
			property("fillColor", utils.color(0,0,0,0.1f))
		}
		
		child(entity("pausedClickLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(320, 440),
			fontColor:utils.color(0.0f,0.0f,0.0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "Paused, press return to continue...")
		})	
		
		component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
			entity.parent.gamestate = "playing"
		})
		
		input("inputmapping"){
			keyboard {
				press(button:"return", eventId:"resumeGame")
				press(button:"space", eventId:"resumeGame")
				press(button:"escape", eventId:"resumeGame")
			}
		}
		
	})
	
	component(utils.components.genericComponent(id:"restartGameHandler", messageId:"restartGame2"){ message ->
		resetGameProperties(gameProperties)
		utils.custom.game.loadScene("floatingislands.scenes.game");
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"r", eventId:"restartGame2")
		}
	}
}
