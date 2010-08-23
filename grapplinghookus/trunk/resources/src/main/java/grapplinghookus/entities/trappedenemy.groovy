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
	
	component(utils.components.genericComponent(id:"updateUntilItReachesTheBase", messageId:"update"){ message ->
		
		def grapplinghook = entity.grapplinghook
		
		if (grapplinghook.state == "reachingBase") {
			
			def endPosition = grapplinghook.endPosition
			
			entity.position = endPosition
			
			if (grapplinghook.position.distance(entity.position) > 2f)
				entity.direction = endPosition.copy().sub(grapplinghook.position).normalise().add(-90f)
		} 
		
	})
	
	component(new ImageRenderableComponent("imageRenderer")) {
		propertyRef("position", "position");
		propertyRef("direction", "direction");
		property("image", utils.resources.image("enemy01"));
		property("layer", 5);
		property("color", Color.white);
	}
	
}
