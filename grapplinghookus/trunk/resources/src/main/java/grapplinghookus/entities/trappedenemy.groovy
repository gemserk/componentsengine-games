package grapplinghookus.entities

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import org.newdawn.slick.Color 


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.games.grapplinghookus.EntityBuilderFactory.ComponentProperties;

import org.newdawn.slick.Color;

builder.entity {
	
	tags("trappedenemy")
	
	property("grapplinghook", parameters.grapplinghook)
	property("base", parameters.base)
	property("enemy", parameters.enemy)
	
	property("position", utils.vector(0,0))
	property("direction", utils.vector(1,0))
	
	property("state", "goingToBase")
	
	component(utils.components.genericComponent(id:"updateUntilItReachesTheBase", messageId:"update"){ message ->
		
		if (entity.state != "goingToBase")
			return
			
		def grapplinghook = entity.grapplinghook
		def endPosition = grapplinghook.endPosition
		
		entity.position = endPosition
		entity.direction = endPosition.copy().sub(grapplinghook.position).normalise().add(-90f)
		
	})
	
	component(utils.components.genericComponent(id:"trappedEnemyBaseReachedHandler", messageId:"trappedEnemyBaseReached"){ message ->
		if (message.trappedEnemyId != entity.id) 
			return
			
		entity.state = "inBase"
		entity.grapplinghook = null
		
		entity.position = entity.base.position
//		entity.direction = utils.vector(-1,0)
	})
	
	component(new ImageRenderableComponent("imageRenderer")) {
			propertyRef("position", "position");
			propertyRef("direction", "direction");
			property("image", utils.resources.image("enemy01"));
			property("layer", 5);
			property("color", Color.white);
	}
	
}
