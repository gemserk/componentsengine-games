package towerofdefense.entities;

import com.gemserk.games.towerofdefense.ComponentFromListOfClosures 
import com.gemserk.games.towerofdefense.components.AngleUtils;
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.games.towerofdefense.components.WeaponComponent;

builder.entity("tower-${Math.random()}") {
	
	parameters.towerImage=utils.resources.image("towerofdefense.images.blastertower")
	parameters.cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
	
	parent("towerofdefense.entities.tower", parameters)
	
	tags("blaster")
	
	property("weaponEnabled", false)
	property("weaponAngle", 1.0f)
	
	component(new DisablerComponent(new WeaponComponent("shooter"))) {
		propertyRef("enabled", "weaponEnabled")
		property("template", parameters.template)
		property("reloadTime", parameters.reloadTime)
		property("instanceParameters", parameters.instanceParameters)
		propertyRef("position", "position")
		propertyRef("targetEntity", "targetEntity")
		property("entity", {entity.parent
		})
		property("fireAngle", 180.0f)
	}
	
	component(new ComponentFromListOfClosures("weaponEnabler", [{ UpdateMessage message ->
		if (entity.targetEntity == null)
			return
	
		def position = entity.position
		def direction = entity.direction
		
		def targetPosition = entity.targetEntity.position
		
		def desiredDirection = targetPosition.copy().sub(position)
		
		double angleDifference = new AngleUtils().angleDifference(direction.getTheta(), desiredDirection.getTheta());
		if (Math.abs(angleDifference) > entity.weaponAngle) {
			entity.weaponEnabled=false;
		} else {
			entity.weaponEnabled=true;
		}
	}]))
	
}
