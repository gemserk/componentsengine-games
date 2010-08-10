package jylonwars.scenes;


import java.text.DateFormat;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;

builder.entity {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:18])
	
	def labelRectangle = utils.rectangle(-260,-50,520,100)
	
	child(entity("scoresLabel-name".toString()){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, (float)(130)),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"left",
		valign:"center"
		])
		
		property("message", "NAME")
	})
	
	child(entity("scoresLabel-date".toString()){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, (float)(130)),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center"
		])
		
		property("message", "DATE")
	})
	
	child(entity("scoresLabel-playtime".toString()){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, (float)(130)),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"right",
		valign:"center"
		])
		
		property("message", "POINTS")
	})
	
	property("scoreId", null)
	
	component(utils.components.genericComponent(id:"enterNodeStateHandler", messageId:"enterNodeState"){ message ->
		
		def sourceMessage = message.message
		def scoreId = sourceMessage.scoreId
		
		if (scoreId)
			entity.scoreId = scoreId
		
		messageQueue.enqueue(utils.genericMessage("refreshScores"){})
	})
	
	component(utils.components.genericComponent(id:"refreshScoresHandler", messageId:"refreshScores"){ message ->
		def newEntities = []
		
		// def dataStore = utils.custom.gameStateManager.gameProperties.dataStore
		
		def scores = utils.custom.gameStateManager.gameProperties.scores
		
		def ascending = false
		def tags = [] as Set
		def quantity = 10
		
		def scoreList = scores.getOrderedByPoints(tags, quantity, ascending)
		
		//		scores = dataStore.get(["score"] as Set).sort { it.values.playtime}.reverse()
		
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
		
		def createLabel = { score, scoreIndex ->
			
			//			calendar.set
			def name = score.name
			def points = score.points
			
			def date = new Date(score.timestamp)
			def dateString = dateFormat.format(date)
			
			//			def playtime = score.values.playtime
			//			def crittersDead = score.values.crittersDead
			
			def fontColor = score.id != entity.scoreId ? utils.color(0f,0f,0f,1f) : utils.color(1f,0f,0f,1f)  
			
			newEntities << entity("scoresLabel-name-${scoreIndex}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(400f, (float)(170 + 30 * scoreIndex)),
				fontColor:fontColor,
				bounds:labelRectangle,
				align:"left",
				valign:"center"
				])
				
				property("message", "$name".toString())
			}
			
			newEntities << entity("scoresLabel-date-${scoreIndex}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(400f, (float)(170 + 30 * scoreIndex)),
				fontColor:fontColor,
				bounds:labelRectangle,
				align:"center",
				valign:"center"
				])
				
				property("message", "$dateString".toString())
			}
			
			newEntities << entity("scoresLabel-points-${scoreIndex}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(400f, (float)(170 + 30 * scoreIndex)),
				fontColor:fontColor,
				bounds:labelRectangle,
				align:"right",
				valign:"center"
				])
				
				property("message", "$points".toString())
			}
		}
		
		createLabel.setResolveStrategy Closure.DELEGATE_FIRST
		
		scoreList.eachWithIndex(createLabel)
		
		newEntities.each { newEntity -> 
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newEntity, entity))
		}
	})
}

