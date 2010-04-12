package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.DisablerComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.components.ChildrenManagementComponent;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;


builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.blastertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
	parameters.fireAngle = 1.0f;
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("blaster")
	
	property("bulletTemplate", parameters.bulletTemplate)
	property("sound", utils.resources.sounds.sound("towerofdefense.sounds.blasterbullet"))
	
	component(new DisablerComponent(new WeaponComponent("shooter"))) {
		propertyRef("enabled", "weaponEnabled")
		propertyRef("reloadTime", "reloadTime")
		propertyRef("position", "position")
		property("shouldFire", {entity.targetEntity!=null})
		
		property("trigger", utils.custom.triggers.closureTrigger { tower -> 
			def bulletTemplate = entity.bulletTemplate
			
			def bullet = bulletTemplate.get(tower)
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
			
			entity.sound.play(1.0f, 0.3f);
		})
	}
	
}
