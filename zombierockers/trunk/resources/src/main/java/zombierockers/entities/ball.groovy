package zombierockers.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.FollowPathComponent 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f 

builder.entity("ball-${Math.random()}") {
	
	tags("ball", "nofriction")
	
	property("position", parameters.position)
	property("color",parameters.color ?: utils.color(1,0,0))
	propertyRef("direction", "movement.velocity")
	property("radius",parameters.radius)
	property("path",parameters.path)
	
	
	property("maxVelocity", parameters.maxVelocity)
	
	component(new SuperMovementComponent("movement")){
		property("velocity",parameters.direction.copy().scale(entity.maxVelocity))
		propertyRef("maxVelocity", "maxVelocity")
		propertyRef("position", "position")
	}
	
	//	component(new FollowPathComponent("followpath")){
	//		propertyRef("path","path");
	//		property("pathindex", 0);
	//		propertyRef("force", "movement.force");
	//		propertyRef("position", "position");
	//	}
	
	component(new ComponentFromListOfClosures("followpath",[ { UpdateMessage message ->
		
	}]))
	
	
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("lineColor", utils.color(0,0,0,1))
		property("fillColor", parameters.color)
	}
	
	//	component(new ImageRenderableComponent("imagerenderer")) {
	//		property("image",utils.resources.image("ship"))
	//		propertyRef("color", "color")
	//		propertyRef("position", "position")
	//		propertyRef("direction", "direction")
	//		property("size", utils.vector(0.7f, 0.7f))
	//	}
	
	component(new GenericHitComponent("deflectBalls")){
		property("targetTag", "ball")
		property("predicate",{EntityPredicates.isNear(entity.position,(float) entity.radius*2)})
		property("trigger", utils.custom.triggers.closureTrigger  { data ->
			def forceValue = 700f
			def thisEntity = data.source
			def targets = data.targets
			
			if(thisEntity != entity)
				return 
			
			def position = thisEntity.position
			
			targets.each { target ->
				if(target == thisEntity)
					return 
				
				def targetPosition = target.position
				Vector2f distanceVector = targetPosition.copy().sub(position);
				Vector2f direction = distanceVector.copy().normalise();
				
				Vector2f generatedForce = direction.copy().scale((float)forceValue / distanceVector.lengthSquared());
				target."movement.force".add(generatedForce)
			}
		})
	}
	
	
	component(new ComponentFromListOfClosures("keepOnThePath",[ { UpdateMessage message ->
		def nextPathIndex = entity."followpath.pathindex"
		def nextPoint = entity.path.getPoint(nextPathIndex).copy()
		def previousPoint = entity.path.getPoint(nextPathIndex -1).copy()
		Line line = new Line(previousPoint,nextPoint)
		def newPosition = new Vector2f()
		line.getClosestPoint(entity.position,newPosition)
		entity.position = newPosition
		
	}
	]))
	
	component(new ComponentFromListOfClosures("forceToNextPoint",[ { UpdateMessage message ->
		def nextPathIndex = entity."followpath.pathindex"
		def nextPoint = entity.path.getPoint(nextPathIndex).copy()
		def directionToNextPoint = nextPoint.copy().sub(entity.position)
		if(directionToNextPoint.lengthSquared() == 0)
			return 
		
		directionToNextPoint.normalise()
		def forceToApply = directionToNextPoint.copy().scale(1)
		entity."movement.force".add(forceToApply)
	}
	]))
}

