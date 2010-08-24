package dudethatsmybullet.entities

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;



import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicates 


builder.entity("bomb-${Math.random()}") {
	
	tags("bomb")
	
	property("position", parameters.position)
	property("radius",1f)
	property("maxRadius",1000f)
	
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
	
	component(utils.components.genericComponent(id:"process", messageId:"update"){ message ->
		def targets = entity.parent.getEntities(Predicates.and(EntityPredicates.withAllTags("turret"),EntityPredicates.isNear(entity.position,entity.radius)))
		
		targets.each { turret ->
			utils.custom.messageQueue.enqueue(utils.genericMessage("dead") { newMessage ->
				newMessage.target = turret
			})
		}
		
		if(entity.radius >= entity.maxRadius)
			utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})

}
