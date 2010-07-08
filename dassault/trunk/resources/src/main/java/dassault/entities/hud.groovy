package dassault.entities

import com.gemserk.componentsengine.commons.components.BarRendererComponent;


builder.entity {
	
	tags("hud")
	
	property("playerId", parameters.playerId)
	
	component(utils.components.genericComponent(id:"detectGameOver", messageId:"update"){ message ->
	
		def player = entity.root.getEntityById(entity.playerId)
		def controlledDroid = entity.root.getEntityById(player.controlledDroidId)
		
		if (!controlledDroid)
			return
		
		entity."healthRenderer.container" = controlledDroid.hitpoints
		
	})
	
	component(new BarRendererComponent("healthRenderer") ){
		property("position", utils.vector(20, 560))
		property("container", utils.container(100, 100))
		property("width", 200f)
		property("height", 20f)
		property("fullColor", utils.color(0.3f, 0.6f, 0.9f, 1.0f))
		property("emptyColor", utils.color(0.9f, 0.1f, 0.1f, 0.5f))
		property("layer", 1500)
	}
	
}
