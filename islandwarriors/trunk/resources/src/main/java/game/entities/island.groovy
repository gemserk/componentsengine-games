package game.entities
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 


import java.util.Set;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.componentsengine.timers.PeriodicTimer;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 


builder.entity("island-${Math.random()}") {
	
	tags("island")
	
	property("position",parameters.position)
	property("radius",50f)
	property("team",parameters.team)
	
	property("units",10)
	
	property("boatTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("game.entities.boat"), 
			utils.custom.genericprovider.provide{ data ->
				[
				position:data.position.copy(),
				team:data.team,
				destination:data.destination
				]
			}))
	
	component(new CircleRenderableComponent("image")){
		propertyRef("position","position")
		propertyRef("radius","radius")
	}
	
	component(new TimerComponent("generateUnitsTimer")){
		property("trigger",utils.custom.triggers.closureTrigger { entity.units += 1 })
		property("timer",new PeriodicTimer(1000))
	}
	
	
	component(new LabelComponent("units")){
		propertyRef("position","position")
		property("message","{0,number,integer}")
		property("value",{entity.units })
	}
	
	component(utils.components.genericComponent(id:"sendShipsHandler", messageId:"sendShips"){ message ->
		if(entity != message.origin)
			return
		
		
		def unitsToMove = (int)(entity.units /2)
		entity.units -=unitsToMove
		
		def boatTemplate = entity.boatTemplate
		def team = entity.team
		def destination = message.destination
		def position = entity.position
		def creationRectangle = utils.rectangle((float)position.x - 100, (float)position.y -100, 200,200)
		
		
		unitsToMove.times {
			def boat = boatTemplate.get([position:utils.randomVector(creationRectangle),team:team, destination: destination])
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(boat, entity.parent))
		}
		
		
	})
	
	
	
	component(new GenericHitComponent("boatArrivedDetector")){
		property("targetTag", "boat")
		property("predicate",{Predicates.and(EntityPredicates.isNear(entity.position, entity.radius),{entityToCheck -> entityToCheck.destination == entity} as Predicate)})
		property("trigger", utils.custom.triggers.genericMessage("boatArrived") { 
			def source = message.source
			def targets = message.targets
			message.island = message.source 
			message.boats = message.targets
		})
	}
	
	component(utils.components.genericComponent(id:"boatArrivedHandler", messageId:"boatArrived"){ message ->
		if(message.island != entity)
			return
			
		def team = entity.team
		message.boats.each { boat ->
			def value = boat.team == team ? 1 : -1
			entity.units += value
		}
	})
}
