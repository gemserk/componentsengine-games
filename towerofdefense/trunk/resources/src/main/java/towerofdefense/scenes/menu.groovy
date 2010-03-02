package towerofdefense.scenes;

import com.gemserk.games.towerofdefense.GemserkGameState;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import org.newdawn.slick.GameContainer 
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import towerofdefense.GroovyBootstrapper 

builder.entity("menu") {
	
	new GroovyBootstrapper();
	
	property("playing", {utils.custom.gameStateManager.gameProperties.inGame})
	property("playingNextValue",false)
	
	def buttonMouseOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.2f)
	def buttonMouseNotOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.8f)
	
	def buttonFont = utils.resources.fonts.font([italic:false, bold:false, size:48])
	
	def menuX = 400
	def menuY = 40
	
	component(new ImageRenderableComponent("logo")) {
		property("position", utils.vector(menuX, menuY + 140))
		property("image", utils.resources.image("towerofdefense.images.logo"))
		property("direction", utils.vector(1,0))
	}
	
	child(template:"towerofdefense.entities.button", id:"buttonResume")	{
		position=utils.vector(menuX, menuY + 340)
		rectangle=utils.rectangle(-160, -50, 320, 100)
		label={entity.parent.playing?"Resume":"Select Scene"}
		lineColor=utils.color(0f, 0f, 1f, 0.5f)
		mouseNotOverFillColor=buttonMouseOverFillColor
		mouseOverFillColor=buttonMouseNotOverFillColor
		font=buttonFont
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("playOrResume") {
		}
	}
	
	child(template:"towerofdefense.entities.button", id:"buttonExit")	{
		position=utils.vector(menuX, menuY + 460)
		rectangle=utils.rectangle(-160, -50, 320, 100)
		label={entity.parent.playing?"End Scene":"Exit"}
		lineColor=utils.color(0f, 0f, 1f, 0.5f)
		mouseNotOverFillColor=buttonMouseOverFillColor
		mouseOverFillColor=buttonMouseNotOverFillColor
		font=buttonFont
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("exitOrEndScene") {
		}
	}
	
	//	property("resumeSound", utils.resources.sounds.sound("assets/sounds/button.wav"))
	
	genericComponent(id:"playOrResumeHandler", messageId:"playOrResume"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
	
		stateBasedGame.enterState(entity.playing ? 1 : 2, new FadeOutTransition(), new FadeInTransition())
		if(entity.playing){
			entity.playingNextValue = true
		}
		//		entity.resumeSound.play()
	}
	
	genericComponent(id:"exitOrEndSceneHandler", messageId:"exitOrEndScene"){ message ->
		if(entity.playing){
			entity.playingNextValue = false;
			StateBasedGame stateBasedGame = utils.custom.gameStateManager
			stateBasedGame.enterState(0, new FadeOutTransition(), new FadeInTransition())
		}else {	
			GameContainer gameContainer = utils.custom.gameContainer;
			gameContainer.exit();
		}
	}
	
	genericComponent(id:"leaveStateHandler", messageId:"leaveState"){ message ->
		utils.custom.gameStateManager.gameProperties.inGame=entity.playingNextValue
	}
	
	genericComponent(id:"dumpDebugHandler", messageId:"dumpDebug"){ message ->
		Entity.times.entrySet().sort({it.count }).each { entry ->  println "$entry.element - $entry.count" }
	}   
	
	
	genericComponent(id:"testSceneHandler", messageId:"goToTestScene"){ message ->
		def game = utils.custom.game
		game.loadScene("towerofdefense.scenes.testScene")
	}
	
	genericComponent(id:"goToSelectionSceneHandler", messageId:"goToSelectionScene"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		stateBasedGame.enterState(2, new FadeOutTransition(), new FadeInTransition())
		
	}
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"resume")
			press(button:"d",eventId:"dumpDebug")
			press(button:"t",eventId:"goToTestScene")
			press(button:"b",eventId:"goToSelectionScene")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			press(button:"right", eventId:"rightClick")
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}
	
	
}