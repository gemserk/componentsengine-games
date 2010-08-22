package grapplinghookus.hud


import com.gemserk.componentsengine.commons.components.BarRendererComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 


builder.entity {
	
	def pointsLabelsFont = utils.resources.fonts.font([italic:false, bold:false, size:16])
	
	tags("hud")
	
	property("playerId", parameters.playerId)
	property("player", {entity.root.getEntityById(entity.playerId)})
	
	property("players", parameters.players)
	
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
	
	def players = parameters.players
	def y = 20f
	
	players.each { player -> 
		
		child(entity("$player.id-pointsLabel".toString()){
			parent("dassault.hud.pointslabel", [
			position:utils.vector(530f,(float)y), 
			message: "Points: {0}", 
			player: player, 
			layer: 1500])
		})
		
		y+=15f
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
		font = utils.resources.fonts.font([italic:false, bold:false, size:14])
		position = utils.vector(20,560)
		message = "Health"
		bounds = utils.rectangle(0, 0, 100, 20)
		layer = 1501 
		align = "left"
		valign = "top"
	}
	
}
