package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicates;

builder.entity {
	
	tags("controller", parameters.player)
	
	property("player", parameters.player)
	
	component(utils.components.genericComponent(id:"controllerComponent", messageId:"update"){ message ->
		
		def player = entity.player
		def delta = message.delta
		
		def controlledEntities = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags(player, "droid")))
		
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
			
		}
	})
	
}