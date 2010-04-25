package game.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import org.newdawn.slick.geom.Vector2f 


builder.entity("whirpool-${Math.random()}") {
	
	tags("whirpool")
	
	property("position",parameters.position)
	property("radius",100f)
		
	component(new CircleRenderableComponent("image")){
		propertyRef("position","position")
		propertyRef("radius","radius")
		property("fillcolor",utils.color(0.5f,0.5f,1))
	}
	
	component(new GenericHitComponent("turnBoats")){
		property("targetTag", "boat")
		property("predicate",{EntityPredicates.isNear(entity.position,(float) entity.radius)})
		property("trigger", utils.custom.triggers.closureTrigger  { data ->
			def whirpool = data.source
			def boats = data.targets
			
			if(whirpool != entity)
				return 
			
			def position = whirpool.position
			
			boats.each { boat ->					
				def boatPosition = boat.position
				Vector2f distanceVector = boatPosition.copy().sub(position);
				Vector2f direction = distanceVector.copy().normalise();
			
				Vector2f perpendicularVector = utils.vector(-direction.y, direction.x);
				
				Vector2f generatedForce = perpendicularVector.copy().scale((float)3000 / distanceVector.lengthSquared());
				boat."movement.force".add(generatedForce)
			}
		})
	}
	
	component(new GenericHitComponent("attractBoats")){
		property("targetTag", "boat")
		property("predicate",{EntityPredicates.isNear(entity.position,(float) entity.radius)})
		property("trigger", utils.custom.triggers.closureTrigger  { data ->
			def whirpool = data.source
			def boats = data.targets
			
			if(whirpool != entity)
				return 
			
			def position = whirpool.position
			
			boats.each { boat ->					
				def boatPosition = boat.position
				Vector2f distanceVector = boatPosition.copy().sub(position);
				Vector2f direction = distanceVector.copy().normalise();
			
				
				Vector2f generatedForce = direction.copy().negate().scale((float)500 / distanceVector.lengthSquared());
				boat."movement.force".add(generatedForce)
				
				if(distanceVector.lengthSquared()<10*10){
					messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(boat))
				}
			}
		})
	}
	
}
