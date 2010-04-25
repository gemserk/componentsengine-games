package game.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import org.newdawn.slick.geom.Vector2f 


builder.entity("boat-${Math.random()}") {
	
	tags("boat")
	
	property("position",parameters.position)
	property("radius",10f)
	property("team",parameters.team)
	property("destination",parameters.destination)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("boat"))
		//property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction","movement.velocity")
		
	}
	
	component(new ImageRenderableComponent("imagerenderer-team")) {
		property("image", utils.resources.image("boat-team"))
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction","movement.velocity")
		
	}
	
//	component(new CircleRenderableComponent("image")){
//		propertyRef("position","position")
//		propertyRef("radius","radius")
//		property("lineColor",parameters.color)
//	}
	
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
	
	
	component(new GenericHitComponent("deflectBoats")){
		property("targetTag", "boat")
		property("predicate",{EntityPredicates.isNear(entity.position,(float) entity.radius + 10)})
		property("trigger", utils.custom.triggers.closureTrigger  { data ->
			def thisBoat = data.source
			def boats = data.targets
			
			if(thisBoat != entity)
				return 
			
			def position = thisBoat.position
			
			boats.each { boat ->
				if(boat == entity)
					return 
				
				def boatPosition = boat.position
				Vector2f distanceVector = boatPosition.copy().sub(position);
				Vector2f direction = distanceVector.copy().normalise();
				
				Vector2f generatedForce = direction.copy().scale((float)300 / distanceVector.lengthSquared());
				boat."movement.force".add(generatedForce)
			}
		})
	}
	
}
