package dassault.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 

builder.entity("helpscreen") {
	
	// TODO: parameter for screen size
	
	property("screen", utils.rectangle(0,0, 600, 600))
	property("center", utils.vector(300, 300))
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("helpscreen"))
		property("color", utils.color(1,1,1,1f))
		propertyRef("position", "center")
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
