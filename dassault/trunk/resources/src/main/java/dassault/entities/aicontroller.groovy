package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

builder.entity {
	
	tags("controller")
	
	property("ownerId", parameters.ownerId)
	
	component(utils.components.genericComponent(id:"controllerComponent", messageId:"update"){ message ->
		
		def ownerId = entity.ownerId
		def owner = entity.root.getEntityById(ownerId)
		
		def controlledDroidId = owner.controlledDroidId ?: null
		
		def delta = message.delta
		
		def controlledEntities = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), // 
				{ droid -> droid.ownerId == ownerId && droid.id != controlledDroidId} as Predicate))

		def enemyTargets = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), // could be another entity, not only droids 
				{ enemyTarget -> enemyTarget.ownerId != ownerId} as Predicate))
				
		def target = enemyTargets.empty ? null : enemyTargets[0]
				
		controlledEntities.each { controlledEntity -> 
			
			def wanderDirection = controlledEntity.wanderDirection ?: utils.vector(1,0)
			def wanderTime = controlledEntity.wanderTime ?: 0
			
			if (wanderTime <= 0) {
				wanderDirection.x = (float) utils.random.nextFloat() - 0.5f
				wanderDirection.y = (float) utils.random.nextFloat() - 0.5f
				wanderDirection.normalise()
				
				wanderTime = utils.random.nextInt(1000) + 1000
				//				println "wanderDirection : $wanderDirection , wanderTime : $wanderTime"
			} else {
				wanderTime = wanderTime - delta
			}
			
			controlledEntity.wanderDirection = wanderDirection.copy()
			controlledEntity.moveDirection = controlledEntity.wanderDirection.copy()
			controlledEntity.wanderTime = wanderTime

			controlledEntity.shouldFire = false
			
			if (!target)
				return
				
			controlledEntity.shouldFire = true
			controlledEntity.fireDirection = target.position.copy().sub(controlledEntity.position).normalise()
			
		}
	})
	
}
