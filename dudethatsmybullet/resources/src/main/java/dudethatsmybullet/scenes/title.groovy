package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;


builder.entity("title") {
	
	
	property("screen", utils.rectangle(0,0, 800, 600))
	property("center", utils.vector(400, 300))
	
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("helpscreen"){})	
	})
	
	component(new RectangleRendererComponent("backback")) {
		property("position", utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("fillColor", utils.color(0,0,0,1))
		property("layer", 1)
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("title"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", 2)
	}
	
	
	
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
