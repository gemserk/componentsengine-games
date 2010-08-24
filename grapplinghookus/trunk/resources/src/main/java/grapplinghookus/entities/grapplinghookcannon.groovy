package grapplinghookus.entities

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

builder.entity {
	
	tags("grapplinghookcannon")
	
	property("base", parameters.base)
	property("cursor", parameters.cursor)
	
	property("targetEnemy", null)
	
	component(utils.components.genericComponent(id:"updateTargetOnUpdate", messageId:"update"){ message ->
		def cursorPosition = entity.cursor.position
		
		def targetEnemy = entity.targetEnemy 
		
		if (targetEnemy == null ) {
			
			entity.targetEnemy = null
			
			def enemies = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("enemy"), //
					{enemy -> !enemy.targeted} as Predicate, //
					{enemy -> enemy.bounds.contains(cursorPosition.x, cursorPosition.y) } as Predicate))
			
			if (enemies.isEmpty()) 
				return
			
			entity.targetEnemy = enemies[0]
			
			log.debug("target adquired : enemy.id - ${entity.targetEnemy.id}")
		} else {
			
			if (!targetEnemy.bounds.contains(cursorPosition.x, cursorPosition.y) || targetEnemy.targeted) {
				log.debug("target lost : enemy.id - ${entity.targetEnemy.id}")
				entity.targetEnemy = null
			}
			
		}
		
	})
	
	component(utils.components.genericComponent(id:"shootGrapplingHook", messageId:"shootGrapplingHook"){ message ->
		
		if (entity.targetEnemy == null)
			return
		
		def grapplingHooks = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("grapplinghook"), // 
				{grapplinghook -> grapplinghook.state == "idle"} as Predicate))
		
		if (grapplingHooks.isEmpty()) {
			log.debug("there is no grappling hooks idle")
			return
		}
		
		def position = entity.targetEnemy.position
		
		grapplingHooks.sort { it.position.distance(position) }
		
		// select nearest to target
		def grapplinghook = grapplingHooks[0]
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("grapplingHookShooted") { newMessage ->
			newMessage.grapplinghookcannon = entity
			newMessage.grapplinghook = grapplinghook
			newMessage.targetEnemy = entity.targetEnemy
		}) 
		
	})
	
	component(utils.components.genericComponent(id:"grapplingHookShootedHandler", messageId:"grapplingHookShooted"){ message ->
		if (message.grapplinghookcannon != entity)
			return
	})
	
	input("inputmapping"){
		mouse {
			press(button:"right", eventId:"shootGrapplingHook")
		}
	}
	
}
