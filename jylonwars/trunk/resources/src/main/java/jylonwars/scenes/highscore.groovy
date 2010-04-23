package jylonwars.scenes;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.games.jylonwars.data.Data;

import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	property("enabled", {entity.parent.gameState != "playing" })
	
	component(new ProcessingDisablerComponent("disableStateComponent")){  propertyRef("enabled", "enabled") }
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
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
	
	child(entity("scoresLabel-playtime".toString()){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, (float)(130)),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"right",
		valign:"center"
		])
		
		property("message", "PLAYTIME")
	})
	
	
	
	component(utils.components.genericComponent(id:"refreshSCoresHandler", messageId:"refreshScores"){ message ->
		def newEntities = []
		def dataStore = utils.custom.gameStateManager.gameProperties.dataStore
		
		scores = dataStore.get([] as Set).sort { it.values.playtime}.reverse()
		
		def createLabel = { data, scoreId -> 
			
			newEntities << entity("scoresLabel-name-${scoreId}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(400f, (float)(170 + 30 * scoreId)),
				fontColor:utils.color(0f,0f,0f,1f),
				bounds:labelRectangle,
				align:"left",
				valign:"center"
				])
				
				property("message", "$data.values.name".toString())
			}
			
			newEntities << entity("scoresLabel-playtime-${scoreId}".toString()){
				
				parent("gemserk.gui.label", [
				font:font,
				position:utils.vector(400f, (float)(170 + 30 * scoreId)),
				fontColor:utils.color(0f,0f,0f,1f),
				bounds:labelRectangle,
				align:"right",
				valign:"center"
				])
				
				property("message", "$data.values.playtime".toString())
			}
		}
		
		createLabel.setResolveStrategy Closure.DELEGATE_FIRST
		
		scores.eachWithIndex(createLabel)
		
		
		newEntities.each { newEntity -> 
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newEntity, entity))
		}
	})
}

