package jylonwars.entities

import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 

builder.entity("critter-${Math.random()}") {
	
	parent("jylonwars.entities.critter",parameters)
	tags("wanderercritter")
	
	property("target",entity.position)
	
	property("rotationValue",0f)
	
	component(new IncrementValueComponent("rotator")) {
		propertyRef("value", "rotationValue")
		property("maxValue", 360f)
		property("increment", 0.2f)
	}
	
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("wanderercritter"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", {utils.vector(1,0).add(entity.rotationValue)})
		property("size", utils.vector(1f, 1f))
	}
	
	
	
	component(new ComponentFromListOfClosures("steering",[ {UpdateMessage message ->
		def target = entity.target
		
		def direction = target.copy().sub(entity.position).normalise()
		
		entity."movement.force".add(direction.scale(1))
	}
	]))
	
	
	component(new ComponentFromListOfClosures("newtarget",[ {UpdateMessage message ->
		def target = entity.target
		
		if(target.distance(entity.position)< 5){
			entity.target = utils.randomVector(entity.bounds)
		}
		
	}
	]))
}

