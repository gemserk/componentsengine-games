package towerofdefense.entities;

import com.gemserk.games.towerofdefense.commoncomponents.WeaponComponent 


builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.missiletower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.missilecannon")
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("missiletower")
	
	component(new WeaponComponent("shooter")){
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
		property("entity", {entity.parent})
	}
}
