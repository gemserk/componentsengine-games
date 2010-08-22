package grapplinghookus.scenes;


import org.lwjgl.input.Mouse;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import gemserk.utils.GroovyBootstrapper 

builder.entity("playing") {
	
	new GroovyBootstrapper();
	
	def enemyFactory = utils.custom.enemyFactory
	
	child(entity("gameLogic") {
		
	})
	
	child(enemyFactory.enemy("enemy1", [
			position:utils.vector(100, 100),
			moveDirection:utils.vector(0,1), 
			speed:0.03f]))
			
			/*
			 * 		imageProperty = Properties.property(id, "image");
		positionProperty = Properties.property(id, "position");
		directionProperty = Properties.property(id, "direction");
		renderColorProperty = Properties.property(id, "color");
		sizeProperty = Properties.property(id, "size");
		rendererProperty = Properties.property("renderer");
		layerProperty = Properties.property(id, "layer");
			 * */
			
	component(new ImageRenderableComponent("background")) {
		property("position", utils.vector(320,240))
		property("image", utils.resources.image("background"))
		property("direction", utils.vector(1,0))
		property("layer", 0)
	}
	
	component(new ExplosionComponent("explosions")) { }
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			press(button:"space",eventId:"pauseGame")
			press(button:"h",eventId:"helpscreen")
			press(button:"g",eventId:"toggleGrabMouse")
		}
		mouse {
			press(button:"left", eventId:"leftmouse")
			press(button:"right", eventId:"rightmouse")
			move(eventId:"movemouse") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
	component(utils.components.genericComponent(id:"toggleGrabMouseHandler", messageId:"toggleGrabMouse"){ message ->
		entity.grabEnabled = !entity.grabEnabled
	})
	
	property("shouldGrabMouse",true)
	property("grabEnabled", !utils.custom.gameStateManager.gameProperties.runningInDebug)
	
	component(utils.components.genericComponent(id:"grabMouse", messageId:"update"){ message ->
		if(entity.shouldGrabMouse && entity.grabEnabled) {
			if (!Mouse.isGrabbed())
				utils.custom.gameContainer.setMouseGrabbed(true)
		}
		else { 
			if (Mouse.isGrabbed())
				utils.custom.gameContainer.setMouseGrabbed(false)
		}
	})
	
	component(utils.components.genericComponent(id:"grabMouse-enternodestate", messageId:"enterNodeState"){ message ->
		entity.shouldGrabMouse = true
		log.info("Entering playing state")
	})
	
	component(utils.components.genericComponent(id:"grabMouse-leavenodestate", messageId:"leaveNodeState"){ message ->
		utils.custom.gameContainer.setMouseGrabbed(false)
		entity.shouldGrabMouse = false
		log.info("Leaving playing state")
	})
	
	component(utils.components.genericComponent(id:"grabscreenshot-leavenodestate", messageId:"leaveNodeState"){ message ->
		def graphics = utils.custom.gameContainer.graphics
		graphics.copyArea(utils.custom.gameStateManager.gameProperties.screenshot, 0, 0); 
	})
	
	component(utils.components.genericComponent(id:"enterPauseWhenLostFocus", messageId:"update"){ message ->
		if(!utils.custom.gameContainer.hasFocus())
			messageQueue.enqueue(utils.genericMessage("paused"){})
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		if(!entity.gameOver)
			messageQueue.enqueue(utils.genericMessage("paused"){})
		else
			messageQueue.enqueue(utils.genericMessage("restartLevel"){})
	})
	
}

