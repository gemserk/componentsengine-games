package towerofdefense.entities;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics 
import org.newdawn.slick.geom.Line 
import org.newdawn.slick.geom.Vector2f 

import com.gemserk.games.towerofdefense.ComponentFromListOfClosures 
import com.gemserk.games.towerofdefense.GenericHitComponent;


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;


import com.gemserk.componentsengine.commons.components.SuperMovementComponent;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 

builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("radius", parameters.radius);
	
	component(new SuperMovementComponent("movement")){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", parameters.image)
		property("color", parameters.color)
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	component(new GenericHitComponent("bullethit")){
		property("targetTag", "critter")
		property("predicate",{EntityPredicates.isNear(entity.position, entity.radius)})
		property("trigger", utils.custom.triggers.genericMessage("hit") { 
			def source = message.source
			def damage = source.damage
			message.damage = damage;
			
			def targets = message.targets
			message.targets = [targets[0]]
		})
	}
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		if (message.getProperty("source").get() == entity)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	}
	
	
//	component(new ComponentFromListOfClosures("debug",[ {SlickRenderMessage message ->
//		
//		def debugVectors = [[vector:entity.direction.copy().scale(50f), color:Color.white]]
//		
//		
//		Graphics g = message.graphics
//		Vector2f start = entity.position.copy()
//		
//		debugVectors.each { vectorContainer ->
//			
//			def vector = vectorContainer.vector
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
//	}
//	
//	]))
//	
	
}
