package jylonwars.entities

import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.utils.AngleUtils;

builder.entity("critter-${Math.random()}") {
	
	parent("jylonwars.entities.critter",parameters)
	tags("avoidercritter")
	
	property("rotationValue",0f)
	property("debugVectors",[])
	
	component(new IncrementValueComponent("rotator")) {
		propertyRef("value", "rotationValue")
		property("maxValue", 360f)
		property("increment", 0.2f)
	}
	
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("avoidercritter"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", {utils.vector(1,0).add(entity.rotationValue)
		})
		property("size", utils.vector(1f, 1f))
	}
	
	
	component(new ComponentFromListOfClosures("steeringFollow",[ {UpdateMessage message ->
		def target = entity.parent.getEntities(EntityPredicates.withAllTags("ship")).first();
		
		if(target == null)
		return
		
		def direction = target.position.copy().sub(entity.position).normalise()
		
		entity."movement.force".add(direction.scale(3))
	}
	]))
	
	component(new ComponentFromListOfClosures("steeringAvoidDirection",[ {UpdateMessage message ->
		def ship = entity.parent.getEntities(EntityPredicates.withAllTags("ship")).first();
		
		def debugVectors = []
		
		if(ship == null)
		return
		
		def position = entity.position
		def shipPosition = ship.position.copy()
		def shipDirection = ship.direction.copy().normalise()
		def followDirection = position.copy().sub(shipPosition).normalise()
		def angle = Math.abs(new AngleUtils().minimumDifference(shipDirection.getTheta(), followDirection.getTheta()))
		if(angle > 45)
			return
			
		
		
		
		Line line = new Line(shipPosition,shipPosition.copy().add(shipDirection.copy().scale(10000)))
		println shipDirection
		println line
		debugVectors << [start:shipPosition.copy(), vector:shipPosition.copy().add(shipDirection.copy().scale(10000)), color:Color.green]
		
		
		def closestPoint = new Vector2f();
		
		line.getClosestPoint(position,closestPoint)
		
		def avoidDirection = position.copy().sub(closestPoint)
		debugVectors << [start:position.copy(), vector:avoidDirection.copy(), color:Color.yellow]
		avoidDirection.normalise()
		debugVectors << [start:position.copy(), vector:followDirection.copy().normalise().scale(50f), color:Color.red]
		
		
		def avoidStrength = (float)(10 - 10*angle/45)
		
		def avoidForce = avoidDirection.scale(avoidStrength)
		
		entity."movement.force".add(avoidForce)
		
		debugVectors << [start:position.copy(), vector:avoidForce.copy().normalise().scale(50f), color:Color.blue]
		
		entity.debugVectors = debugVectors
	}
	]))
	
	
//	component(new ComponentFromListOfClosures("debugVectors",[{SlickRenderMessage message ->
//		def debugVectors = entity.debugVectors
//		if( debugVectors == null)
//		return
//		
//		
//		Graphics g = message.graphics
//		
//		debugVectors.each { vectorContainer ->
//			
//			def vector = vectorContainer.vector
//			Vector2f start = vectorContainer.start
//			def color = vectorContainer.color ?: Color.white
//			
//			Vector2f segment = vector.copy()
//			
//			Line line = new Line(start.x,start.y,segment.x,segment.y,true)
//			def origColor = g.getColor()
//			g.setColor(color)
//			g.draw(line)
//			g.setColor(origColor)
//		}
//		
//		debugVectors.clear()
//	}]))
	

}

