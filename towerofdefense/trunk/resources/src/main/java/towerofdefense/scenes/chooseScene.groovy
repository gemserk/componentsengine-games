package towerofdefense.scenes;


import com.gemserk.componentsengine.commons.components.ChildsDisablerComponent 
import com.gemserk.componentsengine.gamestates.GemserkGameState 
import com.gemserk.componentsengine.utils.Paging 

import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import towerofdefense.GroovyBootstrapper 


builder.entity("sceneselection") {
	
	new GroovyBootstrapper();
	
	def scenes = [[desc:"First scene",script:"towerofdefense.scenes.scene1"],
			[desc:"Second scene",script:"towerofdefense.scenes.scene2"],
			[desc:"Third scene",script:"towerofdefense.scenes.scene3"],
			[desc:"Fourth scene",script:"towerofdefense.scenes.scene4"],
			[desc:"Fifth scene",script:"towerofdefense.scenes.scene5"],
			[desc:"Sixth scene",script:"towerofdefense.scenes.scene6"],
			[desc:"Seventh scene",script:"towerofdefense.scenes.scene7"],
			[desc:"Eighth scene",script:"towerofdefense.scenes.scene8"],
			[desc:"Ninth scene",script:"towerofdefense.scenes.scene9"],
			]
	
	if(utils.custom.gameStateManager.gameProperties.runningFromMain)
		scenes << [desc:"Test scene",script:"towerofdefense.scenes.testScene"]
	
	property("playing", false)
	
	def buttonMouseOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.2f)
	def buttonMouseNotOverFillColor = utils.color(0.0f, 0.0f, 1.0f, 0.8f)
	
	def buttonFont = utils.resources.fonts.font([italic:false, bold:false, size:32])
	
	def menuX = 400
	def menuY = 50
	def buttonHeight = 60
	def buttonSeparation = 15
	
	def itemsPerPage = 6
	
	def paging = new Paging(scenes, itemsPerPage)
	
	property("paging", paging)
	
	itemsPerPage.times { i -> 
		
		child(entity("button-$i".toString()) { 
			
			component(new ChildsDisablerComponent("enabler")) {
				property("enabled", {paging.onPageHasItem(i)})
			}
			
			child(entity("buttonScene-$i".toString()) {
				
				property("scene", {paging.getItem(i)})
				
				parameters = [
						position:utils.vector(menuX, (Float)(menuY + i*(buttonHeight+buttonSeparation))),
						rectangle:utils.rectangle(-160, (float)(-buttonHeight/2f), 320, buttonHeight),
						label:{entity.scene.desc},
						lineColor:utils.color(0f, 0f, 1f, 0.5f),
						mouseNotOverFillColor:buttonMouseOverFillColor,
						mouseOverFillColor:buttonMouseNotOverFillColor,
						font:buttonFont,
						trigger:utils.custom.triggers.genericMessage("loadScene") {
							message.scene = entity.scene.script
						}
						]
				
				parent("towerofdefense.entities.button", parameters)
				
			})
			
		})
		
	}
	
	child(entity("nextPageButton") {
		
		property("paging", {entity.parent.paging})
		
		parameters = [
				position:utils.vector(680, 550),
				rectangle:utils.rectangle(-100, (float)(-buttonHeight/2f), 200, buttonHeight),
				label:"next",
				lineColor:utils.color(0f, 0f, 1f, 0.5f),
				mouseNotOverFillColor:buttonMouseOverFillColor,
				mouseOverFillColor:buttonMouseNotOverFillColor,
				font:buttonFont,
				trigger:utils.custom.triggers.genericMessage("nextPage") {
					entity.paging.nextPage()
				},
				enabled:{!entity.paging.isLastPage()}
				]
		
		parent("towerofdefense.entities.button", parameters)
		
	})
	
	child(entity("previousPageButton") {
		
		property("paging", {entity.parent.paging})
		
		parameters = [
				position:utils.vector(120, 550),
				rectangle:utils.rectangle(-100, (float)(-buttonHeight/2f), 200, buttonHeight),
				label:"previous",
				lineColor:utils.color(0f, 0f, 1f, 0.5f),
				mouseNotOverFillColor:buttonMouseOverFillColor,
				mouseOverFillColor:buttonMouseNotOverFillColor,
				font:buttonFont,
				trigger:utils.custom.triggers.genericMessage("previousPage") {
					entity.paging.previousPage()
				},
				enabled:{!entity.paging.isFirstPage()}
				]
		
		parent("towerofdefense.entities.button", parameters)
		
	})
	
	component(utils.components.genericComponent(id:"loadSceneHandler", messageId:"loadScene"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		GemserkGameState inGameState = stateBasedGame.getState(1)
		inGameState.loadScene(message.scene)
		stateBasedGame.gameProperties.inGame=true
		stateBasedGame.enterState(inGameState.getID(), new FadeOutTransition(), new FadeInTransition())
	})
	
	component(utils.components.genericComponent(id:"resumeHandler", messageId:"resume"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager
		stateBasedGame.enterState(0, new FadeOutTransition(), new FadeInTransition())
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"resume")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			
			press(button:"left", eventId:"mouse.leftpressed")
			release(button:"left", eventId:"mouse.leftreleased")
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}
	
	
}