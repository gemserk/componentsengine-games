package game.entities
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 


import org.newdawn.slick.geom.Vector2f 

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.componentsengine.timers.PeriodicTimer;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 


builder.entity("island-${Math.random()}") {
	
	def themeInfo = ThemeInfo.themeInfo(utils)
	
	tags("island")
	
	property("position",parameters.position)
	property("radius",50f)
	property("team",parameters.team)
	
	property("units",1)
	property("color",{themeInfo[(entity.team)].color})
	
	property("direction",utils.randomVector(utils.rectangle(-1,-1,2,2)))
	
	
	property("boatTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("game.entities.boat"), 
			utils.custom.genericprovider.provide{ data ->
				[
				position:data.position.copy(),
				team:data.team,
				destination:data.destination,
				color:data.color
				]
			}))
	
//	component(new CircleRenderableComponent("image")){
//		propertyRef("position","position")
//		propertyRef("radius","radius")
//		property("lineColor",utils.color(0,0,0,0))
//		property("fillColor",{entity.color.addToCopy(utils.color(0,0,0,-0.5f))})
//	}		
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("island1"))
		//property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction","direction")
		
	}
	
	component(new ImageRenderableComponent("imagerendererteam")) {
		property("image", utils.resources.image("island1-team"))
		propertyRef("color", "color")
		propertyRef("position", "position")
		propertyRef("direction","direction")
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
		def color = entity.color
		
		unitsToMove.times {
			def boat = boatTemplate.get([position:utils.randomVector(creationRectangle),team:team, destination: destination,color:color])
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(boat, entity.parent))
		}
		
		
	})
	
	
	
	component(new GenericHitComponent("boatArrivedDetector")){
		property("targetTag", "boat")
		property("predicate",{Predicates.and({entityToCheck -> entityToCheck.destination == entity} as Predicate,EntityPredicates.isNear(entity.position, entity.radius))})
		property("trigger", utils.custom.triggers.genericMessage("boatArrived") { 
			def source = message.source
			def targets = message.targets
			message.island = message.source 
			message.boats = message.targets
		})
	}
	
	component(new GenericHitComponent("deflectBoats")){
		property("targetTag", "boat")
		property("predicate",{Predicates.and({entityToCheck -> entityToCheck.destination != entity} as Predicate,EntityPredicates.isNear(entity.position,(float) entity.radius + 40))})
		property("trigger", utils.custom.triggers.closureTrigger  { data ->
			def island = data.source
			def boats = data.targets
			
			if(island != entity)
				return 
			
			def islandPosition = island.position
			
			boats.each { boat ->
				def boatPosition = boat.position
				Vector2f distanceVector = boatPosition.copy().sub(islandPosition);
				Vector2f direction = distanceVector.copy().normalise();
				
				Vector2f generatedForce = direction.copy().scale((float)3000 / distanceVector.lengthSquared());
				boat."movement.force".add(generatedForce)
			}
		})
	}
	
	component(utils.components.genericComponent(id:"boatArrivedHandler", messageId:"boatArrived"){ message ->
		if(message.island != entity)
			return
		
		message.boats.each { boat ->
			def team = entity.team
			def value = boat.team == team ? 1 : -1
			entity.units += value
			
			if(entity.units == 0)
				entity.team = boat.team //should throw ownerChanged???
		}
	})
}
