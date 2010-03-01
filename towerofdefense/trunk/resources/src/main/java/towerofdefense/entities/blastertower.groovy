package towerofdefense.entities;

import com.gemserk.games.towerofdefense.components.WeaponComponent;

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.blastertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("blaster")
	
	component(new WeaponComponent("shooter")){
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
	}
	
}
