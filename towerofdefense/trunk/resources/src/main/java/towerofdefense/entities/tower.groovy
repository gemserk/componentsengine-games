package towerofdefense.entities;


import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;

import com.gemserk.games.towerofdefense.components.FaceTargetComponent;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 

builder.entity {
	
	tags("tower")
	
	property("position", parameters.position)
	property("direction", parameters.direction)
	property("radius", parameters.radius)
	// property("cost", parameters.cost)
	property("turnRate", parameters.turnRate)
	
	property("targetEntity", null)
	
	property("selected", false)
	
	component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		propertyRef("enabled", "selected")
	}
	
	component(new DisablerComponent(new ImageRenderableComponent("selectedAuraRenderer"))){
		property("image", utils.resources.image("towerofdefense.images.blasterbullet"))
		property("color", utils.color(1f, 1f, 1f, 1.0f))
		property("direction", utils.vector(1f,0f))
		propertyRef("position", "position")
		propertyRef("enabled", "selected")
	}
	
	component(new FaceTargetComponent("faceTarget")){
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		propertyRef("targetEntity", "targetEntity")
		propertyRef("turnRate", "turnRate")
	}
	
	component(new SelectTargetWithinRangeComponent("selectTarget"))	{
		property("targetTag", "critter")
		propertyRef("targetEntity", "targetEntity")
		propertyRef("radius", "radius")
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("towerRenderer")) {
		property("image", parameters.towerImage)
		property("color", parameters.color)
		propertyRef("position", "position")
		property("direction", utils.vector(1f,0f))
		property("size",utils.vector(0.75f,0.75f))
	}
	
	component(new ImageRenderableComponent("cannonRenderer")) {
		property("image", parameters.cannonImage)
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		property("size",utils.vector(0.75f,0.75f))
	}
	
}
