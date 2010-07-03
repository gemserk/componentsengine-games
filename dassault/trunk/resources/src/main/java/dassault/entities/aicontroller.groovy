package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicates;

builder.entity {
	
	tags("controller", parameters.player)
	
	property("player", parameters.player)
	
	component(utils.components.genericComponent(id:"controllerComponent", messageId:"update"){ message ->
		
		def player = entity.player
		
		def controlledEntities = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags(player, "droid")))
		
		controlledEntities.each { controlledEntity -> 
			
			controlledEntity.moveDirection = utils.vector(1,0)
			
		}
	})
	
}
