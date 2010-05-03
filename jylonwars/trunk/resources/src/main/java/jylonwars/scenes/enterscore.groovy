package jylonwars.scenes;

import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.games.jylonwars.TextField 
import com.gemserk.games.jylonwars.TextFieldComponent 
import com.gemserk.games.jylonwars.data.Data;

builder.entity {
	
	def textFieldFont = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
	}
	
	child(entity("label1"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
		position:utils.vector(400f, 300f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-220,-50,440,100),
		align:"center",
		valign:"center"
		])
		
		property("message", {"Your score was: $entity.parent.playtime, put your name:".toString()})
	})
	
	property("textField", new TextField("", 30))
	
	def textFieldRectangle = utils.rectangle(-220,-20,440,40)
	def textFieldPosition = utils.vector(400f, 340f)
	
	component(new RectangleRendererComponent("background")) {
		property("position", textFieldPosition)
		property("rectangle", textFieldRectangle)
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.0f,0.0f,0.5f,0.3f))
	}
	
	child(entity("label2"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:20]),
		position:textFieldPosition,
		fontColor:utils.color(1f,1f,1f,1f),
		bounds:textFieldRectangle,
		align:"center",
		valign:"center"
		])
		
		property("message", {entity.parent.textField.text})
	})
	
	component(new TextFieldComponent("textFieldTest")) {
		property("textField", {entity.textField})
		property("enabled", true)
	}
	
	property("playtime", {entity.parent.playerData["playtime"]})
	
	component(utils.components.genericComponent(id:"enterNameGameStateEndHandler", messageId:"enterNameGameStateEnd"){ message ->
		def dataStore = utils.custom.gameStateManager.gameProperties.dataStore
		
		def playtime = entity.parent.playerData["playtime"]
		def crittersDead = entity.parent.playerData["crittersdead"]
		
		// post the game score...
		
		dataStore.submit(new Data(tags:["score"], values:[name:entity.textField.text, playtime:playtime, crittersdead:crittersDead]))
		
		entity.parent.gameState = "gameover"
		
		messageQueue.enqueue(utils.genericMessage("refreshScores"){})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"enterNameGameStateEnd")
		}
	}
}

