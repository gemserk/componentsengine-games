package towerofdefense.entities;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;

import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.games.towerofdefense.components.DebugVectorComponent;
import com.gemserk.games.towerofdefense.components.SelectTargetsWithinRangeComponent;
import com.gemserk.games.towerofdefense.components.DebugVectorComponent.DebugVector;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.SlickCallable 

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.shocktower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.shockcannon")
	parameters.fireAngle = 360.0f;
	parameters.turnRate = 0f
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("shocktower")
	
	property("reloadTimer",new CountDownTimer(1000))
	property("canFire", true)
	property("shockFiredTimer", new CountDownTimer(1000))
	
	property("targets", [])

	property("sound", utils.resources.sounds.sound("towerofdefense.sounds.shock"))
	
	component(new SelectTargetsWithinRangeComponent("selectTarget")) {
		propertyRef("targets", "targets")
		propertyRef("max", "maxTargets")
	}
	
	component(new ComponentFromListOfClosures("shockWeapon",[ {UpdateMessage message ->
		if(!entity.canFire)
		return
		
		if (entity.upgrading) 
		return;
		
		def targets = entity.targets
		
		if (targets.isEmpty())
		return
		
		def shockCritter = { targetEntity ->
			def targetVelocity  = targetEntity."movement.velocity"
			def shockFactor = entity.shockFactor
			def shockForce = targetVelocity.copy().scale((float)-shockFactor)
			entity.debugVectors << new DebugVector(targetEntity.position.copy(), targetEntity.forwardForce.copy().add(shockForce).scale(1000000),utils.color(1,1,0,1))
			targetEntity."movement.force".add(shockForce)
		}
		
		targets.each(shockCritter)
		
		def shockFiredTimer = entity.shockFiredTimer
		if(!shockFiredTimer.isRunning()){
			
			entity.reloadTimer = new CountDownTimer(entity.reloadTime)
			entity.shockFiredTimer = new CountDownTimer(entity.fireDuration)
			
			entity.reloadTimer.reset()
			entity.shockFiredTimer.reset()
			
			entity.sound.play()
		}
	}
	]))
	
	
	property("debugVectors",[])
	component(new DebugVectorComponent("debugVector")){
		propertyRef("vectors","debugVectors")
		property("enabled",false)
	}
	
	
	component(new TimerComponent("reloadTimerComponent")){
		propertyRef("timer","reloadTimer")
		property("trigger",utils.custom.triggers.closureTrigger {entity.canFire = true
		})
	}
	
	component(new TimerComponent("shockFiredTimerComponent")){
		propertyRef("timer","shockFiredTimer")
		property("trigger",utils.custom.triggers.closureTrigger {entity.canFire = false
		})
	}
	
	component(new ComponentFromListOfClosures("effect", [ { UpdateMessage m ->
		
		def targets = entity.targets
		
		if (targets.isEmpty()) {
			entity.effect = null
			return
		}
		
		def targetEntity = targets[0]
		
		if (entity.effect != null && !entity.effect.isDone() ) {
			entity.effect.start = entity.position
			entity.effect.end = targetEntity.position
			entity.effect.update(m.delta)
			return
		}
		
		def shockFiredTimer = entity.shockFiredTimer
		if (!shockFiredTimer.isRunning())
		return
		
		def start = entity.position
		def end = targetEntity.position
		
		def beamColor = new Color(1.0f, 1.0f, 0.0f, 0.5f);
		def beamDuration = entity.fireDuration
		
		entity.effect = EffectFactory.beamEffect(beamDuration, start, end, 1.0f, 15.0f, beamColor)
		
	}, {SlickRenderMessage m ->
		
		if (entity.upgrading) 
		return;
		
		if (!entity.effect)
		return
		
		def targets = entity.targets
		
		SlickCallable.enterSafeBlock();
		targets.each { targetEntity ->
			entity.effect.end = targetEntity.position
			entity.effect.render();
		}
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
		property("direction", {utils.vector(1,0).add(entity.rotationValue)
		})
		property("size",utils.vector(0.85f,0.85f))
	}
	
}
