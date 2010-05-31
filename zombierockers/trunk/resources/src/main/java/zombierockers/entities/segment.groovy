package zombierockers.entities

import com.google.common.base.Predicate;
import com.google.common.base.Predicates 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
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
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(message.ball, entity.parent))
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
	})
	
	component(utils.custom.components.closureComponent("collisionBetweenSegmentsDetector"){ UpdateMessage message ->
		def segments = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), {segment -> !segment.balls.isEmpty()} as Predicate))
		
		def collisionSegment = segments.find{ segment ->
			if(segment == entity)
				return false
			
			def myPosition = entity.pathTraversal.position
			def segmentBalls = segment.balls
			
			def firstBall = segmentBalls[0]
			def firstBallPosition = firstBall.position
			
			def distance = myPosition.distance(firstBallPosition)
			return (distance < (float)firstBall.radius * 2)						
		}
		
		if(collisionSegment == null)
			return
			
		utils.custom.messageQueue.enqueue(utils.genericMessage("mergeSegments"){newMessage ->
			newMessage.masterSegment = entity
			newMessage.slaveSegment = collisionSegment
		})
		
		
	})
	
	component(utils.components.genericComponent(id:"mergeSegmentsHandler", messageId:["mergeSegments"]){ message ->
		if(message.masterSegment != entity)
			return
			
		def slaveSegment = message.slaveSegment
		entity.pathTraversal = slaveSegment.pathTraversal
		
		
		
		entity.balls.addAll(slaveSegment.balls)
		slaveSegment.balls.clear()
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(slaveSegment))
	})
}

