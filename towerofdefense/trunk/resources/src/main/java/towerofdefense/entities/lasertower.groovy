package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.timers.CountDownTimer 





builder.entity("lasertower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.lasertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.lasercannon")
	parameters.fireAngle = 30.0f;
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("lasertower")
	
	def bulletId ="laserbullet-$entity.id".toString();
	
	property("canFire",true)
	property("canFireTimer",new CountDownTimer(5000))
	property("fireDurationTimer",new CountDownTimer(2000))
	
	component(new ComponentFromListOfClosures("shooter",[ {UpdateMessage message ->
		if(!entity.canFire || !entity.targetEntity )
			return
			
		if (!entity.weaponEnabled) 
			return;
		
		entity.children[(bulletId)].enabled = true
		entity.canFireTimer.reset();
		entity.fireDurationTimer.reset();
		entity.canFire = false
	}]))
	
	component(new TimerComponent("canFireTimerComponent")){
		property("trigger",utils.custom.triggers.genericMessage("enableFire") {message.source = entity })
		propertyRef("timer","canFireTimer")
	}
	
	component(utils.components.genericComponent(id:"enableFireHandler", messageId:"enableFire"){ message ->
		if(message.source != entity)
			return
		
		entity.canFire = true
	})
	
	component(new TimerComponent("fireDurationTimerComponent")){
		property("trigger",utils.custom.triggers.genericMessage("fireStop") {message.source = entity })
		propertyRef("timer","fireDurationTimer")
	}
	
	component(utils.components.genericComponent(id:"fireStopHandler", messageId:"fireStop"){ message ->
		if(message.source != entity)
			return
		
		entity.children[(bulletId)].enabled = false
	})
	
	child(template:"towerofdefense.entities.laserbullet", id:bulletId)	{
		damage = parameters.damage
	}
	
}
