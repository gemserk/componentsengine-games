package grapplinghookus.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 

builder.entity {
	
	tags("spawner")
	
	property("timer", parameters.maxTime)
	property("minTime", parameters.minTime)
	property("maxTime", parameters.maxTime)
	
	// timers are decreased 10% while they are > 100ms, each 5secs
	property("accelerateTimersTime", 10000)
	property("accelerateCurrentTime", 0)
	
	component(utils.components.genericComponent(id:"accelerateTimers", messageId:"update"){ message ->
		
		entity.accelerateCurrentTime = (int) entity.accelerateCurrentTime + message.delta
		
		if (entity.accelerateCurrentTime > entity.accelerateTimersTime) {
			
			entity.minTime = (int) (entity.minTime - entity.minTime * 0.1)
			entity.maxTime = (int) (entity.maxTime - entity.maxTime * 0.1)
			
			if (entity.minTime < 100)
				entity.minTime = 100
			
			if (entity.maxTime < 100)
				entity.maxTime = 100
			
			entity.accelerateCurrentTime = 0
		}
		
	})
	
	component(utils.components.genericComponent(id:"updateTimer", messageId:"update"){ message ->
		def timer = entity.timer
		
		timer -= message.delta
		
		if (timer <= 0) {
			utils.custom.messageQueue.enqueue(utils.genericMessage("spawnEnemy"){ newMessage ->
				newMessage.spawnerId = entity.id
			})
			timer = utils.random.nextInt(entity.maxTime) + entity.minTime
		}
		
		entity.timer = timer
	})
	
	def random = utils.random
	
	component(utils.components.genericComponent(id:"spawnEnemy", messageId:"spawnEnemy"){ message ->
		
		if (entity.id != message.spawnerId)
			return
		
		def position = utils.vector((float) (20f + utils.random.nextFloat() * 600f), -20f)
		def moveDirection = utils.vector(0,1)
		def speed = (float) (0.01f + utils.random.nextFloat() * 0.05f)
		
		def enemyFactory = utils.custom.enemyFactory
		
		def enemy = enemyFactory.enemy("enemy-${Math.random()}".toString(), [
				position:position,
				moveDirection:moveDirection, 
				speed:speed])
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(enemy,entity.parent))
	})
}
