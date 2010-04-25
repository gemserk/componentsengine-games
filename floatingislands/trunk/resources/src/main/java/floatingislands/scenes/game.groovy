package floatingislands.scenes

import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 



import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import floatingislands.GroovyBootstrapper 

builder.entity("game") {
	
	new GroovyBootstrapper();
	
	def gameProperties = utils.custom.gameStateManager.gameProperties
	
	def currentScene = gameProperties.currentScene ?: 1
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	def font2 = utils.resources.fonts.font([italic:false, bold:false, size:36])
	
	def scenes = [1:[startPosition:utils.vector(100, 50), islands:[
			[type:"floatingislands.entities.island01", position:utils.vector(100,150)],
			[type:"floatingislands.entities.island02", position:utils.vector(230,300)],
			[type:"floatingislands.entities.island03", position:utils.vector(350,230)],
			[type:"floatingislands.entities.island04", position:utils.vector(550,280)]
			]], 2:[startPosition:utils.vector(320, 400), islands:[
			[type:"floatingislands.entities.island02", position:utils.vector(320,450)],
			[type:"floatingislands.entities.island02", position:utils.vector(220,400)],
			[type:"floatingislands.entities.island03", position:utils.vector(150,340)],
			[type:"floatingislands.entities.island04", position:utils.vector(234,250)],
			[type:"floatingislands.entities.island01", position:utils.vector(360,230)],
			]]]
	
	def scene = scenes[currentScene]
	
	property("gamestate", "playing")
	
	component(utils.components.genericComponent(id:"lastIslandReachedHandler", messageId:"lastIslandReached"){ message ->
		entity.gamestate = "sceneFinished"
	})
	
	child(entity("world") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "playing"})
			property("exclusions", [SlickRenderMessage.class])
		}
		
		parent("floatingislands.scenes.world", [scene:scene])
		
	})
	
	child(entity("sceneFinishedState") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "sceneFinished"})
		}
		
		component(new RectangleRendererComponent("rectangle")) {
			property("position", utils.vector(0,0))
			property("rectangle", utils.rectangle(40,40, 560, 400))
			property("lineColor", utils.color(0,0,0,0))
			property("fillColor", utils.color(0.5f,0.5f,1f,0.4f))
			property("cornerRadius", 10)
		}
		
		child(entity("gameOverLabel"){
			
			parent("gemserk.gui.label", [
			font:font2,
			position:utils.vector(320, 240),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "Scene completed")
		})
		
		component(utils.components.genericComponent(id:"nextSceneHanlder", messageId:"nextScene"){ message ->
			utils.custom.gameStateManager.gameProperties.currentScene = currentScene+1
			if (utils.custom.gameStateManager.gameProperties.currentScene> scenes.size())
				utils.custom.gameStateManager.gameProperties.currentScene = 1
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
			property("enabled", {entity.parent.gamestate == "gameover"})
		}
		
		component(new RectangleRendererComponent("rectangle")) {
			property("position", utils.vector(0,0))
			property("rectangle", utils.rectangle(40,40, 560, 400))
			property("lineColor", utils.color(0,0,0,0))
			property("fillColor", utils.color(0.5f,0.5f,1f,0.4f))
			property("cornerRadius", 10)
		}
		
		child(entity("gameOverLabel"){
			
			parent("gemserk.gui.label", [
			font:font2,
			position:utils.vector(320, 240),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "Game Over")
		})
		
		component(utils.components.genericComponent(id:"restartGameHandler", messageId:"restartGame"){ message ->
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
