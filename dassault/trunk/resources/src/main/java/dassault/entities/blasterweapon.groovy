package dassault.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 



builder.entity(entityName ?: "blasterweapon-${Math.random()}") {
	
	property("droid", parameters.droid)
	
	property("totalReloadTime", parameters.reloadTime)
	property("reloadTime", parameters.reloadTime)
	property("damage", parameters.damage)
	property("loaded", parameters.loaded ?: false)
	
	property("weaponEnergy", parameters.energy)
	
	property("bulletTemplate", parameters.bulletTemplate)
	
	component(utils.components.genericComponent(id:"weaponComponent", messageId:"update"){ message ->
		// def droid = entity.root.getEntityById(entity.ownerId)
		def droid = entity.droid
		
		def loaded = entity.loaded ?: false
		
		if (droid == null) {
			log.error("Owner is null - ownerId : $entity.ownerId - weapon.id : $entity.id")
			return
		}
			
		def energy = droid.energy
		
		def weaponEnergy = entity.weaponEnergy
		
		// owner hasEnergy?
		if (droid.shouldFire && loaded && energy.current > weaponEnergy) {
			
			def bulletTemplate = entity.bulletTemplate
			
			def fireDirection = droid.fireDirection
			def position = droid.position
			
			def bulletSpeed = 0.5f
			
			def bullet = bulletTemplate.instantiate("blasterbullet-${utils.random.nextInt()}", // 
					[position:position, moveDirection:fireDirection, owner:droid, speed:bulletSpeed, damage:entity.damage, player:droid.player])
			
			// I dont like the entity.parent.parent to point to world
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet,entity.parent.parent))
			
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
