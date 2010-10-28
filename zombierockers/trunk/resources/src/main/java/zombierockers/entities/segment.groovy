package zombierockers.entities

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 

builder.entity("segment-${Math.random()}") {
	
	tags("segment")
	
	property("pathTraversal", parameters.pathTraversal )
	property("speed", parameters.speed)
	property("balls",parameters.balls ?: new LinkedList())
	
	property("firstBall", {entity.balls[0] })
	property("lastBall", {entity.balls[-1] })
	property("isEmpty", {entity.balls.isEmpty() })
	
	property("minSpeedFactor", parameters.minSpeedFactor ?: 0.2f)
	property("maxSpeed", parameters.maxSpeed ?: 0.04f)
	property("speedWhenReachBase", parameters.speedWhenReachBase ?: 0.4f)
	
	property("acceleratedSpeed", parameters.acceleratedSpeed ?: 0.08f)
	property("accelerated", parameters.accelerated ?: false)
	property("accelerationStopPoint", parameters.accelerationStopPoint)
	
	property("pathLength",parameters.pathLength)
	
	property("segmentTemplate",new InstantiationTemplateImpl(
			utils.templateProvider.getTemplate("zombierockers.entities.segment"), 
			utils.genericprovider.provide{ data ->
				[
				pathTraversal:data.pathTraversal,
				balls:data.balls,
				speed:data.speed,
				minSpeedFactor:data.minSpeedFactor,
				maxSpeed:data.maxSpeed,
				speedWhenReachBase:data.speedWhenReachBase,
				pathLength:data.pathLength
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
			messageQueue.enqueue(utils.messages.genericMessage("destroySegment"){ newMessage ->
				newMessage.segment = entity 					
			})
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
		
		if (insertionPoint >= entity.balls.size)
			insertionPoint = entity.balls.size
		
		entity.balls.add(insertionPoint, ball)
		ball.pathTraversal = getPathTraversal(entity, insertionPoint)
		ball.newPathTraversal = ball.pathTraversal
		//		if(insertionPoint > 0)
		//			entity.pathTraversal = entity.pathTraversal.add((float)ball.radius * 2)
		
		if (insertionPoint == entity.balls.size()-1) 
			ball.pathTraversal = entity.pathTraversal
		
			
		ball.segment = entity
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(ball, entity.parent))
		
		log.info("Added ball to segment - segment.id: $message.segment.id -  ball: $ball.id - $ball.color - index: $insertionPoint")
	})
	
	component(utils.components.genericComponent(id:"incrementRadiusBallQueued", messageId:["update"]){ message ->
		def delayedCheckBallSeries = []
		entity.balls.each { ball ->
			if (!ball.isGrownUp) {
				
				def ballGrowSpeed = 0.016f * 6f
				def grow = ballGrowSpeed * message.delta
				
				ball.radius = (float) ball.radius + grow
				def diff = ball.radius - ball.finalRadius
				if (diff > 0) {
					grow -= diff
					ball.radius = ball.finalRadius
					
					delayedCheckBallSeries << ball
					
				}
				
				if (ball != entity.firstBall)
					entity.pathTraversal = entity.pathTraversal.add((float)grow * 2)
			}
		}
		
		delayedCheckBallSeries.each { ball -> 
			utils.messageQueue.enqueue(utils.messages.genericMessage("checkBallSeries"){newMessage -> 
				newMessage.ball = ball
			})
		}
		
		
	})
	
	component(utils.components.genericComponent(id:"advanceHandler", messageId:["update"]){ message ->
		def speed = entity.speed
		
		if (entity.accelerated)
			speed = entity.acceleratedSpeed
		else if(entity.baseReached){
			speed = entity.speedWhenReachBase
		}
		else if(speed > 0){
			def minSpeedFactor = entity.minSpeedFactor
			def maxSpeed = entity.maxSpeed
			def minSpeed = maxSpeed*minSpeedFactor
			speed = (float) minSpeed + maxSpeed * (1-minSpeedFactor) *(1-(entity.pathTraversal.distanceFromOrigin/entity.pathLength))
		}	
		
		
		def distance = (float)(speed * message.delta)
		def pathTraversal = entity.pathTraversal.add(distance)
		entity.pathTraversal = pathTraversal
		
		def messageQueue = utils.messageQueue
		entity.balls.reverseEach { ball ->
			ball.newPathTraversal = pathTraversal
			pathTraversal = pathTraversal.add((float)-ball.radius * 2f)
		}
	})
	
	component(utils.components.genericComponent(id:"checkEndAcceleration", messageId:["update"]){ message ->

		if (!entity.accelerated)
			return
		
		if (entity.pathTraversal.distanceFromOrigin > entity.accelerationStopPoint) { 
			log.info("Segment stoped initial acceleration - segment.id: $entity.id")
			entity.accelerated = false
		}
	})
	
	component(utils.components.genericComponent(id:"bulletHitHandler", messageId:["bulletHit"]){ message ->
		def collisionBall = message.targets[0]
		if(collisionBall.segment != entity)
			return
		
		def ballIndex = entity.balls.indexOf(collisionBall)
		
		if(ballIndex == -1){
			def textMessage = "Collision ball had wrong segment setted -  ball.id: $collisionBall.id - ball.segment.id: $entity.id"
			log.error(textMessage)
			throw new RuntimeException(textMessage)
		}
		
		def ball = message.source.ball
		
		log.info("Bullet collided with segment: segment.id: $entity.id - ball.id: $collisionBall.id - ballIndex: $ballIndex - newBall.id: $ball.id")
		
		def tangent = getPathTraversal(entity,ballIndex).tangent
		
		def collisionBallPosition = collisionBall.position
		def bulletPosition = message.source.position
		
		def differenceVector = bulletPosition.copy().sub(collisionBallPosition)
		
		def proyection = tangent.dot(differenceVector)
		
		if(proyection > 0)
			ballIndex++
		
		
		messageQueue.enqueue(utils.messages.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity
			newMessage.ball = ball
			newMessage.index = ballIndex
		})
		
		entity.parent.ballsQuantity++
		
	})
	
	component(utils.components.genericComponent(id:"checkBallSeriesHandler", messageId:["checkBallSeries"]){ message ->
		def ballFromMessage = message.ball
		if(!ballFromMessage.alive)
			return
		if (ballFromMessage.segment != entity)
			return
		
		
		def index = entity.balls.indexOf(ballFromMessage)
		
		log.info("Checking ball series - segment.id: $entity.id - ball.id: $ballFromMessage.id - ballIndex: $index")
		if(index == -1){
			def textMessage = "CheckBallSeries -  ball had wrong segment setted -  ball.id: $ballFromMessage.id - ball.segment.id: $entity.id"
			log.error(textMessage)
			throw new RuntimeException(textMessage)
		}
		
		def forwardIterator = entity.balls.listIterator(index)
		def newBall = forwardIterator.next()
		def ballsToRemove = [newBall]
		
		while(forwardIterator.hasNext()){
			def ballToCheck = forwardIterator.next()
			if(ballToCheck.type != newBall.type)
				break;
			
			ballsToRemove << ballToCheck			
		}
		
		def backwardsIterator = entity.balls.listIterator(index)
		while(backwardsIterator.hasPrevious()){
			def ballToCheck = backwardsIterator.previous()
			if(ballToCheck.state == "spawned" || ballToCheck.type != newBall.type)
				break;
			
			ballsToRemove.add(0,ballToCheck)			
		}
		
		
		if(ballsToRemove.size() < 3) {
			utils.messageQueue.enqueue(utils.messages.genericMessage("checkSameColorSegments"){
			})
			log.info("When ball added to segment less than 3 balls  in series- segment.id: $entity.id - balls.id: ${ballsToRemove*.id} - balls.color: ${ballsToRemove[0].color}")			
			return
		}
		
		def mustContainBall = message.mustContainBall
		if (mustContainBall) {
			if (!ballsToRemove.contains(mustContainBall)) {
				log.info("When ball added to segment more than 3 balls but does not contains - segment.id: $entity.id - balls.id: ${ballsToRemove*.id} - balls.color: ${ballsToRemove[0].color} - mustContainBall.id: ${mustContainBall.id} - mustContainBall.color: ${mustContainBall.color}")			
				return
			}
		}
		
		log.info("When ball added to segment 3 or more in series- segment.id: $entity.id - balls.id: ${ballsToRemove*.id} - balls.color: ${ballsToRemove[0].color}")	
		utils.messageQueue.enqueue(utils.messages.genericMessage("seriesDetected"){newMessage ->
			newMessage.segment = entity
			newMessage.ballsToRemove = ballsToRemove
		})
	})
	
	component(utils.components.genericComponent(id:"ChangeSpeedHandler", messageId:["segmentChangeSpeed"]){ message ->
		if(message.segment != entity)
			return 
		def oldSpeed = entity.speed
		def newSpeed = message.speed
		log.info("Changing speed - $entity.id - oldSpeed: $oldSpeed - newSpeed: $newSpeed")
		entity.speed = newSpeed
	})
	
	component(utils.components.genericComponent(id:"splitSegmentHandler", messageId:["seriesDetected"]){ message ->
		//		if(message.segment != entity)
		//			return
		def balls = entity.balls
		def ballsToRemove = message.ballsToRemove
		
		def ballsInside = ballsToRemove.findAll { balls.contains(it) }
		
		if (ballsInside.isEmpty())
			return
		
		if (ballsInside.size != ballsToRemove.size) {
			
			log.info("Splitting segment when removeBalls cancelled because ballsToRemove not in segment - segment.id: $entity.id - ballsToRemove: ${ballsToRemove.size()} - ballsOutside.ids: ${ballsToRemove.findAll({ !balls.contains(it)})*.id }")

			messageQueue.enqueue(utils.messages.genericMessage("checkBallSeries"){newMessage -> 
				newMessage.ball = ballsInside[0]
			})
			
			return
		}
		
		def firstIndex = balls.indexOf(ballsToRemove[0])
		def lastIndex = balls.indexOf(ballsToRemove[-1])
		
		def originalPathTraversal = entity.pathTraversal
		
		def firstSegmentBalls = new LinkedList(balls.subList(0,firstIndex))
		def secondSegmentBalls = new LinkedList(balls.subList(lastIndex+1,balls.size()))
		
		log.info("Splitting segment when removeBalls - segment.id: $entity.id - ballsToRemove: ${ballsToRemove.size()} - segment.balls.size: $balls.size")
		log.info("First subsegment balls - ${firstSegmentBalls.size()}")
		log.info("Second subsegment balls - ${secondSegmentBalls.size()}")
		
		def betweenSegment =  new LinkedList(balls.subList(firstIndex, lastIndex+1))
		
		if (betweenSegment.size != ballsToRemove.size) {
			log.info("Splitting canceled because concurrent merge and ball insertion - balls.ids: ${betweenSegment*.id} - balls.colors: ${betweenSegment*.color}")
			// log.info(JSONArray.fromObject(new EntityDumper().dumpEntity(entity.root)).toString(4))
			return
		}
		
		if(firstSegmentBalls.isEmpty() && secondSegmentBalls.isEmpty()){
			log.info("Both subsegments are empty, removing balls - segment.id: $entity.id")
			entity.balls.clear()
			messageQueue.enqueue(utils.messages.genericMessage("destroySegment"){ newMessage ->
				newMessage.segment = entity 					
			})
			
		} else 	if(firstSegmentBalls.isEmpty()){
			log.info("First subsegment is empty - $entity.id")
			entity.balls = secondSegmentBalls	
		} else {
			entity.pathTraversal = getPathTraversal(entity,firstIndex -1)
			entity.balls = firstSegmentBalls
			
			if(!secondSegmentBalls.isEmpty()){
				def newParameters = [pathTraversal:originalPathTraversal,balls:secondSegmentBalls,speed:0.0f,pathLength:entity.pathLength, 
				                     minSpeedFactor:entity.minSpeedFactor, maxSpeed:entity.maxSpeed, speedWhenReachBase:entity.speedWhenReachBase]
				                     
				def segment = entity.segmentTemplate.get(newParameters)
				messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(segment,entity.parent))
				secondSegmentBalls.each { ball -> ball.segment = segment}
				log.info("Splitted in two segments - segment.id: $entity.id - newSegment.id: $segment.id")
			} else {
				log.info("Second subsegment is empty - segment: $entity.id")
			}
		}
		
		utils.messageQueue.enqueue(utils.messages.genericMessage("explodeBall"){newMessage -> 
			newMessage.balls=ballsToRemove
		})
		
		utils.messageQueue.enqueue(utils.messages.genericMessage("checkSameColorSegments"){
		})
	})
	
	component(utils.components.genericComponent(id:"mergeSegmentsHandler", messageId:["mergeSegments"]){ message ->
		if(message.masterSegment != entity)
			return
		
		log.info("Merging segments: masterSegment.id: $message.masterSegment.id - slaveSegment.id: $message.slaveSegment.id")
		
		def slaveSegment = message.slaveSegment
		entity.pathTraversal = slaveSegment.pathTraversal
		
		def ballToCheck = slaveSegment.firstBall
		def mustContainBall = entity.lastBall
		
		entity.balls.addAll(slaveSegment.balls)
		slaveSegment.balls.each { ball -> ball.segment = entity}
		slaveSegment.balls.clear()
		messageQueue.enqueue(utils.messages.genericMessage("destroySegment"){ newMessage ->
			newMessage.segment = slaveSegment 					
		})
		
		utils.messageQueue.enqueue(utils.messages.genericMessage("checkBallSeries"){newMessage ->
			newMessage.ball = ballToCheck
			newMessage.mustContainBall = mustContainBall
		})
	})
	
	component(utils.components.genericComponent(id:"baseReachedHandler", messageId:["baseReached"]){ message ->
		log.info("Base reached - Accelerating - segment.id: $entity.id")
		entity.baseReached = true
	})
}

