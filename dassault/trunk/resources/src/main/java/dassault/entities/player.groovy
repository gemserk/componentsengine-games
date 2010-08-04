package dassault.entities

import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 

builder.entity {
	
	tags("player")
	
	property("color", parameters.color)
	property("enemies", [])
	
	property("name", "Guest-${Math.random()}".toString())
	property("points", 0)
	
	component(utils.components.genericComponent(id:"updateTargetsForPlayer", messageId:"update"){ message ->
		
		def entity = entity
		
		entity.enemies = entity.root.getEntities(Predicates.and( //
				EntityPredicates.withAllTags("droid"), // could be another entity, not only droids 
				{ droid -> droid.player != entity} as Predicate, //
				))
		
	})
	
	property("droidsLimit", parameters.droidsLimit)
	property("droidCount", 0)
	
	component(utils.components.genericComponent(id:"incrementDroidsCountWhenDroidSpawned", messageId:"droidSpawned"){ message ->
		def droid = message.droid
		
		if (droid.player != entity)
			return
		
		entity.droidCount = entity.droidCount + 1 
		
		log.debug("Droid spawned : player $entity.id - count $entity.droidCount")
	})
	
	component(utils.components.genericComponent(id:"decrementDroidsCountWhenDroidDies", messageId:"droidDead"){ message ->
		def droid = message.droid
		
		if (droid.player != entity)
			return
		
		entity.droidCount = entity.droidCount - 1
		
		log.debug("Droid died: player $entity.id - count $entity.droidCount")
	})
	
}
