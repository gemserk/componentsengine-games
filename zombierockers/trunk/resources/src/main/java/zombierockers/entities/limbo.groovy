package zombierockers.entities

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.games.zombierockers.PathTraversal;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 

builder.entity("limbo-${Math.random()}") {
	
	tags("limbo")
	
	property("deque",new LinkedList())
	property("path", parameters.path)
	property("nextBallPoint", new PathTraversal(parameters.path,0,0).add(32))
	property("done",false)
	
	property("isEmpty", {entity.deque.isEmpty()})
	
	component(utils.components.genericComponent(id:"releaseBallsHandler", messageId:["releaseBalls"]){ message ->
		def deque = entity.deque
		if(deque.isEmpty())
			return
			
		def ball = deque.pop()
		ball.state = "spawned"
		
		log.info("Ball released from limbo - ballId:$ball.id - color:$ball.color")
		// ball.position = entity.spawnPoint.copy()
		messageQueue.enqueue(utils.messages.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity.segment
			newMessage.ball = ball
		})
		entity.parent.ballsQuantity++
		
		if(deque.isEmpty()){
			log.info("Last ball in limbo released - limbo.id: $entity.id")
			entity.done = true
		}
	})
	component(utils.components.genericComponent(id:"nextBallPointReached", messageId:["update"]){ message ->	
		def predicates = Predicates.and(EntityPredicates.withAllTags("ball"),{ball -> ball.state == "spawned"} as Predicate,{ball -> ball.pathTraversal > entity.nextBallPoint} as Predicate)
	    def balls = entity.root.getEntities(predicates)
	    
	    if(balls.isEmpty())
	    	return		
	    	
	    
	    def ball = balls[0]
	    ball.state = "inWorld"
	    utils.messageQueue.enqueue(utils.messages.genericMessage("releaseBalls"){})
	})
		
		
	component(utils.components.genericComponent(id:"spawnedSegmentHandler", messageId:["spawnedSegment"]){ message ->
		def deque = entity.deque
		message.balls.each { ball ->
			deque.addLast(ball)
		}
		def segment = message.segment
		entity.segment = segment
		
		log.info("New segment and balls added to limbo - segment.id:$segment.id - deque.size:$deque.size - deque.balls.collors:${deque.collect{it.color}}")
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(segment, entity.parent))
		utils.messageQueue.enqueue(utils.messages.genericMessage("releaseBalls"){
		})
	})
	
	component(utils.components.genericComponent(id:"baseReachedHandler", messageId:["baseReached"]){ message ->
		entity.deque.clear()
		log.info("Limbo cleared because of baseReached")
	})
	
}

