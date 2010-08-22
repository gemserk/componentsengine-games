package grapplinghookus.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 

builder.entity("paused") {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:16])
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	def textColor = utils.color(0,0,0,1)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.custom.gameStateManager.gameProperties.screenshot})
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(320f,240f))
		property("direction", utils.vector(1,0))
		property("layer", 900)
	}
	
	child(entity("pausedLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(320, 280),
		fontColor:textColor,
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010,
		message: "Game paused, click mouse to continue"
		])
	})
	
	child(entity("restartLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(320, 320f),
		fontColor:textColor,
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010,
		message: "or press R restart"
		])
		
	})
	
	component(utils.components.genericComponent(id:"grabMouse-enternodestate", messageId:"enterNodeState"){ message ->

	})
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("resume"){})	
	})
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		messageQueue.enqueue(utils.genericMessage("restartLevel"){})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"resumeGame")
			press(button:"r",eventId:"restart")
			press(button:"space",eventId:"resumeGame")
			press(button:"p",eventId:"resumeGame")
			press(button:"escape",eventId:"resumeGame")
		}
		mouse {
			press(button:"left", eventId:"resumeGame")
			press(button:"right", eventId:"resumeGame")
		}
	}
	
}
