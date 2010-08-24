package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;


builder.entity("helpscreen") {
	
	// TODO: parameter for screen size
	def textFont = utils.resources.fonts.font([italic:false, bold:false, size:16])
	
	property("screen", utils.rectangle(0,0, 800, 600))
	property("center", utils.vector(400, 300))
	
	def textColor = utils.color(1,1,1,1)
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("resume"){})	
	})
	
	component(new RectangleRendererComponent("backback")) {
		property("position", utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("fillColor", utils.color(0,0,0,0.5))
		property("layer", 1)
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.custom.gameStateManager.gameProperties.screenshot})
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", 0)
	}
	
	def texts = ["Dude that's my bullet - Ludum Dare 18",
			"",
			"Instructions", 
			"",
			"Move: MOUSE", 
			"Shield: LEFT MOUSE BUTTON", 
			"",
			"",
			"Your mission:",
			"Kill the turrets!!",
			"Once there is only one left you will unleash the megabomb!!",
			"You can't shoot, use their own bullets against them",
			"",
			"Press any key to continue"]
	
	float y = 60f
	
	texts.each { text ->
		child(id:"moveHelpText-$y".toString(), template:"gemserk.gui.label") { 
			font = textFont
			position = utils.vector(350,(float)y)
			message =  text
			bounds = utils.rectangle(-200, 0, 400, 20)
			layer = 2
			align = "left"
			fontColor = textColor
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
