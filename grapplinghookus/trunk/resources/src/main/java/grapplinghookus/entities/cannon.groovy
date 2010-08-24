package grapplinghookus.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;



builder.entity {
	
	tags("cannon")
	
	property("base", parameters.base)
	property("cursor", parameters.cursor)
	
	component(utils.components.genericComponent(id:"shootEnemyHandler", messageId:"shootEnemy"){ message ->
		
		def grapplingHooks = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("grapplinghook"), // 
		{grapplinghook -> grapplinghook.state == "waitingToShoot"} as Predicate))
		
		if (grapplingHooks.isEmpty()) {
			log.debug("there is no grappling hooks waiting to be shooted")
			return
		}
		
		def grapplinghook = grapplingHooks[0]
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("enemyWasShooted") { newMessage ->
			newMessage.cannon = entity
			newMessage.grapplinghook = grapplinghook
			newMessage.trappedenemy = grapplinghook.trappedEnemy
		}) 
		
	})
	
	component(utils.components.genericComponent(id:"enemyWasShootedHandler", messageId:"enemyWasShooted"){ message ->
		if (message.cannon != entity)
			return
		log.debug("an enemy was fired: cannon.id - $entity.id")
		// instantiate a new bullet
		
		def trappedenemy = message.trappedenemy
		
		def enemyFactory = utils.custom.enemyFactory
		
		def cursor = entity.cursor
		def position = message.grapplinghook.position
		
		def direction = cursor.position.copy().sub(position).normalise()
		
		def bullet = enemyFactory.enemybullet("bullet-${Math.random()}", [
				position:position,
				moveDirection:direction,
				speed:0.12f,
				enemy:trappedenemy.enemy,
				])
				
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(trappedenemy))
		
	})
	
	input("inputmapping"){
		mouse {
			press(button:"left", eventId:"shootEnemy")
		}
	}
	
}
