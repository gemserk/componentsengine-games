package towerofdefense.entities;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage;

import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates 





import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.games.towerofdefense.CountDownTimer;
import com.gemserk.games.towerofdefense.GenericHitComponent 
import com.gemserk.games.towerofdefense.components.AngleUtils;
import com.gemserk.games.towerofdefense.components.TimerComponent;
import com.google.common.base.Predicates;

import org.newdawn.slick.geom.Line 
import org.newdawn.slick.geom.Vector2f 


builder.entity("missilebullet-${Math.random()}") {
	
	parent("towerofdefense.entities.bullet",parameters)
	tags("missile")
	
	property("targetEntity",parameters.targetEntity)
	property("blastRadius",parameters.blastRadius)
	property("turnRatio",parameters.turnRatio)
	
	component(new ComponentFromListOfClosures("steering",[ {UpdateMessage message ->
		if(entity.targetEntity == null)
			return;
		
		def debugVectors = []
		
		
		Vector2f position = entity.position
		Vector2f velocity = entity.direction
		
		debugVectors << [vector:velocity.copy().normalise().scale(50f), color:Color.red]
		
		Vector2f targetPosition = entity.targetEntity.position
		//Vector2f targetPosition = new Vector2f(20,300)
		
		Vector2f directionToTarget = targetPosition.copy().sub(position)
		debugVectors << [vector:directionToTarget.copy(), color:Color.yellow]
		
		def desiredVelocity = directionToTarget.normalise().scale(entity."movement.maxVelocity")
		
		debugVectors << [vector:desiredVelocity.copy().scale(50f), color:Color.green]
		
		def steerVelocity = desiredVelocity.copy().sub(velocity)
		debugVectors << [vector:steerVelocity.copy().scale(50f), color:Color.pink]            
		
		
		def nextAngle = new AngleUtils().calculateTruncatedNextAngle((float)(entity.turnRatio*message.delta),velocity.getTheta(),steerVelocity.getTheta())
		def steerForceLength = 1f
		
		def force = new Vector2f(steerForceLength,0).add(nextAngle)
		
		
		
		debugVectors << [vector:force.copy().normalise().scale(50f), color:Color.blue]
		
		entity."movement.force".add(force)
		
		//entity.debugVectors = debugVectors
		
	}, {SlickRenderMessage message ->
		def debugVectors = entity.debugVectors
		if( debugVectors == null)
			return
		
		
		Graphics g = message.graphics
		Vector2f start = entity.position.copy()
		
		debugVectors.each { vectorContainer ->
			
			def vector = vectorContainer.vector
			def color = vectorContainer.color ?: Color.white
			
			Vector2f segment = vector.copy()
			
			Line line = new Line(start.x,start.y,segment.x,segment.y,true)
			def origColor = g.getColor()
			g.setColor(color)
			g.draw(line)
			g.setColor(origColor)
		}
		
	}
	
	]))
	
	
	component(new GenericHitComponent("bullethit")){
		property("targetTag", "critter")
		property("predicate",{Predicates.and(Predicates.equalTo(entity.targetEntity), EntityPredicates.isNear(entity.position, entity.radius))})
		property("trigger", utils.custom.triggers.closureTrigger { data -> 
			def source = data.source
			def targets = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("critter"),EntityPredicates.isNear(entity.targetEntity.position, entity.blastRadius)))//find targets in range (it is bigger than impact radius)
			
			targets.each { target ->
				def distance = entity.targetEntity.position.copy().sub(target.position).length()
				messageQueue.enqueue(utils.genericMessage("hit"){ message ->
					def damage = entity.damage
					message.damage = (float)(damage - (damage/entity.blastRadius)*distance)
					message.targets = [target]
					message.source = source
				})
			}
			
			
		})
	}
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		if(message.critter == entity.targetEntity){
			entity.timeoutTimer.reset()
			entity.targetEntity = null
		}
	}
	
	genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		if(message.critter == entity.targetEntity){
			entity.timeoutTimer.reset()
			entity.targetEntity = null
		}
	}
	
	
	
	property("timeoutTimer", new CountDownTimer(3000))
	
	component(new TimerComponent("timeoutComponent")){
		propertyRef("timer", "timeoutTimer")
		property("trigger", utils.custom.triggers.closureTrigger {
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
		})
	}
	
	
	
}
