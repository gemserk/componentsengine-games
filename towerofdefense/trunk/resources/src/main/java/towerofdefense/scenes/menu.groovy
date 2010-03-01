package towerofdefense.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import org.newdawn.slick.GameContainer 
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import towerofdefense.GroovyBootstrapper 

builder.entity("menu") {
	
	new GroovyBootstrapper();
	
	property("playing", false)
	
	def buttonMouseOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 1.0f)
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
		label={entity.parent.playing?"Resume":"Play"}
		
		color=buttonMouseOverFillColor
		
		mouseNotOverImage=utils.resources.image("towerofdefense.images.menubutton")
		mouseOverImage=utils.resources.image("towerofdefense.images.menubutton_over")
		
		font=buttonFont
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("resume") {
		}
	}
	
	child(template:"towerofdefense.entities.button", id:"buttonExit")	{
		position=utils.vector(menuX, menuY + 460)
		rectangle=utils.rectangle(-160, -50, 320, 100)
		label="Exit"
		
		color=buttonMouseOverFillColor
		
		mouseNotOverImage=utils.resources.image("towerofdefense.images.menubutton")
		mouseOverImage=utils.resources.image("towerofdefense.images.menubutton_over")
		
		font=buttonFont
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("exit") {
		}
	}
	
	//	property("resumeSound", utils.resources.sounds.sound("assets/sounds/button.wav"))
	
	genericComponent(id:"resumeHandler", messageId:"resume"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		stateBasedGame.enterState(1, new FadeOutTransition(), new FadeInTransition())
		
		//		entity.resumeSound.play()
	}
	
	genericComponent(id:"exitHandler", messageId:"exit"){ message ->
		GameContainer gameContainer = utils.custom.gameContainer;
		gameContainer.exit();
	}
	
	genericComponent(id:"leaveStateHandler", messageId:"leaveState"){ message ->
		entity.playing=true
	}
	
	genericComponent(id:"dumpDebugHandler", messageId:"dumpDebug"){ message ->
		Entity.times.entrySet().sort({it.count }).each { entry ->  println "$entry.element - $entry.count" }
	}   
	
	
	genericComponent(id:"testSceneHandler", messageId:"goToTestScene"){ message ->
		def game = utils.custom.game
		game.loadScene("towerofdefense.scenes.testScene")
	}
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"resume")
			press(button:"d",eventId:"dumpDebug")
			press(button:"t",eventId:"goToTestScene")
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