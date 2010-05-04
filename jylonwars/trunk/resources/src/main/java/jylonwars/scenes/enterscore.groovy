package jylonwars.scenes;

import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.games.jylonwars.TextField 
import com.gemserk.games.jylonwars.TextFieldComponent 
import com.gemserk.games.jylonwars.data.Data;

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
		
		property("message", {"Your killed $entity.parent.crittersdead critters in ${entity.parent.playtime}s, put your name:".toString()})
	})
	
	property("textField", new TextField("", 30))
	
	def textFieldRectangle = utils.rectangle(-220,-20,440,40)
	def textFieldPosition = utils.vector(400f, 340f)
	
	component(new RectangleRendererComponent("textFieldBackground")) {
		property("position", textFieldPosition)
		property("rectangle", textFieldRectangle)
		property("lineColor", utils.color(0.0f,0.0f,0.0f,1.0f))
		property("fillColor", utils.color(1.0f,1f,1f,1f))
	}
	
	child(entity("label2"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
		position:textFieldPosition,
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:textFieldRectangle,
		align:"center",
		valign:"center"
		])
		
		property("message", {entity.parent.textField.text + "|"})
	})
	
	component(textFieldComponent) {
		property("textField", {entity.textField})
	}
	
	component(utils.components.genericComponent(id:"enterNameGameStateEndHandler", messageId:"enterNameGameStateEnd"){ message ->
		def text = entity.textField.text.trim()
		
		if (text == "") 
			return

		def dataStore = utils.custom.gameStateManager.gameProperties.dataStore
		
		def playtime = entity.playtime
		def crittersDead = entity.crittersdead
		
		
		def dataId = dataStore.submit(new Data(tags:["score"], values:[name:text, playtime:playtime, crittersdead:crittersDead]))
		
		messageQueue.enqueue(utils.genericMessage("gameover") { newMessage ->
			newMessage.scoreId = dataId			
		})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"enterNameGameStateEnd")
		}
	}
}

