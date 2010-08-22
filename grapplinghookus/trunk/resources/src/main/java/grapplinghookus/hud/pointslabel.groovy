package grapplinghookus.hud

builder.entity {
	
	def pointsLabelsFont = utils.resources.fonts.font([italic:false, bold:false, size:16])
	
	property("player", parameters.player)
	
	parameters.bounds = utils.rectangle(-50, -10, 100, 20)
	parameters.fontColor = {entity.player.color}
	parameters.value = {entity.player.points}
	parameters.font = pointsLabelsFont
	
	parent("gemserk.gui.label", parameters)
	
}
