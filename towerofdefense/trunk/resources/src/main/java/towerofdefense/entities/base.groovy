package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.games.towerofdefense.HitComponent;
import com.gemserk.games.towerofdefense.LabelComponent;

builder.entity {
	
	tags("base")
	
	property("position", parameters.position)
	property("radius", parameters.radius)
	property("direction", parameters.direction)
	
	component(new CircleRenderableComponent("circlerenderer")){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
	}
	
	component(new HitComponent("remover")) {
		property("targetTag", "critter")
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("trigger", utils.custom.triggers.genericMessage("hit") { source = message.source  })
	}

	component(new LabelComponent("alabel")){
		property("message", "{0}")
		propertyRef("position", "position")
		property("value", {entity.parent.lives})
	}

	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def sourceEntity = message.source
		
		if (entity != sourceEntity)
			return;
		
		message.targets.each { critter -> 
			messageQueue.enqueue(utils.genericMessage("critterReachBase"){ deadMessage ->
				deadMessage.critter = critter
			})
		}
	}
	
}
