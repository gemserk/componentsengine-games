package dassault.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 

builder.entity("helpscreen") {
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("helpscreen"))
		property("color", utils.color(1,1,1,1f))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", 0)
	}
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("resume"){})	
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"resumeGame")
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
