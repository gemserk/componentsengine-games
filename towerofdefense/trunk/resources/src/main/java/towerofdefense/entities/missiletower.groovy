package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.missiletower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.missilecannon")
	parameters.fireAngle = 360.0f;
	
	parent("towerofdefense.entities.tower", parameters)
	tags("missiletower")
	
	property("bulletTemplate", parameters.bulletTemplate)
	property("sound", utils.resources.sounds.sound("towerofdefense.sounds.missilebullet"))
	
	component(new DisablerComponent(new WeaponComponent("shooter"))) {
		propertyRef("enabled", "weaponEnabled")
		propertyRef("reloadTime", "reloadTime")
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
		
		property("trigger", utils.custom.triggers.closureTrigger { tower -> 
			def bulletTemplate = entity.bulletTemplate
			
			def bullet = bulletTemplate.get(tower)
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
			
			entity.sound.play(3.0f, 0.3f);
		})
	}
	
	
}
