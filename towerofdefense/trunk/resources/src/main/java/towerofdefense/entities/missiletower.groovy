package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.commons.components.WeaponComponent 

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.missiletower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.missilecannon")
	parameters.fireAngle = 360.0f;
	
	parent("towerofdefense.entities.tower", parameters)
	property("damage",parameters.damage)
	tags("missiletower")
	
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
