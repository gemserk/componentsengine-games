package grapplinghookus.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 

builder.entity {
	
	tags("spawner")
	
	property("timer", parameters.maxTime)
	property("minTime", parameters.minTime)
	property("maxTime", parameters.maxTime)
	
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
