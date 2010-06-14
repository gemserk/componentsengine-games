package zombierockers.entities
import com.gemserk.games.zombierockers.PathTraversal;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 

builder.entity("spawner-${Math.random()}") {
	
	Random random = new Random()
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
	property("ballDefinitions", parameters.ballDefinitions)
	property("spawnQuantity", parameters.ballsQuantity)
	property("fired", false)
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ data ->
				[
				direction:utils.vector(0,1),
				radius:16.0f,
				definition:data.ballDefinition
				]
			}))
	
	property("segmentTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.segment"), 
			utils.custom.genericprovider.provide{ data ->
				[
				pathTraversal:data.pathTraversal,
				speed:0.04f,
				acceleratedSpeed:0.5f,
				accelerationStopPoint:1000f,
				accelerated:true,
				pathLength:data.pathLength
				]
			}))
	
	component(utils.components.genericComponent(id:"spawnHandler", messageId:["spawn"]){ message ->
		if(entity.fired)
			return
		
		entity.fired = true
		
		def template = entity.ballTemplate
		
		def ballDefinitions = entity.ballDefinitions
		
		def balls = []
		
		entity.spawnQuantity.times {
			def ballDefinition = getRandomItem(ballDefinitions)
			
			def parameters = [ballDefinition:ballDefinition]
			def ball = template.get(parameters)
			balls << ball
		}
		
		def path = entity.parent.path
		def pathTraversal = new PathTraversal(path,1,0)
		pathTraversal.distanceFromOrigin //so that it is calculated, and propagated when segment split
		def pathLength = new PathTraversal(path,path.points.size-1).distanceFromOrigin
		println pathLength
		
		def segment = entity.segmentTemplate.get([pathTraversal:pathTraversal, pathLength:pathLength])
		
		messageQueue.enqueue(utils.genericMessage("spawnedSegment"){ newMessage ->
			newMessage.balls = balls
			newMessage.segment = segment
		})
		
	})
	
}

