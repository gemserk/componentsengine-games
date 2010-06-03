package zombierockers.entities

import com.google.common.base.Predicate;
import com.google.common.base.Predicates 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.utils.EntityDumper 
import com.gemserk.games.zombierockers.PathTraversal;
import net.sf.json.JSONArray 

builder.entity("segment-${Math.random()}") {
	
	tags("segment")
	
	property("pathTraversal", parameters.pathTraversal ?: new PathTraversal(parameters.path,1,0))
	property("speed", parameters.speed)
	property("balls",parameters.balls ?: new LinkedList())
	
	property("firstBall", {entity.balls[0]})
	property("lastBall", {entity.balls[-1]})
	property("isEmpty", {entity.balls.isEmpty()})

	property("acceleratedSpeed", parameters.acceleratedSpeed ?: 0.08f)
	property("accelerated", parameters.accelerated ?: false)
	property("accelerationStopPoint", parameters.accelerationStopPoint)
	
	property("segmentTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.segment"), 
			utils.custom.genericprovider.provide{ data ->
				[
				pathTraversal:data.pathTraversal,
				balls:data.balls,
				speed:data.speed
				]
			}))
	
	
	def getPathTraversal = {entity, index ->
		def pathTraversal = entity.pathTraversal
		int currentIndex = entity.balls.size() -1
		entity.balls.reverseEach { ball ->
			if(currentIndex == index)
				return
			
			pathTraversal = pathTraversal.add((float)-ball.radius * 2)
			currentIndex--
		}
		
		return pathTraversal
	}
	
	component(utils.components.genericComponent(id:"segmentRemoveHead", messageId:["segmentRemoveHead"]){ message ->
		if(message.segment != entity)
			return
		
		if (entity.balls.size() < 2) {
			entity.balls.each { ball ->
				log.info("Removed last ball - segment.id: $entity.id - ball.id: $ball.id")
				messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(ball))
			}
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
			log.info("Removed segment - segment.id: $entity.id")
			return
		}
		
		def lastBall = entity.lastBall
		entity.pathTraversal = getPathTraversal(entity,entity.balls.size()-2)
		log.info("Removed last ball - segment.id: $entity.id - ball.id: $lastBall.id")
		entity.balls.remove(lastBall)
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(lastBall))
	})
	
	
	component(utils.components.genericComponent(id:"addNewBallHandler", messageId:["addNewBall"]){ message ->
		if(message.segment != entity)
			return
		
		def insertionPoint = message.index ?: 0
		
		def ball = message.ball
		
		entity.balls.add(insertionPoint, ball)
		ball.pathTraversal = getPathTraversal(entity, insertionPoint)
		
		if(insertionPoint > 0)
			entity.pathTraversal = entity.pathTraversal.add((float)ball.radius * 2)
		
		if (insertionPoint == entity.balls.size()-1) 
			ball.pathTraversal = entity.pathTraversal
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(ball, entity.parent))
		
		log.info("Added ball to segment - segment.id: $message.segment.id -  ball: $ball.id - $ball.color - index: $insertionPoint")
	})
	
	component(utils.custom.components.closureComponent("advanceHandler"){ UpdateMessage message ->
		def speed = entity.speed
		if (entity.accelerated)
			speed = entity.acceleratedSpeed
			
		def distance = (float)(speed * message.delta)
		def pathTraversal = entity.pathTraversal.add(distance)
		entity.pathTraversal = pathTraversal
		
		def messageQueue = utils.custom.messageQueue
		entity.balls.reverseEach { ball ->
			ball.pathTraversal = pathTraversal
			pathTraversal = pathTraversal.add((float)-ball.radius * 2)
		}
	})
	
	component(utils.custom.components.closureComponent("checkEndAcceleration"){ UpdateMessage message ->
		if (!entity.accelerated)
			return
			
		if (entity.pathTraversal > entity.accelerationStopPoint) { 
			log.info("Segment stoped initial acceleration - segment.id: $entity.id")
			entity.accelerated = false
		}
	})
	
	component(utils.components.genericComponent(id:"bulletHitHandler", messageId:["bulletHit"]){ message ->
		def collisionBall = message.targets[0]
		def ballIndex = entity.balls.indexOf(collisionBall)
		
		if(ballIndex == -1)
			return

		def ball = message.source.ball
		
		log.info("Bullet collided with segment: segment.id: $entity.id - ball.id: $collisionBall.id - ballIndex: $ballIndex - newBall.id: $ball.id")
		
		def tangent = getPathTraversal(entity,ballIndex).tangent
		
		def collisionBallPosition = collisionBall.position
		def bulletPosition = message.source.position
		
		def differenceVector = bulletPosition.copy().sub(collisionBallPosition)
		
		def proyection = tangent.dot(differenceVector)
		
		if(proyection > 0)
			ballIndex++
		
		
		messageQueue.enqueue(utils.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity
			newMessage.ball = ball
			newMessage.index = ballIndex
		})
		
		messageQueue.enqueue(utils.genericMessage("checkBallSeries"){newMessage -> 
			newMessage.segment = entity
			newMessage.ball = ball
		})
		entity.parent.ballsQuantity++
		
	})
	
	component(utils.components.genericComponent(id:"checkBallSeriesHandler", messageId:["checkBallSeries"]){ message ->
		if(message.segment != entity)
			return
		
			
		def ballFromMessage = message.ball
		def index = entity.balls.indexOf(ballFromMessage)
		
		log.info("Checking ball series - segment.id: $entity.id - ball.id: $ballFromMessage - ballIndex: $index")
		if(index == -1)
			return
		
		def forwardIterator = entity.balls.listIterator(index)
		def newBall = forwardIterator.next()
		def ballsToRemove = [newBall]
		
		while(forwardIterator.hasNext()){
			def ballToCheck = forwardIterator.next()
			if(ballToCheck.color != newBall.color)
				break;
			
			ballsToRemove << ballToCheck			
		}
		
		def backwardsIterator = entity.balls.listIterator(index)
		while(backwardsIterator.hasPrevious()){
			def ballToCheck = backwardsIterator.previous()
			if(ballToCheck.state == "spawned" || ballToCheck.color != newBall.color)
				break;
			
			ballsToRemove.add(0,ballToCheck)			
		}
		
		
		if(ballsToRemove.size() < 3) {
			utils.custom.messageQueue.enqueue(utils.genericMessage("checkSameColorSegments"){})
			log.info("When ball added to segment less than 3 balls  in series- segment.id: $entity.id - balls.id: ${ballsToRemove*.id} - balls.color: ${ballsToRemove[0].color}")			
			return
		}
		
		log.info("When ball added to segment 3 or more in series- segment.id: $entity.id - balls.id: ${ballsToRemove*.id} - balls.color: ${ballsToRemove[0].color}")	
		utils.custom.messageQueue.enqueue(utils.genericMessage("removeBalls"){newMessage ->
			newMessage.segment = entity
			newMessage.ballsToRemove = ballsToRemove
		})
	})
	
	component(utils.components.genericComponent(id:"engageReverseHandler", messageId:["engageReverse"]){ message ->
		if(message.segment != entity)
			return 
		def oldSpeed = entity.speed
		def newSpeed = message.speed
		log.info("Changing speed - $entity.id - oldSpeed: $oldSpeed - newSpeed: $newSpeed")
		entity.speed = newSpeed
	})
	
	component(utils.components.genericComponent(id:"splitSegmentHandler", messageId:["removeBalls"]){ message ->
		if(message.segment != entity)
			return
		def balls = entity.balls
		def ballsToRemove = message.ballsToRemove
		
		def firstIndex = balls.indexOf(ballsToRemove[0])
		def lastIndex = balls.indexOf(ballsToRemove[-1])
		
		def originalPathTraversal = entity.pathTraversal
		
		def firstSegmentBalls = new LinkedList(balls.subList(0,firstIndex))
		def secondSegmentBalls = new LinkedList(balls.subList(lastIndex+1,balls.size()))
		
		log.info("Splitting segment when removeBalls - segment.id: $entity.id - ballsToRemove: ${ballsToRemove.size()} - segment.balls.size: $balls.size")
		log.info("First subsegment balls - ${firstSegmentBalls.size()}")
		log.info("Second subsegment balls - ${secondSegmentBalls.size()}")
		
		if  (balls.size != (ballsToRemove.size + firstSegmentBalls.size + secondSegmentBalls.size))
		{
			log.info("WARNING: SPLIT SEEMS TO BE BROKEN!! DUMPING")
			log.info(JSONArray.fromObject(new EntityDumper().dumpEntity(entity.root)).toString(4))
		}
		
		if(firstSegmentBalls.isEmpty() && secondSegmentBalls.isEmpty()){
			log.info("Both subsegments are empty, removing segment - $entity.id")
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
		} else 	if(firstSegmentBalls.isEmpty()){
			log.info("First subsegment is empty - $entity.id")
			entity.balls = secondSegmentBalls	
		} else {
			entity.pathTraversal = getPathTraversal(entity,firstIndex -1)
			entity.balls = firstSegmentBalls
			
			if(!secondSegmentBalls.isEmpty()){
				def newParameters = [pathTraversal:originalPathTraversal,balls:secondSegmentBalls,speed:0.0f]
				def segment = entity.segmentTemplate.get(newParameters)
				messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(segment,entity.parent))
				log.info("Splitted in two segments - segment.id: $entity.id - newSegment.id: $segment.id")
			} else {
				log.info("Second subsegment is empty - segment: $entity.id")
			}
		}
		
		ballsToRemove.each { ball ->
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(ball))
		}
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("checkSameColorSegments"){})
	})
	
	component(utils.components.genericComponent(id:"explosionsWhenRemoveBallsHandler", messageId:["removeBalls"]){ message ->
		if(message.segment != entity)
			return
		
		def ballsToRemove = message.ballsToRemove
		ballsToRemove.each { ball ->
			messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
				newMessage.explosion =EffectFactory.explosionEffect(100, (int) ball.position.x, (int) ball.position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f, ball.color, ball.color) 
			})
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
		
		log.info("Collision detected with other segment - masterSegment.id: $entity.id - slaveSegment.id: $collisionSegment.id")

		utils.custom.messageQueue.enqueue(utils.genericMessage("mergeSegments"){newMessage ->
			newMessage.masterSegment = entity
			newMessage.slaveSegment = collisionSegment
		})
		
		
	})
	
	component(utils.components.genericComponent(id:"mergeSegmentsHandler", messageId:["mergeSegments"]){ message ->
		if(message.masterSegment != entity)
			return
		
		log.info("Merging segments: masterSegment.id: $message.masterSegment.id - slaveSegment.id: $message.slaveSegment.id")
			
		def slaveSegment = message.slaveSegment
		entity.pathTraversal = slaveSegment.pathTraversal
		
		def ballToCheck = slaveSegment.firstBall
		
		entity.balls.addAll(slaveSegment.balls)
		slaveSegment.balls.clear()
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(slaveSegment))
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("checkBallSeries"){newMessage ->
			newMessage.segment = entity
			newMessage.ball = ballToCheck
		})
	})
	
	component(utils.components.genericComponent(id:"baseReachedHandler", messageId:["baseReached"]){ message ->
		log.info("Base reached - Accelerating - segment.id: $entity.id")
		entity.speed = 0.8f
	})
}

