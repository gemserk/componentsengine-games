package zombierockers.scenes
;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 


builder.entity {
	
	def font = utils.slick.resources.fonts.font([italic:false, bold:false, size:28])
	
	def labelRectangle = utils.slick.rectangle(-220,-50,440,100)
	
	
	//
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.slick.stateBasedGame.gameProperties.screenshot})
		property("color", utils.slick.color(1,1,1,1))
		property("position", utils.slick.vector(400,300))
		property("direction", utils.slick.vector(1,0))
		property("layer", 900)
	}
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.slick.vector(0,0))
		property("rectangle", utils.slick.rectangle(0,0, 800, 600))
		property("lineColor", utils.slick.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.slick.color(0.5f,0.5f,0.5f,0.5f))
		property("layer",1000)
	}
	
	child(entity("pausedLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.slick.vector(400f, 280),
		fontColor:utils.slick.color(0f,0f,0f,1f),
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
		position:utils.slick.vector(400f, 320f),
		fontColor:utils.slick.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010
		])
		
		property("message", "Press \"r\" to restart")
	})
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		utils.messageQueue.enqueue(utils.messages.genericMessage("resume"){})	
	})
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		utils.messageQueue.enqueue(utils.messages.genericMessage("restartLevel"){})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"resumeGame")
			press(button:"r",eventId:"restart")
			press(button:"space",eventId:"resumeGame")
			press(button:"p",eventId:"resumeGame")
			press(button:"escape",eventId:"resumeGame")
			press(button:"e",eventId:"goToEditor")
		}
		mouse {
			press(button:"left", eventId:"resumeGame")
			press(button:"right", eventId:"resumeGame")
		}
	}
}
