package dassault.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 

builder.entity("paused") {
	
def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.custom.gameStateManager.gameProperties.screenshot})
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", 900)
	}
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
		property("layer",1000)
	}
	
	child(entity("pausedLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 280),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010
		])
		
		property("message", "Paused, press click to continue...")
	})
	
	child(entity("restartLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 320f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010
		])
		
		property("message", "Press \"r\" to restart")
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
