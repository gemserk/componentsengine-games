package towerofdefense.entities;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.effects.EffectFactory 

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.SlickCallable 

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.shocktower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.shockcannon")
	parameters.fireAngle = 360.0f;
	parameters.turnRate = 0f
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("shocktower")
	
	property("reloadTimer",new CountDownTimer(parameters.reloadTime))
	property("canFire",true)
	property("fireDuration", parameters.fireDuration)
	property("shockFiredTimer",new CountDownTimer(parameters.fireDuration))
	property("shockFactor", parameters.shockFactor)
	
	component(new ComponentFromListOfClosures("shockWeapon",[ {UpdateMessage message ->
		if(!entity.canFire)
			return
		
		def targetEntity = entity.targetEntity
		if(targetEntity == null)
			return
		
		def targetVelocity  = targetEntity."movement.velocity"
		def targetVelocityLength = targetVelocity.length()
		def targetMaxVelocity = targetEntity."movement.maxVelocity"
		def shockFactor = entity.shockFactor
		
		def velocityReduction = targetVelocityLength*shockFactor*message.delta
		
		def newVelocityLenghtCandidate = targetVelocityLength - velocityReduction
		
		def newVelocityLength = Math.max(1/1000,newVelocityLenghtCandidate)
		
		def newVelocity =targetVelocity.copy().normalise().scale((float)newVelocityLength)
		targetEntity."movement.velocity"=newVelocity
		
		def shockFiredTimer = entity.shockFiredTimer
		if(!shockFiredTimer.isRunning()){
			entity.reloadTimer.reset()
			shockFiredTimer.reset()
		}
	}
	]))
	
	component(new TimerComponent("reloadTimerTimerComponent")){
		propertyRef("timer","reloadTimer")
		property("trigger",utils.custom.triggers.closureTrigger {entity.canFire = true})
	}
	
	component(new TimerComponent("shockFiredTimerComponent")){
		propertyRef("timer","shockFiredTimer")
		property("trigger",utils.custom.triggers.closureTrigger {entity.canFire = false})
	}
	
	component(new ComponentFromListOfClosures("effect", [ { UpdateMessage m ->
		
		if (!entity.targetEntity) {
			entity.effect = null
			return
		}
		
		if (entity.effect != null && !entity.effect.isDone() ) {
			entity.effect.start = entity.position
			entity.effect.end = entity.targetEntity.position
			entity.effect.update(m.delta)
			return
		}

		def shockFiredTimer = entity.shockFiredTimer
		if (!shockFiredTimer.isRunning())
			return
		
		def start = entity.position
		def end = entity.targetEntity.position
		
		def beamColor = new Color(1.0f, 1.0f, 0.0f, 0.5f);
		def beamDuration = entity.fireDuration
		
		entity.effect = EffectFactory.beamEffect(beamDuration, start, end, 1.0f, 16.0f, beamColor)
			
		// entity.effect = EffectFactory.lightingBoltEffect(start, end, 4, 20f, 30f, 0.3f, 300, 1.0f)
		
	}, {SlickRenderMessage m ->
		
		if (!entity.effect)
			return
		
		SlickCallable.enterSafeBlock();
		
		entity.effect.render();
		
		SlickCallable.leaveSafeBlock();
		
	}]))
	
	property("rotationValue", 0f)
	
	component(new Component("faceTarget")){
		
	}
	
	component(new IncrementValueComponent("rotator")) {
		
		def totalTime = 10f * 1000f 
		def roundTime = (float)(360f / totalTime) // time to rotate 360 degrees
		
		propertyRef("value", "rotationValue")
		property("maxValue", 360f)
		property("increment", roundTime)
	}
	
	component(new ImageRenderableComponent("cannonRenderer")) {
		property("image", parameters.cannonImage)
		property("color", parameters.color)
		propertyRef("position", "position")
		property("direction", {utils.vector(1,0).add(entity.rotationValue)})
		property("size",utils.vector(0.85f,0.85f))
	}
	
}
