package dosdewinia.entities

import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates 

builder.entity("officer-${Math.random()}") {
	
	
	tags("officer")
	
	property("position", parameters.position)
	property("direction",parameters.direction)
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("darwinian"))
		property("color", utils.color(0.5f,1,1,1))
		propertyRef("position", "position")
		property("direction", utils.vector(1,0))
		property("layer", 2)
	}
	
	
	
	
	component(new GenericHitComponent("bullethitComponent")){
		property("targetTag", "darwinian")
		property("predicate",{EntityPredicates.isNear(entity.position, (float)100)})
		property("trigger", utils.custom.triggers.closureTrigger { data ->
			def source = data.source
			def targets = data.targets
			targets.each { target ->
				target."movement.force".add(source.direction.copy().scale(10))
			}
		})
	}
	
	
	
}