package towerofdefense.entities;

import com.gemserk.games.towerofdefense.HitComponent;


builder.entity {
	
	tags("base")
	
	property("position", parameters.position)
	property("radius", parameters.radius)
	property("direction", parameters.direction)
	
	component("circlerenderer"){
		property("lineColor", parameters.lineColor)
		property("fillColor", parameters.fillColor)
		propertyRef("position", "position")
		propertyRef("radius", "radius")
	}
	
	
	component(new HitComponent("remover")) {
		property("targetTag", "critter")
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("messageBuilder", utils.custom.messageBuilderFactory.messageBuilder("hit") {   def source = message.source  })
	}
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def entity = message.entity
		def sourceEntity = message.source
		
		if (entity != sourceEntity)
			return;
		
		message.targets.each { critter -> 
			messageQueue.enqueue(utils.genericMessage("critterReachBase"){ deadMessage ->
				deadMessage.critter = critter
			})
		}
		
		println "hit base"
		
	}
	
}
