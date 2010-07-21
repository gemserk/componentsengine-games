package dassault.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 



builder.entity {
	
	tags("spawner")
	
	property("position", parameters.position.copy())
	
	property("timer", parameters.maxTime)
	property("minTime", parameters.minTime)
	property("maxTime", parameters.maxTime)
	
	property("ownerId", parameters.ownerId)
	
	property("droidFactory", parameters.droidFactory)
	property("weaponFactory", parameters.weaponFactory)
	
	property("droidTypes", parameters.droidTypes)
	property("weaponTypes", parameters.weaponTypes)
	
	component(utils.components.genericComponent(id:"updateTimer", messageId:"update"){ message ->
		def timer = entity.timer
		
		timer -= message.delta
		
		if (timer <= 0) {
			utils.custom.messageQueue.enqueue(utils.genericMessage("spawnDroid"){ newMessage ->
				newMessage.spawnerId = entity.id
			})
			timer = utils.random.nextInt(entity.maxTime) + entity.minTime
		}
		
		entity.timer = timer
	})
	
	def random = utils.random
	
	component(utils.components.genericComponent(id:"spawnDroid", messageId:"spawnDroid"){ message ->
		
		if (entity.id != message.spawnerId)
			return
		
		def droidTypes = entity.droidTypes
		def droidType = droidTypes[random.nextInt(droidTypes.size)]
		                           
		def droidTemplate = entity.droidFactory[droidType]
		def droid = droidTemplate.get([position:entity.position.copy(), ownerId:entity.ownerId])

		def weaponTypes = entity.weaponTypes
		def weaponType = weaponTypes[random.nextInt(weaponTypes.size)]
		
		def weaponTemplate = entity.weaponFactory[weaponType]
		def weapon = weaponTemplate.get([owner:droid])
		
		// def droid = entity.droidTemplate.instantiate(droidId, [position:entity.position.copy(), ownerId:entity.ownerId])
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(droid,entity.parent))
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(weapon,droid))
	})
	
}
