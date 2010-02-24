package towerofdefense.entities;

import com.gemserk.games.towerofdefense.components.WeaponComponent;

import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;

import com.gemserk.games.towerofdefense.components.FaceTargetComponent;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 

builder.entity("tower-${Math.random()}") {

	parameters.towerImage=utils.resources.image("towerofdefense.images.blastertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
	
	parent("towerofdefense.entities.tower")
	
	tags("blaster")
	
	component(new WeaponComponent("shooter")){
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
	}
	
}
