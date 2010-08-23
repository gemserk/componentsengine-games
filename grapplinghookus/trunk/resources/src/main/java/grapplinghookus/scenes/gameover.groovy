package grapplinghookus.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 

builder.entity("paused") {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:16])
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	def textColor = utils.color(1,1,1,1)
	
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
		message: "Game over, click to restart"
		])
	})
	
	component(utils.components.genericComponent(id:"grabMouse-enternodestate", messageId:"enterNodeState"){ message ->

	})
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		messageQueue.enqueue(utils.genericMessage("restartLevel"){})
	})
	
	input("inputmapping"){
		mouse {
			press(button:"left", eventId:"restart")
			press(button:"right", eventId:"restart")
		}
	}
	
}
