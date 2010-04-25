package game.scenes
;

import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 


builder.entity("gameover") {
	
	
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	property("enabled", {entity.parent.gameState == "gameover" })
	property("result",{entity.parent.result})
	
	component(new ProcessingDisablerComponent("disableStateComponent")){  propertyRef("enabled", "enabled") }
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
	}
	
	child(entity("resultLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 270),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center"
		])
		
		property("message", {entity.parent.result})
	})
	
	
	child(entity("resumeLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 310),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center"
		])
		
		property("message", "Press click to continue...")
	})
	
	
	
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		//			backgroundMusic.stop();
		utils.custom.game.loadScene("game.scenes.scene");
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"restart")
			press(button:"space",eventId:"restart")
		}
		mouse {
			press(button:"left", eventId:"restart")
			press(button:"right", eventId:"restart")
		}
	}
}
