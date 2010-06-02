package zombierockers.scenes
;

import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 

builder.entity {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	property("labelText","")
	
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
	}
	
	child(entity("deadLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 280),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center"
		])
		
		property("message", {entity.parent.labelText })
	})
	
	component(utils.components.genericComponent(id:"enterNodeStateHandler", messageId:"enterNodeState"){ message ->
	
		def sourceMessage = message.message
		def win = sourceMessage.win
		
		if (win){
			entity.labelText = "You win"
		}else{
			entity.labelText = "You lose"
		}	
		
	})
	
	
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		utils.custom.game.loadScene("zombierockers.scenes.scene");
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
