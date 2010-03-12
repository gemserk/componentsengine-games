package towerofdefense.entities;
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.utils.AngleUtils 

import com.gemserk.componentsengine.commons.components.*;

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
	
	property("weaponEnabled", false)
	property("weaponAngle", parameters.fireAngle)
	
	component(new ComponentFromListOfClosures("weaponEnabler", [{ UpdateMessage message ->
		if (entity.targetEntity == null)
			return
		
		def position = entity.position
		def direction = entity.direction
		
		def targetPosition = entity.targetEntity.position
		
		def desiredDirection = targetPosition.copy().sub(position)
		
		double angleDifference = new AngleUtils().minimumDifference(direction.getTheta(), desiredDirection.getTheta());
		if (Math.abs(angleDifference) > entity.weaponAngle) {
			entity.weaponEnabled=false;
		} else {
			entity.weaponEnabled=true;
		}
	}]))
	
	// Render components
	
	component(new ImageRenderableComponent("towerRenderer")) {
		property("image", parameters.towerImage)
		property("color", parameters.color)
		propertyRef("position", "position")
		property("direction", utils.vector(1f,0f))
		property("size",utils.vector(0.85f,0.85f))
	}
	
	component(new ImageRenderableComponent("cannonRenderer")) {
		property("image", parameters.cannonImage)
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
		property("size",utils.vector(0.85f,0.85f))
	}
	
}
