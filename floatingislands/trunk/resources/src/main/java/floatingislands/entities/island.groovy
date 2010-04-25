package floatingislands.entities;

import org.newdawn.slick.geom.Vector2f 

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.messages.UpdateMessage;

builder.entity {
	
	tags("island")
	
	property("image", parameters.islandImage)
	property("position", parameters.position)
	property("direction", utils.vector(1,0))
	property("bounds", parameters.bounds)
	property("startPosition", parameters.startPosition)
	
	component(new ImageRenderableComponent("imageRender")) {
		propertyRef("image", "image")
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	property("moveTime", 5000)
	
	def movePositions = [parameters.position.copy(), parameters.position.copy().add(new Vector2f(7f,4f)), 
	         			parameters.position.copy().add(new Vector2f(9f,-6f)), 
	        			parameters.position.copy().add(new Vector2f(-8f,-5f)), 
	        			parameters.position.copy().add(new Vector2f(-6f,7f))]
	
	property("positions", movePositions)
	
	def nextPositionIndex = new Random().nextInt() % movePositions.size()
	
	property("nextPositionIndex", nextPositionIndex)
	property("velocity", movePositions[nextPositionIndex].copy().sub(parameters.position).scale((float)(1/5000)) )
	
	component(new ComponentFromListOfClosures("levitatorIslandComponent", [{UpdateMessage message -> 
		
		def nextPosition = entity.positions[entity.nextPositionIndex]
		def position = entity.position
		
		if (position.distance(nextPosition)<1.0f) {
			entity.nextPositionIndex++
			
			if (entity.nextPositionIndex >= entity.positions.size())
				entity.nextPositionIndex = 0
			
			nextPosition = entity.positions[entity.nextPositionIndex]
			entity.velocity = nextPosition.copy().sub(position).scale((float)(1/entity.moveTime)) 
			
			return
		}
		
		entity.position.add(entity.velocity.copy().scale(message.delta))
		
		
	}]))
	
	// for debug
	//	component(new RectangleRendererComponent("boundsRenderer")) {
	//		propertyRef("position", "position")
	//		propertyRef("rectangle", "bounds")
	//		property("lineColor", utils.color(1,1,1,1))
	//	}
	
}
