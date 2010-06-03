package zombierockers.entities
import com.gemserk.games.zombierockers.PathTraversal;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 

builder.entity("spawner-${Math.random()}") {
	
	Random random = new Random()
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
	property("spawnQuantity", parameters.ballsQuantity)
	property("fired", false)
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ data ->
				[
				direction:utils.vector(0,1),
				radius:16.0f,
				color:data.color
				]
			}))
	
	property("segmentTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.segment"), 
			utils.custom.genericprovider.provide{ data ->
				[
				path:data.path,
				speed:0.04f,
				acceleratedSpeed:0.5f,
				accelerationStopPoint:new PathTraversal(data.path, 6, 0f),
				accelerated:true
				]
			}))
	
	component(utils.components.genericComponent(id:"spawnHandler", messageId:["spawn"]){ message ->
		if(entity.fired)
			return
		
		entity.fired = true
		
		def template = entity.ballTemplate
		
		def colors = [utils.color(1,0,0,1), utils.color(0,1,0,1), utils.color(0,0,1,1)]
		
		def balls = []
		
		entity.spawnQuantity.times {
			def color = getRandomItem(colors)
			
			def parameters = [color:color]
			def ball = template.get(parameters)
			balls << ball
		}
		
		def segment = entity.segmentTemplate.get([path:entity.parent.path])
		
		messageQueue.enqueue(utils.genericMessage("spawnedSegment"){ newMessage ->
			newMessage.balls = balls
			newMessage.segment = segment
		})
		
	})
	
}

