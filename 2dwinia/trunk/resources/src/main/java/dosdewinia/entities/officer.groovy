package dosdewinia.entities
import com.gemserk.componentsengine.commons.components.DisablerComponent;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates 


import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.games.dosdewinia.Target;


builder.entity("officer-${Math.random()}") {
	
	def random = new Random();
	
	tags("officer","selectable")
	
	property("position", parameters.position)
	property("destinationPoint",parameters.destinationPoint)
	property("destinationPointZoneId",parameters.destinationPointZoneId)
	property("radius",20)
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("darwinian"))
		property("color", utils.color(0.5f,1,1,1))
		propertyRef("position", "position")
		property("direction", utils.vector(1,0))
		property("layer", 2)
	}
	
	
	
	
	component(new DisablerComponent(new GenericHitComponent("bullethitComponent"))){
		property("targetTag", "darwinian")
		property("predicate",{Predicates.and(/*{darwinian -> !darwinian.outsideOfBounds} as Predicate,*/EntityPredicates.isNear(entity.position, (float)100))})
		property("trigger", utils.custom.triggers.closureTrigger { data ->
			def source = data.source
			def targets = data.targets
			targets.each { target ->
				if(target.state != "goTowardsTarget"){
					//log.info("BOUND: $target.outsideOfBounds")
					if(random.nextFloat() < 0.001f){
						target.targetPosition = null
						target.target = new Target(source.destinationPoint,100,20,source.destinationPointZoneId)
						target.state = "goTowardsTarget"
					}
				}
			}
		})
		property("enabled",{entity.destinationPoint != null})
	}
	
	
	
}