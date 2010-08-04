package dassault.hud

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 

builder.entity {
	
	property("dataStore", utils.custom.gameStateManager.gameProperties.dataStore)
	property("layer", parameters.layer)
	property("displayCount", parameters.displayCount ?: 10)
	property("position", parameters.position)
	
	def normalScoreFont = utils.resources.fonts.font([italic:false, bold:false, size:16])
	def selectedScoreFont = utils.resources.fonts.font([italic:false, bold:true, size:16])
	
	component(utils.components.genericComponent(id:"updateScoresHandler", messageId:"updateScores"){ message ->
		
		def newEntities = []
		
		def dataId = message.dataId
		def labelRectangle = utils.rectangle(-250,-50, 500,100)
		
		def dataStore = entity.dataStore
		def layer = entity.layer
		def displayCount = entity.displayCount
		def position = entity.position
		
		scores = dataStore.get(["score"] as Set).sort { it.values.points }.reverse()
		
		def createLabel = { data, scoreIndex ->
			
			if (scoreIndex > displayCount)
				return
			
			def fontColor = data.id != dataId ? utils.color(0.9f,0.9f,0.9f,0.9f) : utils.color(1f,1f,1f,1f)  
			def font = data.id != dataId ? normalScoreFont : selectedScoreFont
			
			newEntities << entity("scoresLabel-name-${scoreIndex}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(position.x, (float)(position.y + 30 * scoreIndex)),
				fontColor:fontColor,
				bounds:labelRectangle,
				layer:layer,
				align:"left",
				valign:"center"
				])
				
				property("message", "$data.values.name".toString())
			}
			
			newEntities << entity("scoresLabel-points-${scoreIndex}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(position.x, (float)(position.y + 30 * scoreIndex)),
				fontColor:fontColor,
				bounds:labelRectangle,
				layer:layer,
				align:"right",
				valign:"center"
				])
				
				property("message", "$data.values.points".toString())
			}
		}
		
		createLabel.setResolveStrategy Closure.DELEGATE_FIRST
		
		scores.eachWithIndex(createLabel)
		
		if (newEntities.isEmpty() ) {
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(entity("noScoresLabel") {
				parent("gemserk.gui.label", [
				font:normalScoreFont,
				position:utils.vector(position.x, (float)(position.y + 100f)),
				fontColor:utils.color(1f,1f,1f,1f),
				bounds:labelRectangle,
				layer:layer,
				message:"There are no scores yet",
				align:"center",
				valign:"center"
				])
			}, entity))
		}
		
		newEntities.each { newEntity -> 
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newEntity, entity.parent))
		}
		
	})
	
	
}
