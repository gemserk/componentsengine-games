package towerofdefense.entities;

import com.gemserk.games.towerofdefense.components.WeaponComponent;




import com.gemserk.componentsengine.components.Component 

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.blastertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.lasercannon")
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("missiletower")
	
	component(new Component("faceTarget"){
			})
	
	
	component(new WeaponComponent("shooter")){
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
		property("entity", {entity.parent})
	}
}
