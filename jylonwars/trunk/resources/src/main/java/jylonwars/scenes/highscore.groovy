package jylonwars.scenes;


import java.text.DateFormat;
import java.util.concurrent.Callable;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;

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
		def submitted = sourceMessage.submitted
		
		if (scoreId)
			entity.scoreId = scoreId
		
		def childPanel = entity("childPanel") { }
		entity.childPanel = childPanel
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(childPanel, entity))
		
		messageQueue.enqueue(utils.genericMessage("refreshScores"){})
	})
	
	property("refreshScoresTimer", new CountDownTimer(5000))
	
	component(new ComponentFromListOfClosures("checkScoresAreRefreshed", [{UpdateMessage message ->
		def future = entity.future
		def timer = entity.refreshScoresTimer
		
		def messageQueue = utils.custom.messageQueue
		
		if (future == null)
			return
		
		def triggered = timer.update(message.delta)
		
		if (future.done) {
			
			try {
				def scoreList = future.get()
				
				messageQueue.enqueue(utils.genericMessage("scoresRefreshed") { newMessage ->
					newMessage.scoreList = scoreList
				})
				
			} catch (exception) {
				println exception
				messageQueue.enqueue(utils.genericMessage("scoresFailedToRefresh") { newMessage ->
					newMessage.reason = "Failed to load highscores from server"
				})
			}
			
		} else {
			
			if (!triggered)
				return
			
			println "timer triggered"
			messageQueue.enqueue(utils.genericMessage("scoresFailedToRefresh") { newMessage ->
				newMessage.reason = "Failed to load highscores from server"
			})
			
		}
		
		entity.future = null
		
	}]))
	
	component(utils.components.genericComponent(id:"startScoresRefresh", messageId:"refreshScores"){ message ->
		def scores = utils.custom.gameStateManager.gameProperties.scores
		
		def ascending = false
		def tags = [] as Set
		def quantity = 10
		
		def executor = utils.custom.gameStateManager.gameProperties.executor
		
		def future = executor.submit({
			return scores.getOrderedByPoints(tags, quantity, ascending)
		} as Callable )
		
		entity.future = future
		entity.refreshScoresTimer.reset()
		
		entity.refreshingScoresLabel = entity("refreshingScoresLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(400f, 300f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:labelRectangle,
			align:"center",
			valign:"center"
			])
			
			property("message", "Updating scores, please wait...")
		}
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(entity.refreshingScoresLabel, entity.childPanel))
	})
	
	component(utils.components.genericComponent(id:"scoresFailedToRefreshHandler", messageId:"scoresFailedToRefresh"){ message ->
		
		def newLabel = entity("scoresFailedLabel".toString()){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(400f, 300f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:labelRectangle,
			align:"center",
			valign:"center"
			])
			
			property("message", message.reason)
		}
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newLabel, entity.childPanel))
		
	})
	
	component(utils.components.genericComponent(id:"scoresRefreshedHandler", messageId:"scoresRefreshed"){ message ->
		def newEntities = []
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity.refreshingScoresLabel))		                   
		
		def scoreList = message.scoreList
		
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
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newEntity, entity.childPanel))
		}
	})
}

