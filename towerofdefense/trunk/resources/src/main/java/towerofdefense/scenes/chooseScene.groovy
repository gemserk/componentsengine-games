package towerofdefense.scenes;

import com.gemserk.games.towerofdefense.GemserkGameState;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import org.newdawn.slick.GameContainer 
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import towerofdefense.GroovyBootstrapper 

builder.entity("sceneselection") {
	
	new GroovyBootstrapper();
	
	def scenes = [[desc:"First scene",script:"towerofdefense.scenes.scene1"],
			[desc:"Second scene",script:"towerofdefense.scenes.scene2"],
			[desc:"Third scene",script:"towerofdefense.scenes.scene3"]
			]
	
	
	
	property("playing", false)
	
	def buttonMouseOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.2f)
	def buttonMouseNotOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.8f)
	
	def buttonFont = utils.resources.fonts.font([italic:false, bold:false, size:48])
	
	def menuX = 400
	def menuY = 150
	def buttonHeight = 100
	def buttonSeparation = 20
	

	scenes.eachWithIndex { scene, i ->
		child(template:"towerofdefense.entities.button", id:"buttonScene-$i".toString())	{
			position=utils.vector(menuX, (Float)(menuY + i*(buttonHeight+buttonSeparation)))
			rectangle=utils.rectangle(-160, -50, 320, buttonHeight)
			label=scene.desc
			lineColor=utils.color(0f, 0f, 1f, 0.5f)
			mouseNotOverFillColor=buttonMouseOverFillColor
			mouseOverFillColor=buttonMouseNotOverFillColor
			font=buttonFont
			messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("loadScene") {
				message.scene = scene.script
			}
		}
	}
	
	genericComponent(id:"loadSceneHandler", messageId:"loadScene"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		GemserkGameState inGameState = stateBasedGame.getState(1)
		inGameState.loadScene(message.scene)
		stateBasedGame.gameProperties.inGame=true
		stateBasedGame.enterState(inGameState.id, new FadeOutTransition(), new FadeInTransition())
	}
	
	genericComponent(id:"resumeHandler", messageId:"resume"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		stateBasedGame.enterState(0, new FadeOutTransition(), new FadeInTransition())
	}
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"resume")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}
	
	
}