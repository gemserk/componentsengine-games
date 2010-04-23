package jylonwars.scenes;

import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 


builder.entity("gameover") {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	property("enabled", {entity.parent.gameState == "gameover" })
	property("playtime", {
		(float)(entity.parent.playtime/1000f)
	})
	
	component(new ProcessingDisablerComponent("disableStateComponent")){  propertyRef("enabled", "enabled") }
	
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
		position:utils.vector(400f, 70),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center"
		])
		
		property("playtime", {entity.parent.playtime })
		property("message", {"Your time was: ${entity.playtime} seconds".toString() })
	})
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		//			backgroundMusic.stop();
		utils.custom.game.loadScene("jylonwars.scenes.scene");
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
