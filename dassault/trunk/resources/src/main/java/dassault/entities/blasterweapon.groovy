package dassault.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 



builder.entity {
	
	property("owner", parameters.owner)
	property("totalReloadTime", parameters.reloadTime)
	property("reloadTime", parameters.reloadTime)
	property("damage", parameters.damage)
	property("loaded", parameters.loaded ?: false)
	
	property("bulletTemplate", parameters.bulletTemplate)
	
	component(utils.components.genericComponent(id:"weaponComponent", messageId:"update"){ message ->
		def owner = entity.root.getEntityById(entity.owner)
		
		def loaded = entity.loaded ?: false
		
		// owner hasEnergy?
		if (owner.shouldFire && loaded) {
			
			def bulletTemplate = entity.bulletTemplate
			
			def fireDirection = owner.fireDirection
			def position = owner.position
			
			// property
			def bulletSpeed = 0.3f
			
			def bullet = bulletTemplate.instantiate("blasterbullet-${utils.random.nextInt()}", // 
					[position:position, moveDirection:fireDirection, owner:owner, speed:bulletSpeed, damage:entity.damage])
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet,entity.parent))
			
			// owner.reduceEnergy(...)
			
			entity.reloadTime = entity.totalReloadTime
		}
		
	})
	
	component(utils.components.genericComponent(id:"reloadWeapon", messageId:"update"){ message ->
		def reloadTime = entity.reloadTime 
		reloadTime = reloadTime - message.delta
		if (reloadTime < 0)
			reloadTime = 0
		entity.loaded = (reloadTime == 0)
		entity.reloadTime = reloadTime
	})
	
}
