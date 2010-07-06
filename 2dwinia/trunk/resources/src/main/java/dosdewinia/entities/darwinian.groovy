package dosdewinia.entities

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 
import com.gemserk.games.dosdewinia.Target;

builder.entity("darwinian-${Math.random()}") {
	
	def traversableColor = utils.color(1,1,1,1)
	def traversable = { terrainMap, position ->
		def x = (int)position.x
		def y = (int)position.y
		if(x < 0 || y < 0 || x > terrainMap.width || y > terrainMap.height)
			return false
		
		def terrainColor = terrainMap.getColor((int)position.x, (int)position.y)
		return terrainColor == traversableColor
	}
	
	
	Random random = new Random()
	
	tags("darwinian","nofriction")
	
	property("position", parameters.position)
	
	property("targetSelectionRadius",40)
	
	property("targetPosition",null)
	
	propertyRef("direction", "movement.velocity")
	property("terrainMap",utils.resources.image("terrainMap"))
	
	property("speed", parameters.speed)
	
	property("target",parameters.target)
	
	property("state",parameters.state ?: "wander")
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("darwinian"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", utils.vector(1,0))
		property("size",utils.vector(0.5f,0.5f))
		property("layer", 1)
	}
	
	component(new SuperMovementComponent("movement")){
		property("velocity",utils.vector(entity.speed,0))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "position")
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
		
		def terrainMap = entity.terrainMap
		def direction
		def randomTargetPosition
		def target = entity.target
		def targetCenter = target.position
		def targetWanderRadiusSquared = (float)target.wanderRadius * target.wanderRadius
		while(randomTargetPosition == null || !traversable(terrainMap, randomTargetPosition) || targetCenter.distanceSquared(randomTargetPosition) > targetWanderRadiusSquared){
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
		
		def terrainMap = entity.terrainMap
		def direction
		def randomTargetPosition
		def target = entity.target
		def targetCenter = target.position
		def targetWanderRadiusSquared = (float)target.wanderRadius * target.wanderRadius
		while(randomTargetPosition == null || !traversable(terrainMap, randomTargetPosition) ){
			direction = utils.vector(1,0).add((float)random.nextFloat()*360)
			randomTargetPosition = targetCenter.copy().add(direction.copy().scale((float)random.nextFloat()*target.arrivalRadius))
		}
		entity.targetPosition = randomTargetPosition
		entity."movement.velocity" = randomTargetPosition.copy().sub(position).normalise().scale(entity.speed)
		
		
	})
	
	
	
	property("outsideOfBounds",false)
	component(utils.components.genericComponent(id:"islandboundchecker", messageId:"update"){ message ->
		def position = entity.position
		if( !traversable(entity.terrainMap, position)){
			if(!entity.outsideOfBounds){
				entity."movement.velocity"=utils.vector(0,0)
				entity.targetPosition = null
				entity.state = "wander"
				entity.target = new Target(position, 30,10)
				entity.outsideOfBounds = true
			}
		}else{
			entity.outsideOfBounds = false
		}
		
	})
	
	
	
	
}