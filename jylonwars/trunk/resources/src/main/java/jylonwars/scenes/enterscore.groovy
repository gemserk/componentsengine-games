package jylonwars.scenes;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.timers.CountDownTimer 
import com.gemserk.games.jylonwars.TextField 
import com.gemserk.games.jylonwars.TextFieldComponent 
import com.gemserk.scores.Score;
import java.util.concurrent.Callable 

builder.entity {
	
	def textFieldFont = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	property("playtime", 0f)
	property("crittersdead", 0f)
	
	def textFieldComponent = new TextFieldComponent("textFieldTest")
	
	component(utils.components.genericComponent(id:"enterNodeStateHandler", messageId:"enterNodeState"){ message ->
		
		def sourceMessage = message.message
		
		entity.playtime = sourceMessage.playtime
		entity.crittersdead= sourceMessage.crittersdead
		
		def input = utils.custom.gameContainer.input
		input.addKeyListener(textFieldComponent);
	})
	
	component(utils.components.genericComponent(id:"leaveNodeStateHandler", messageId:"leaveNodeState"){ message ->
		def input = utils.custom.gameContainer.input
		input.removeKeyListener(textFieldComponent);
	})
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
	}
	
	child(entity("label1"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:24]),
		position:utils.vector(400f, 300f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-220,-50,440,100),
		align:"center",
		valign:"center"
		])
		
		property("message", {"Your killed $entity.parent.crittersdead critters in ${entity.parent.playtime}s, put your name:".toString() })
	})
	
	child(entity("textField1") {
		
		property("textField", new TextField("", 20))
		property("text", {entity.textField.text })
		
		def textFieldRectangle = utils.rectangle(-220,-20,440,40)
		def textFieldPosition = utils.vector(400f, 340f)
		
		component(new RectangleRendererComponent("textFieldBackground")) {
			property("position", textFieldPosition)
			property("rectangle", textFieldRectangle)
			property("lineColor", utils.color(0.0f,0.0f,0.0f,1.0f))
			property("fillColor", utils.color(1.0f,1f,1f,1f))
		}
		
		child(entity("textFieldLabel"){
			
			parent("gemserk.gui.label", [
			font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
			position:textFieldPosition,
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:textFieldRectangle,
			align:"center",
			valign:"center"
			])
			
			property("message", {entity.parent.text + "|" })
		})
		
		component(textFieldComponent) {
			property("textField", {entity.textField })
		}
		
	})
	
	property("nameTextField", {entity.children["textField1"]})
	
	property("failedToSubmitTimer", new CountDownTimer(5000))
	
	component(new ComponentFromListOfClosures("checkScoresAreRefreshed", [{UpdateMessage message ->
		def future = entity.future
		def timer = entity.failedToSubmitTimer
		
		def messageQueue = utils.custom.messageQueue
		
		if (future == null)
			return
		
		def triggered = timer.update(message.delta)
		
		if (future.done) {
			
			try {
				def scoreId = future.get()
				
				messageQueue.enqueue(utils.genericMessage("gameover") { newMessage ->
					newMessage.scoreId = scoreId		
					newMessage.submitted = true
				})
				
			} catch (exception) {
				println exception
				messageQueue.enqueue(utils.genericMessage("gameover") { newMessage ->
					newMessage.submitted = false
				})
			}
			
		} else {
			
			if (!triggered)
				return
			
			println "timer triggered"
			messageQueue.enqueue(utils.genericMessage("gameover") { newMessage ->
				newMessage.submitted = false
			})
			
		}
		
		entity.future = null
		
	}]))
	
	component(utils.components.genericComponent(id:"enterNameGameStateEndHandler", messageId:"submitScore"){ message ->
		
		def textField = entity.nameTextField
		def name = textField.text.trim()
		
		if (name == "") 
			return
			
		def scores = utils.custom.gameStateManager.gameProperties.scores
		def executor = utils.custom.gameStateManager.gameProperties.executor
		
		def playtime = entity.playtime
		def crittersDead = entity.crittersdead
		
		long points = (long) playtime * 1000
		
		def tags = ["easy"] as Set
		def data = [playtime:playtime, crittersdead:crittersDead]
		
		def future = executor.submit({
			return scores.submit( new Score(name, points, tags, data))
		} as Callable )
		
		entity.future = future
		entity.failedToSubmitTimer.reset()
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"submitScore")
		}
	}
}

