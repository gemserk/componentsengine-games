package dassault.entities


import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicates;

builder.entity {
	
	component(utils.components.genericComponent(id:"weaponComponent", messageId:"update"){ message ->
		
		def player = entity.player
		
		def entities = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("blasterweapon")))
		
		entities.each { controlledEntity -> 
			
			if (!controlledEntity.shouldFire)
				return
				
			println "$controlledEntity.id shouldFire!!"
			// create a bullet using properties of the controlledEntity
		
		}
	})
	
}
