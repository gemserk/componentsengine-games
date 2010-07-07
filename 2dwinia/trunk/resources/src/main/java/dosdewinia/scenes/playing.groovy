package dosdewinia.scenes

builder.entity {
	
	child(entity("world"){ parent("dosdewinia.scenes.world",parameters) })
//	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			press(button:"space",eventId:"unselect")
			press(button:"s",eventId:"spawnDarwinians"){ message ->
				message.quantity = 50				
			}
		}
		mouse {
			press(button:"left", eventId:"mouse.left.press")
			release(button:"left", eventId:"mouse.left.release")
			press(button:"right", eventId:"mouse.right.press")
			release(button:"right", eventId:"mouse.right.press")
			
			move(eventId:"mouse.move") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
//	
//	property("shouldGrabMouse",true)
//	
//	component(utils.components.genericComponent(id:"grabMouse", messageId:"update"){ message ->
//		if(entity.shouldGrabMouse && !utils.custom.gameStateManager.gameProperties.runningInDebug)
//			utils.custom.gameContainer.setMouseGrabbed(true)
//	})
//	
//	component(utils.components.genericComponent(id:"grabMouse-enternodestate", messageId:"enterNodeState"){ message ->
//		entity.shouldGrabMouse = true
//		log.info("Entering playing state")
//	})
//	
//	component(utils.components.genericComponent(id:"grabMouse-leavenodestate", messageId:"leaveNodeState"){ message ->
//		utils.custom.gameContainer.setMouseGrabbed(false)
//		entity.shouldGrabMouse = false
//		log.info("Leaving playing state")
//	})
//	
//	
	component(utils.components.genericComponent(id:"grabscreenshot-leavenodestate", messageId:"leaveNodeState"){ message ->
		def graphics = utils.custom.gameContainer.graphics
        graphics.copyArea(utils.custom.gameStateManager.gameProperties.screenshot, 0, 0); 
	})
	
	component(utils.components.genericComponent(id:"enterPauseWhenLostFocus", messageId:"update"){ message ->
		if(!utils.custom.gameStateManager.gameProperties.runningInDebug && !utils.custom.gameContainer.hasFocus())
			messageQueue.enqueue(utils.genericMessage("paused"){})
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("paused"){})
	})
	
}

