package dassault.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 


builder.entity("helpscreen") {
	
	// TODO: parameter for screen size
	
	property("screen", utils.rectangle(0,0, 600, 600))
	property("center", utils.vector(300, 300))
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("resume"){})	
	})
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.custom.gameStateManager.gameProperties.screenshot})
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(300,300))
		property("direction", utils.vector(1,0))
		property("layer", 0)
	}
	
	def texts = ["Dassault - Droid Assault Demake",
			"",
			"Instructions", 
			"",
			"Move: WASD or CURSOR KEYS", 
			"Aim: MOUSE", 
			"Shoot: LEFT MOUSE", 
			"Transfer: HOLD RIGHT MOUSE", 
			"Zoom: MOUSE WHEEL", 
			"Switch camera type: E KEY",
			"",
			"",
			"Press any key to continue"]
	
	float y = 60f
	
	texts.each { text ->
		child(id:"moveHelpText-$y".toString(), template:"gemserk.gui.label") { 
			font = utils.resources.fonts.font([italic:false, bold:false, size:20])
			position = utils.vector(350,(float)y)
			message =  text
			bounds = utils.rectangle(-200, 0, 400, 20)
			layer = 1
			align = "left"
		}
		
		y += 30f
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
