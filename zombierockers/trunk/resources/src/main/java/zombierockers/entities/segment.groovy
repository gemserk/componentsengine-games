package zombierockers.entities

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.games.zombierockers.PathTraversal;

builder.entity("segment-${Math.random()}") {
	
	tags("segment")
	
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
	
	
	
	
	component(utils.components.genericComponent(id:"bulletHitHandler", messageId:["bulletHit"]){ message ->
		def target = message.targets[0]
		def ballIndex = entity.balls.indexOf(target)
		
		if(ballIndex == -1)
			return
			
		def ball = message.source.ball
		ball.position = target.position
				
		messageQueue.enqueue(utils.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity
			newMessage.ball = ball
			newMessage.index = ballIndex
		})
		entity.parent.ballsQuantity++
		
		
		
		println "Chocaron segmento - $entity.id"
	})
}

