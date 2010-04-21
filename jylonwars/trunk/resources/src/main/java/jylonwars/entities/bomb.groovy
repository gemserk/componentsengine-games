package jylonwars.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;



import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicates 


builder.entity("bomb-${Math.random()}") {
	
	tags("bomb")
	
	property("position", parameters.position)
	property("radius",1f)
	property("maxRadius",500f)
	
	component(new IncrementValueComponent("radiusIncrementor")) {
		propertyRef("value", "radius")
		propertyRef("maxValue","maxRadius")
		property("increment", 1f)
		property("loop",false)
	}
	
//	component(new CircleRenderableComponent("bomb")){
//		propertyRef("position","position")
//		propertyRef("radius","radius")
//		property("lineColor",utils.color(0,0,0,1))
//	}
	
	component(new ComponentFromListOfClosures("process",[ {UpdateMessage message ->
		def targets = entity.parent.getEntities(Predicates.and(EntityPredicates.withAllTags("critter"),EntityPredicates.isNear(entity.position,entity.radius)))
		
		targets.each { critter ->
//			utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(critter))
			utils.custom.messageQueue.enqueue(utils.genericMessage("critterdead") { newMessage ->
				newMessage.critter = critter
			})
		}
		
		if(entity.radius >= entity.maxRadius)
			utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	}
	]))
}
