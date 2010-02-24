package towerofdefense.entities;

import com.gemserk.games.towerofdefense.components.WeaponComponent;

import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;

import com.gemserk.games.towerofdefense.components.FaceTargetComponent;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 

builder.entity("tower-${Math.random()}") {
	
	tags("tower","blaster")
	
	property("position", parameters.position)
	property("direction", parameters.direction)
	property("radius", parameters.radius)
	property("cost",parameters.cost)
	
	property("targetEntity", null)
	
	property("selected", false)
	
	component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		
		propertyRef("enabled", "selected")
	}

	component(new FaceTargetComponent("faceTarget")){
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		propertyRef("targetEntity", "targetEntity")
	}
	
	component(new SelectTargetWithinRangeComponent("selectTarget"))	{
		property("targetTag", "critter")
		propertyRef("targetEntity", "targetEntity")
		propertyRef("radius", "radius")
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("towerRenderer")) {
		property("image", utils.resources.image("towerofdefense.images.blastertower"))
		property("color", parameters.color)
		propertyRef("position", "position")
		property("direction", utils.vector(1f,0f))
	}
	
	component(new ImageRenderableComponent("cannonRenderer")) {
		property("image", utils.resources.image("towerofdefense.images.blastercannon"))
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}

	component(new WeaponComponent("shooter")){
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
	}
	
}
