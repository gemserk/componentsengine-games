package zombierockers.entities

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.games.zombierockers.PathTraversal;

builder.entity("segment-${Math.random()}") {
	
	tags("segment")
	
	property("path",parameters.path)
	property("pathTraversal", new PathTraversal(parameters.path,1,0))
	property("speed",0.07f)
	property("balls",new LinkedList())
	
	
	
	component(utils.components.genericComponent(id:"addNewBallHandler", messageId:["addNewBall"]){ message ->
		if(message.segment != entity)
			return
			
		def insertionPoint = message.index ?: 0
		entity.balls.add(insertionPoint,message.ball)
		
		if(insertionPoint > 0)
			entity.pathTraversal = entity.pathTraversal.add((float)message.ball.radius * 2)
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(message.ball, entity))
	})
	
	component(utils.custom.components.closureComponent("advanceHandler"){ UpdateMessage message ->
		def distance = (float)(entity.speed * message.delta)
		def pathTraversal = entity.pathTraversal.add(distance)
		entity.pathTraversal = pathTraversal
		def messageQueue = utils.custom.messageQueue
		entity.balls.reverseEach { ball ->
			//			messageQueue.enqueue(utils.genericMessage("moveBall"){newMessage ->
			//				newMessage.ball = ball
			//				newMessage.position = pathTraversal.getPosition()
			//			})
			ball.position = pathTraversal.position
			pathTraversal = pathTraversal.add((float)-ball.radius * 2)
		}
	})
	
	Random random = new Random()
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
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
	
	
	
	component(utils.components.genericComponent(id:"bulletHitHandler", messageId:["bulletHit"]){ message ->
		def target = message.targets[0]
		def ballIndex = entity.balls.indexOf(target)
		
		if(ballIndex == -1)
			return
		
		def template = entity.ballTemplate
		
		def colors = [utils.color(1,0,0,1), utils.color(0,1,0,1), utils.color(0,0,1,1)]
		
		
		
		
		def color = getRandomItem(colors)
		
		def parameters = [position:message.source.position.copy(),path:entity.path,color:color]
		def ball = template.get(parameters)
		
		messageQueue.enqueue(utils.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity
			newMessage.ball = ball
			newMessage.index = ballIndex
		})
		entity.parent.ballsQuantity++
		
		
		
		println "Chocaron segmento - $entity.id"
	})
}

