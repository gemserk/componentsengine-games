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
		def collisionBall = message.targets[0]
		def ballIndex = entity.balls.indexOf(collisionBall)
		
		if(ballIndex == -1)
			return
		
		
		
		def tangent = entity.pathTraversal.add((float)-collisionBall.radius * 2 * ballIndex).tangent
		
		def collisionBallPosition = collisionBall.position
		def bulletPosition = message.source.position
		
		def differenceVector = bulletPosition.copy().sub(collisionBallPosition)
		
		def proyection = tangent.dot(differenceVector)
		
		if(proyection > 0)
			ballIndex++
		
		def ball = message.source.ball
		ball.position = collisionBall.position
				
		messageQueue.enqueue(utils.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity
			newMessage.ball = ball
			newMessage.index = ballIndex
		})
		
		messageQueue.enqueue(utils.genericMessage("checkBallSeries"){newMessage -> 
			newMessage.segment = entity
			newMessage.index = ballIndex
		})
		entity.parent.ballsQuantity++
		
		
		
		println "Chocaron segmento - $entity.id"
	})
	
	component(utils.components.genericComponent(id:"checkBallSeriesHandler", messageId:["checkBallSeries"]){ message ->
		if(message.segment != entity)
			return
			
		def forwardIterator = entity.balls.listIterator(message.index)
		def newBall = forwardIterator.next()
		def ballsToRemove = [newBall]
		
		while(forwardIterator.hasNext()){
			def ballToCheck = forwardIterator.next()
			if(ballToCheck.color != newBall.color)
				break;
			
			ballsToRemove << ballToCheck			
		}
		
		def backwardsIterator = entity.balls.listIterator(message.index)
		while(backwardsIterator.hasPrevious()){
			def ballToCheck = backwardsIterator.previous()
			if(ballToCheck.color != newBall.color)
				break;
			
			ballsToRemove << ballToCheck			
		}
		
		if(ballsToRemove.size() < 3)
			return
			
		ballsToRemove.each { ball ->
			entity.balls.remove(ball)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(ball))
		}
		println "Se formo jueguito - ${ballsToRemove.size()}"
	})
}

