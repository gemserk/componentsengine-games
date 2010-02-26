package towerofdefense.scenes;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import towerofdefense.GroovyBootstrapper;

builder.entity("menu") {
	
	new GroovyBootstrapper();
	
	def buttonMouseOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.2f)
	def buttonMouseNotOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.8f)
	
	child(template:"towerofdefense.entities.towerbutton", id:"buttonResume")	{
		position=utils.vector(300, 200)
		rectangle=utils.rectangle(-15, -20, 120, 50)
		label="Resume"
		mouseNotOverFillColor=buttonMouseOverFillColor
		mouseOverFillColor=buttonMouseNotOverFillColor
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("resume") {
		}
	}
	
	child(template:"towerofdefense.entities.towerbutton", id:"buttonExit")	{
		position=utils.vector(300, 260)
		rectangle=utils.rectangle(-15, -20, 120, 50)
		label="Exit"
		mouseNotOverFillColor=buttonMouseOverFillColor
		mouseOverFillColor=buttonMouseNotOverFillColor
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("exit") {
		}
	}
	
	genericComponent(id:"resumeHandler", messageId:"resume"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager;
		stateBasedGame.enterState(0, new FadeOutTransition(), new FadeInTransition());
	}
	
	genericComponent(id:"exitHandler", messageId:"exit"){ message ->
		GameContainer gameContainer = utils.custom.gameContainer;
		gameContainer.exit();
	}
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"resume")
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