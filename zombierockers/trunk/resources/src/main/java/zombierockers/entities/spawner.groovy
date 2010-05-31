package zombierockers.entities

import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.timers.PeriodicTimer 

builder.entity("segment-${Math.random()}") {
	
	Random random = new Random()
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
	property("position", utils.vector(-20,200))
	
	//property("spawnTimer",new Count(4000))
	property("spawnQuantity",200)
	property("fired",false)
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ spawner ->
				[
				position:spawner.position.copy(),
				direction:utils.vector(0,1),
				radius:16.0f,
				color:spawner.color
				]
			}))
	
	property("segmentTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.segment"), 
			utils.custom.genericprovider.provide{ data ->
				[
				path:data.path,
				speed:0.04f
				]
			}))
	
	
	//	component(new TimerComponent("spawnerTimer")){
	//		property("trigger",utils.custom.triggers.genericMessage("spawnBall") {message.source = entity })
	//		propertyRef("timer","spawnTimer")
	//	}
	
	component(utils.components.genericComponent(id:"spawnHandler", messageId:["spawn"]){ message ->
		if(entity.fired)
			return
			
		entity.fired = true
		
		def template = entity.ballTemplate
		
		def colors = [utils.color(1,0,0,1), utils.color(0,1,0,1), utils.color(0,0,1,1)]
		
		def balls = []
		
		entity.spawnQuantity.times {
			def color = getRandomItem(colors)
			
			def parameters = [position:entity.position.copy(),path:entity.parent.path,color:color]
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

