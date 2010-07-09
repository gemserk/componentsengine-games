package dosdewinia.entities

import org.newdawn.slick.Color;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 
import com.gemserk.games.dosdewinia.Target;

import dosdewinia.components.BoundsTriggers;

builder.entity("darwinian-${Math.random()}") {
	
	
	Random random = new Random()
	
	tags("darwinian","nofriction")
	
	property("position", parameters.position)
	property("nextPosition",parameters.position)
	
	property("targetSelectionRadius",40)
	
	property("targetPosition",null)
	
	propertyRef("direction", "movement.velocity")
	property("mapData",parameters.mapData)
	property("zoneMap",parameters.mapData.zoneMap)
	property("traversableMap",parameters.mapData.traversableMap)
	
	property("speed", parameters.speed)
	
	property("target",parameters.target)
	
	property("state",parameters.state ?: "wander")
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("darwinian"))
		property("color", {new Color(entity.zoneMap.getZoneValue(entity.position))})//utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", utils.vector(1,0))
		property("size",utils.vector(0.5f,0.5f))
		property("layer", 1)
	}
	
	component(new SuperMovementComponent("movement")){
		property("velocity",utils.vector(entity.speed,0))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "nextPosition")
	}
	
	component(new WorldBoundsComponent("bounds")){
		property("bounds",utils.rectangle(0,0,800,600))
		propertyRef("position","position")
	}
	
	
	component(utils.components.genericComponent(id:"reachedNextDestinationChecker", messageId:"update"){ message ->
		def position = entity.position
		def targetPosition = entity.targetPosition
		if(targetPosition){
			if(targetPosition.distanceSquared(position) < 9){
				entity."movement.velocity"=utils.vector(0,0)
				entity.targetPosition = null
				if(entity.state=="goTowardsTarget")
					entity.state = "wander"
			}
		} 
	})
	
	component(utils.components.genericComponent(id:"wanderBehaviour", messageId:"update"){ message ->
		def position = entity.position
		def targetPosition = entity.targetPosition
		if(targetPosition)
			return
		
		if(entity.state != "wander")
			return
		
		def zoneMap = entity.zoneMap
		def direction
		def randomTargetPosition
		def target = entity.target
		def targetCenter = target.position
		def zoneId = target.zoneId
		def targetWanderRadiusSquared = (float)target.wanderRadius * target.wanderRadius
		while(randomTargetPosition == null ){//|| zoneMap.getZoneValue(randomTargetPosition) != zoneId){
			direction = utils.vector(1,0).add((float)random.nextFloat()*360)
			randomTargetPosition = position.copy().add(direction.copy().scale((float)random.nextFloat()*100))
		}
		entity.targetPosition = randomTargetPosition
		entity."movement.velocity" = direction.copy().scale(entity.speed)
		
		
	})
	
	component(utils.components.genericComponent(id:"goTowardsTarget", messageId:"update"){ message ->
		def position = entity.position
		def targetPosition = entity.targetPosition
		if(targetPosition)
			return
		
		if(entity.state != "goTowardsTarget")
			return
		
		def traversableMap = entity.traversableMap
		def direction
		def randomTargetPosition
		def target = entity.target
		def targetCenter = target.position
		def targetWanderRadiusSquared = (float)target.wanderRadius * target.wanderRadius
		while(randomTargetPosition == null || !traversableMap.getTraversable(randomTargetPosition) ){
			direction = utils.vector(1,0).add((float)random.nextFloat()*360)
			randomTargetPosition = targetCenter.copy().add(direction.copy().scale((float)random.nextFloat()*target.arrivalRadius))
		}
		entity.targetPosition = randomTargetPosition
		entity."movement.velocity" = randomTargetPosition.copy().sub(position).normalise().scale(entity.speed)
		
		
	})
	
//	component(utils.components.genericComponent(id:"7879", messageId:"update"){ message ->
//		log.info("STATE_BOUND: $entity.outsideOfBounds")
//	})
	
	property("wentOutsideLogic",{{ ->
		def position = entity.position
		entity."movement.velocity"=utils.vector(0,0)
		entity.targetPosition = null
		entity.state = "wander"
		def zoneId = entity.zoneMap.getZoneValue(position)
		entity.target = new Target(position, 30,10,zoneId)
	}})
	
	
	component(utils.components.genericComponent(id:"detectOutOfBounds", messageId:"update"){ message ->
		def nextPosition = entity.nextPosition
		def position = entity.position
		//log.info("POS: $position - NPOS: $nextPosition")
		def traversableMap = entity.traversableMap
		if(traversableMap.getTraversable(nextPosition)){
			entity.position = nextPosition
		} else {
			entity.nextPosition = entity.position
			def wentOutsideLogic = entity.wentOutsideLogic
			if(wentOutsideLogic)
				wentOutsideLogic.call()	
		}
	})
	
//	component(utils.components.genericComponent(id:"debugZoneInfo", messageId:"update"){ message ->
//		def position = entity.position
//		def zoneMap = entity.zoneMap
//		println zoneMap.getZoneValue(position)
//		
//	})
	

}