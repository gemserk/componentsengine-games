package zombierockers.scenes




builder.entity {
	
	component(utils.components.genericComponent(id:"acceleratorSystem2000", messageId:["update"]){ message ->
		if(entity.accelerating)
			message.delta = (int)message.delta * 10
	})
	component(utils.components.genericComponent(id:"acceleratorSystem2000-setter", messageId:["accelerateSystem2000-press","accelerateSystem2000-release"]){ message ->
		entity.accelerating = (message.id == "accelerateSystem2000-press")
	})
	
	child(entity("world"){ parent("zombierockers.scenes.world",parameters) })
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			press(button:"space",eventId:"pauseGame")
			press(button:"g",eventId:"dumpEditorPositions")
			press(button:"z",eventId:"accelerateSystem2000-press")
			release(button:"z",eventId:"accelerateSystem2000-release")
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
	
	property("shouldGrabMouse",true)
	
	component(utils.components.genericComponent(id:"grabMouse", messageId:"update"){ message ->
		if(entity.shouldGrabMouse && !utils.stateBasedGame.gameProperties.runningInDebug)
			utils.custom.gameContainer.setMouseGrabbed(true)
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
        graphics.copyArea(utils.stateBasedGame.gameProperties.screenshot, 0, 0); 
	})
	
	component(utils.components.genericComponent(id:"enterPauseWhenLostFocus", messageId:"update"){ message ->
		if(!utils.custom.gameContainer.hasFocus())
			messageQueue.enqueue(utils.messages.genericMessage("paused"){})
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		messageQueue.enqueue(utils.messages.genericMessage("paused"){})
	})
	
	child(entity("fpsLabel"){
		
		parent("gemserk.gui.label", [
		//font:utils.slick.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.slick.vector(60f, 30f),
		fontColor:utils.slick.color(0f,0f,0f,1f),
		bounds:utils.slick.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top",
		layer: 100000
		])
		
		property("message", {"FPS: ${utils.custom.gameContainer.getFPS()}".toString() })
	})
}

