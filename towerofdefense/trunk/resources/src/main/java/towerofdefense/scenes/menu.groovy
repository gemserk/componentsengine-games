package towerofdefense.scenes;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import towerofdefense.GroovyBootstrapper;

builder.entity("menu") {
	
	new GroovyBootstrapper();
	
	property("playing", false)
	
	def buttonMouseOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.2f)
	def buttonMouseNotOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.8f)
	
	def buttonFont = utils.resources.fonts.font([italic:false, bold:false, size:48])
	
	def menuX = 400
	def menuY = 340
	
	component(new ImageRenderableComponent("logo")) {
		property("position", utils.vector(menuX, 140))
		property("image", utils.resources.image("towerofdefense.images.logo"))
		property("direction", utils.vector(1,0))
	}
	
//	component(new RectangleRendererComponent("menuRectangle")) {
//		property("position", utils.vector(menuX, menuY + 60))		
//		property("rectangle", utils.rectangle(-200, -160, 400, 320))
//		property("cornerRadius", 5)
//		property("lineColor", utils.color(0f, 0f, 0f, 1f))
//		property("fillColor", utils.color(0f, 0f, 0f, 1f))
//	}
	
	child(template:"towerofdefense.entities.button", id:"buttonResume")	{
		position=utils.vector(menuX, menuY + 0)
		rectangle=utils.rectangle(-160, -50, 320, 100)
		label={entity.parent.playing?"Resume":"Play"}
		lineColor=utils.color(0f, 0f, 1f, 0.5f)
		mouseNotOverFillColor=buttonMouseOverFillColor
		mouseOverFillColor=buttonMouseNotOverFillColor
		font=buttonFont
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("resume") {
		}
	}
	
	child(template:"towerofdefense.entities.button", id:"buttonExit")	{
		position=utils.vector(menuX, menuY + 120)
		rectangle=utils.rectangle(-160, -50, 320, 100)
		label="Exit"
		lineColor=utils.color(0f, 0f, 1f, 0.5f)
		mouseNotOverFillColor=buttonMouseOverFillColor
		mouseOverFillColor=buttonMouseNotOverFillColor
		font=buttonFont
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("exit") {
		}
	}
	
	genericComponent(id:"resumeHandler", messageId:"resume"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		stateBasedGame.enterState(1, new FadeOutTransition(), new FadeInTransition())
	}
	
	genericComponent(id:"exitHandler", messageId:"exit"){ message ->
		GameContainer gameContainer = utils.custom.gameContainer;
		gameContainer.exit();
	}
	
	genericComponent(id:"leaveStateHandler", messageId:"leaveState"){ message ->
		entity.playing=true
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