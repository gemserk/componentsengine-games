package game.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 


builder.entity("boat-${Math.random()}") {
	
	tags("boat")
	
	property("position",parameters.position)
	property("radius",5f)
	property("team",parameters.team)
	property("destination",parameters.destination)
	
	
	component(new CircleRenderableComponent("image")){
		propertyRef("position","position")
		propertyRef("radius","radius")
	}
	
	component(new SuperMovementComponent("movement")){
		property("velocity", utils.vector(0,0))
		property("maxVelocity", (float)(100/1000))
		propertyRef("position", "position")
	}
	
	component(new ComponentFromListOfClosures("steeringfollow",[ {UpdateMessage message ->
		def target = entity.destination
		
		if(target == null)
			return
		
		def direction = target.position.copy().sub(entity.position).normalise()
		
		entity."movement.force".add(direction.scale(1))
	}
	]))
	
	
	component(utils.components.genericComponent(id:"boatArrivedHandler", messageId:"boatArrived"){ message ->
		if(!message.boats.contains(entity))
			return
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
}
