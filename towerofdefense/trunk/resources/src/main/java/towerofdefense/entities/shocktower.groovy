package towerofdefense.entities;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.TimerComponent;
import org.newdawn.slick.Graphics 
import org.newdawn.slick.geom.Line;

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.missiletower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
	parameters.fireAngle = 360.0f;
	parameters.turnRate = 0f
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("shocktower")
	
	property("reloadTimer",new CountDownTimer(parameters.reloadTime))
	property("canFire",true)
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
	, { SlickRenderMessage message ->
		if(!entity.canFire)
			return
		
		if(entity.targetEntity == null)
			return
		
		Graphics g = message.graphics
		g.draw(new Line(entity.position,entity.targetEntity.position))
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
	
	
	
	
}
