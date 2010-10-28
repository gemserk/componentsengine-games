package zombierockers.entities
import com.gemserk.games.zombierockers.PathTraversal;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 

builder.entity("spawner-${Math.random()}") {
	
	Random random = new Random()
	
	def getRandomItemFromMap = {def items ->
		def keys = items.collect { it.key }
		def randomKey = keys[random.nextInt(keys.size())]
		return items[randomKey]
	}
	
	property("ballDefinitions", parameters.ballDefinitions)
	property("subPathDefinitions", parameters.subPathDefinitions)
	
	property("spawnQuantity", parameters.ballsQuantity)
	property("fired", false)
	
	property("pathProperties", parameters.pathProperties)
	
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ data ->
				[
				direction:utils.slick.vector(0,1),
				radius:16.0f,
				definition:data.ballDefinition,
				collisionMap:data.collisionMap,
				subPathDefinitions:data.subPathDefinitions
				]
			}))
	
	property("segmentTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.segment"), 
			utils.custom.genericprovider.provide{ data ->
				[
				pathTraversal:data.pathTraversal,
				acceleratedSpeed:data.acceleratedSpeed,
				accelerationStopPoint:data.accelerationStopPoint,
				minSpeedFactor:data.minSpeedFactor,
				maxSpeed:data.maxSpeed,
				speedWhenReachBase:data.speedWhenReachBase,
				speed:0.04f,
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
			def ballDefinition = getRandomItemFromMap(ballDefinitions)
			
			def parameters = [ballDefinition:ballDefinition,subPathDefinitions:entity.subPathDefinitions]
			def ball = template.get(parameters)
			balls << ball
		}
		
		def path = entity.parent.path
		def pathTraversal = new PathTraversal(path,0,0)
		pathTraversal.distanceFromOrigin //so that it is calculated, and propagated when segment split
		def pathLength = new PathTraversal(path,path.points.size-1).distanceFromOrigin
		println pathLength
		
		def properties = [pathTraversal:pathTraversal, pathLength:pathLength]
		properties.putAll(entity.pathProperties)
		def segment = entity.segmentTemplate.get(properties)
		
		messageQueue.enqueue(utils.genericMessage("spawnedSegment"){ newMessage ->
			newMessage.balls = balls
			newMessage.segment = segment
		})
		
	})
	
}

