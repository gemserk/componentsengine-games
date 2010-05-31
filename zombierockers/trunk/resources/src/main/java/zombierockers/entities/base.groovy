package zombierockers.entities
import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.google.common.base.Predicate 


import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate;



builder.entity("base") {
	
	tags("base")
	
	property("position",parameters.position)
	property("radius",parameters.radius)
	
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("lineColor", utils.color(0,0,0,1))
		property("fillColor", utils.color(0,0,0,1))
	}
	
	
	
	component(new GenericHitComponent("segmentHit")){
		property("targetTag", "segment")
		property("predicate",{segment ->
			segment.pathTraversal.position.distance(entity.position) < entity.radius
		} as Predicate)
		property("trigger", utils.custom.triggers.genericMessage("segmentReachedBase") { 
			def source = message.source
			def targets = message.targets
		})
	}
	
	component(utils.components.genericComponent(id:"segmentReachedBaseHandler", messageId:["segmentReachedBase"]){ message ->
		if(message.source != entity)
			return
		
		entity.root.getEntities(EntityPredicates.withAllTags("segment")).each{ segment ->
			segment.speed = 0.5f
		}
		
		def segment = message.targets[0]
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("segmentRemoveHead"){newMessage ->
			newMessage.segment = segment
		})
	})
}