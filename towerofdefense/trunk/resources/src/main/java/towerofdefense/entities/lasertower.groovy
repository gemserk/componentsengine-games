package towerofdefense.entities;
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.games.towerofdefense.CountDownTimer;

import com.gemserk.componentsengine.messages.UpdateMessage;

import com.gemserk.games.towerofdefense.components.TimerComponent;





builder.entity("lasertower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.lasertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.lasercannon")
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("lasertower")
	
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
		damage = parameters.damage
	}
	
}
