package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 

builder.entity {
	
	tags("player")
	
	property("color", parameters.color)
	property("enemies", [])
	
	component(utils.components.genericComponent(id:"updateTargetsForPlayer", messageId:"update"){ message ->
		
		entity.enemies = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), // could be another entity, not only droids 
		{ droid -> droid.player != entity} as Predicate))
		
	})
	
}
