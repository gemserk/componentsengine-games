package zombierockers.scenes
;

import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 

builder.entity {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	property("labelText","")
	property("win", false)
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
		property("layer",1000)
	}
	
	child(entity("deadLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 280),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010
		])
		
		property("message", {entity.parent.labelText })
	})
	
	component(utils.components.genericComponent(id:"enterNodeStateHandler", messageId:"enterNodeState"){ message ->
		
		def sourceMessage = message.message
		entity.win = sourceMessage.win
		
		if (entity.win){
			entity.labelText = "You win"
		}else{
			entity.labelText = "You lose"
		}	
		
	})
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		if (entity.win) {
			messageQueue.enqueue(utils.genericMessage("nextLevel"){ })
		} else {
			messageQueue.enqueue(utils.genericMessage("restartLevel"){})
		}
	})
	
	input("inputmapping"){
		keyboard {
			release(button:"escape",eventId:"restart")
			release(button:"space",eventId:"restart")
		}
		mouse {
			press(button:"left", eventId:"restart")
			press(button:"right", eventId:"restart")
		}
	}
	
}
