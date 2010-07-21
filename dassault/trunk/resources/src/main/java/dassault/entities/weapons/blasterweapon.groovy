package dassault.entities.weapons

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 

builder.entity(entityName ?: "blasterweapon-${Math.random()}") {
	
	property("owner", parameters.owner)
	
	property("totalReloadTime", parameters.reloadTime)
	property("reloadTime", parameters.reloadTime)
	property("damage", parameters.damage)
	property("loaded", parameters.loaded ?: false)
	
	property("weaponEnergy", parameters.energy)
	
	property("bulletTemplate", parameters.bulletTemplate)
	
	component(utils.components.genericComponent(id:"weaponComponent", messageId:"update"){ message ->
		def owner = entity.owner
		
		def loaded = entity.loaded ?: false
		
		if (owner == null) {
			log.error("Owner is null - ownerId : $entity.ownerId - weapon.id : $entity.id")
			return
		}
		
		def world = entity.parent.parent
		if (world == null) {
			log.error("An update came when I am not on the world: weapon.id : $entity.id")
			return
		}
			
		def energy = owner.energy
		
		def weaponEnergy = entity.weaponEnergy
		
		// owner hasEnergy?
		if (owner.shouldFire && loaded && energy.current > weaponEnergy) {
			
			def bulletTemplate = entity.bulletTemplate
			
			def fireDirection = owner.fireDirection
			def position = owner.position
			
			def bulletSpeed = 0.5f
			
			def bullet = bulletTemplate.instantiate("blasterbullet-${utils.random.nextInt()}", // 
					[position:position, moveDirection:fireDirection, owner:owner, speed:bulletSpeed, damage:entity.damage, player:owner.player])
			
			// I dont like the entity.parent.parent to point to world
			// IT FAILS IF THE DROID IS REMOVED AND AN UPDATE CAME, THE PARENT.PARENT IS NULL
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, world.id))
			
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
