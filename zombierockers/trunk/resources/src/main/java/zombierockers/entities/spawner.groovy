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
	
	property("spawnTimer",new PeriodicTimer(4000))
	property("spawnQuantity",10)
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ spawner ->
				[
				position:spawner.position.copy(),
				path:spawner.path,
				direction:utils.vector(0,1),
				radius:10.0f,
				maxVelocity:0.07f,
				color:spawner.color
				]
			}))
	
	
	component(new TimerComponent("spawnerTimer")){
		property("trigger",utils.custom.triggers.genericMessage("spawnBall") {message.source = entity })
		propertyRef("timer","spawnTimer")
	}
	
	component(utils.components.genericComponent(id:"spawnBallHandler", messageId:["spawnBall"]){ message ->
		def template = entity.ballTemplate
		
		def colors = [utils.color(1,0,0,1), utils.color(0,1,0,1), utils.color(0,0,1,1)]
		
		entity.spawnQuantity.times {
			def color = getRandomItem(colors)
			
			def parameters = [position:entity.position.copy(),path:entity.parent.path,color:color]
			def ball = template.get(parameters)
			messageQueue.enqueue(utils.genericMessage("spawnedBall"){newMessage -> newMessage.ball = ball})
		}
	})
	
}

