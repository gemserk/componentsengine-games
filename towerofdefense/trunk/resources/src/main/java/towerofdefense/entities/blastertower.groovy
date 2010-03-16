package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.DisablerComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.utils.AngleUtils 


builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.blastertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
	parameters.fireAngle = 1.0f;
	
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("blaster")
	
	component(new DisablerComponent(new WeaponComponent("shooter"))) {
		propertyRef("enabled", "weaponEnabled")
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
		property("entity", {entity.parent})
	}
	
}
