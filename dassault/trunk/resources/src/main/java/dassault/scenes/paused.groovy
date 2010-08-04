package dassault.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 

builder.entity("paused") {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:16])
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.custom.gameStateManager.gameProperties.screenshot})
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(300,300))
		property("direction", utils.vector(1,0))
		property("layer", 900)
	}
	
	child(entity("pausedLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(300f, 480),
		fontColor:utils.color(1f,1f,1f,1f),
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
		position:utils.vector(300f, 520f),
		fontColor:utils.color(1f,1f,1f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010,
		message: "or press R restart"
		])
		
	})
	
	child(entity("hightsocres-table"){
		parent("dassault.hud.highscoretable", [layer:1010, 
		displayCount:10, 
		position:utils.vector(300f, 100f)] )
	})
	
	component(utils.components.genericComponent(id:"grabMouse-enternodestate", messageId:"enterNodeState"){ message ->
		utils.custom.messageQueue.enqueue(utils.genericMessage("updateScores"){ })
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
