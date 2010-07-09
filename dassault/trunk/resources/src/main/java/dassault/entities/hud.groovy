package dassault.entities

import com.gemserk.componentsengine.commons.components.BarRendererComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 


builder.entity {
	
	tags("hud")
	
	property("playerId", parameters.playerId)
	property("player", {entity.root.getEntityById(entity.playerId)})
	
	component(utils.components.genericComponent(id:"updateHudValues", messageId:"update"){ message ->
		
		def player = entity.player
		def controlledDroid = entity.root.getEntityById(player.controlledDroidId)
		
		if (!controlledDroid)
			return
		
		entity."healthRenderer.container" = controlledDroid.hitpoints
	})
	
	component(new ImageRenderableComponent("hudBackground")) {
		property("position", utils.vector(300, 300))
		property("image", utils.resources.image("background"))
		property("direction", utils.vector(1,0))
		property("layer", 1400)
		property("color", utils.color(1f,1,1,1f))
	}
	
	child(id:"playerPointsLabel", template:"gemserk.gui.label") { 
		position = utils.vector(520,20)
		message = "POINTS: {0}"
		bounds = utils.rectangle(-50, -10, 100, 20)
		value = {entity.parent.player.points}
		layer = 1500 
	}
	
	component(new BarRendererComponent("healthRenderer") ){
		property("position", utils.vector(20, 560))
		property("container", utils.container(100, 100))
		property("width", 200f)
		property("height", 15f)
		property("fullColor", utils.color(0.3f, 0.6f, 0.9f, 1.0f))
		property("emptyColor", utils.color(0.9f, 0.1f, 0.1f, 0.5f))
		property("layer", 1500)
	}
	
	child(id:"healthLabel", template:"gemserk.gui.label") { 
		position = utils.vector(70,585)
		message = "HEALTH"
		bounds = utils.rectangle(-50, -10, 100, 20)
		layer = 1500 
		align = "left"
	}
	
}