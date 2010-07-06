package dassault.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 



builder.entity(entityName ?: "blasterweapon-${Math.random()}") {
	
	property("ownerId", parameters.ownerId)
	property("totalReloadTime", parameters.reloadTime)
	property("reloadTime", parameters.reloadTime)
	property("damage", parameters.damage)
	property("loaded", parameters.loaded ?: false)
	
	property("weaponEnergy", parameters.energy)
	
	property("bulletTemplate", parameters.bulletTemplate)
	
	component(utils.components.genericComponent(id:"weaponComponent", messageId:"update"){ message ->
		def owner = entity.root.getEntityById(entity.ownerId)
		
		def loaded = entity.loaded ?: false
		
		if (owner == null) {
			log.error("Owner is null - weapon.id = $entity.id")
			return
		}
			
		def energy = owner.energy
		
		def weaponEnergy = entity.weaponEnergy
		
		// owner hasEnergy?
		if (owner.shouldFire && loaded && energy.current > weaponEnergy) {
			
			def bulletTemplate = entity.bulletTemplate
			
			def fireDirection = owner.fireDirection
			def position = owner.position
			
			// property
			def bulletSpeed = 0.3f
			
			def bullet = bulletTemplate.instantiate("blasterbullet-${utils.random.nextInt()}", // 
					[position:position, moveDirection:fireDirection, owner:owner, speed:bulletSpeed, damage:entity.damage])
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet,entity.parent))
			
			// owner.reduceEnergy(...)
			energy.remove(weaponEnergy)
			
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
