package towerofdefense.entities;
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.games.towerofdefense.CountDownTimer;

import com.gemserk.componentsengine.messages.UpdateMessage;

import com.gemserk.games.towerofdefense.components.TimerComponent;

import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;

import com.gemserk.games.towerofdefense.components.FaceTargetComponent;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 

builder.entity("lasertower-${Math.random()}") {
	
	tags("tower","lasertower")
	
	property("position", parameters.position)
	property("direction", parameters.direction)
	property("radius", parameters.radius)
	property("cost",parameters.cost)
	
	property("targetEntity", null)
	
	property("selected", false)
	
	component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		propertyRef("enabled", "selected")
	}
	
	component(new DisablerComponent(new ImageRenderableComponent("selectedAuraRenderer"))){
		property("image", utils.resources.image("towerofdefense.images.blasterbullet"))
		property("color", utils.color(1f, 1f, 1f, 1.0f))
		property("direction", utils.vector(1f,0f))
		propertyRef("position", "position")
		propertyRef("enabled", "selected")
	}
	
	component(new FaceTargetComponent("faceTarget")){
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		propertyRef("targetEntity", "targetEntity")
	}
	
	component(new SelectTargetWithinRangeComponent("selectTarget"))	{
		property("targetTag", "critter")
		propertyRef("targetEntity", "targetEntity")
		propertyRef("radius", "radius")
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("towerofdefense.images.lasertower"))
		property("color", parameters.color)
		propertyRef("position", "position")
		property("direction", utils.vector(1f,0f))
	}
	
	component(new ImageRenderableComponent("lasercannonImageRenderer")) {
		property("image", utils.resources.image("towerofdefense.images.lasercannon"))
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	def bulletId ="laserbullet-$entity.id".toString();
	
	property("canFire",true)
	property("canFireTimer",new CountDownTimer(5000))
	property("fireDurationTimer",new CountDownTimer(2000))
	
	component(new ComponentFromListOfClosures("shooter",[ {UpdateMessage message ->
		if(!entity.canFire || !entity.targetEntity )
			return
		
		entity.children[(bulletId)].enabled = true
		entity.canFireTimer.reset();
		entity.fireDurationTimer.reset();
		entity.canFire = false
	}]))
	
	component(new TimerComponent("canFireTimerComponent")){
		property("messageBuilder",utils.custom.messageBuilderFactory.messageBuilder("enableFire") {message.source = entity })
		propertyRef("timer","canFireTimer")
	}
	
	genericComponent(id:"enableFireHandler", messageId:"enableFire"){ message ->
		if(message.source != entity)
			return
		
		entity.canFire = true
	}
	
	component(new TimerComponent("fireDurationTimerComponent")){
		property("messageBuilder",utils.custom.messageBuilderFactory.messageBuilder("fireStop") {message.source = entity })
		propertyRef("timer","fireDurationTimer")
	}
	
	genericComponent(id:"fireStopHandler", messageId:"fireStop"){ message ->
		if(message.source != entity)
			return
		
		entity.children[(bulletId)].enabled = false
	}
	
	child(template:"towerofdefense.entities.laserbullet", id:bulletId)	{
		
	}
	
}
