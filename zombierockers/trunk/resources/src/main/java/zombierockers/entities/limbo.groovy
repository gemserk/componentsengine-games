package zombierockers.entities

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 

builder.entity("limbo-${Math.random()}") {
	
	tags("limbo")
	
	property("deque",new LinkedList())
		
	property("path", parameters.path)
		
	property("spawnPoint",{entity.path.getPoint(1)})
	property("nextBallPoint",{entity.path.getPoint(2)})
	
	component(utils.components.genericComponent(id:"releaseBallsHandler", messageId:["releaseBalls"]){ message ->
		def deque = entity.deque
		if(deque.isEmpty())
			return
			
		def ball = deque.pop()
		ball.state = "spawned"
		
		ball.position = entity.spawnPoint.copy()
		messageQueue.enqueue(utils.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity.segment
			newMessage.ball = ball
		})
		entity.parent.ballsQuantity++
	})
		
	component(new ComponentFromListOfClosures("nextBallPointReached",[{ UpdateMessage message ->
		def predicates = Predicates.and(EntityPredicates.withAllTags("ball"),{ball -> ball.state == "spawned"} as Predicate,EntityPredicates.isNear(entity.nextBallPoint, 5f))
	    def balls = entity.root.getEntities(predicates)
	    
	    if(balls.isEmpty())
	    	return
	                                                                  			
	    def ball = balls[0]
	    ball.state = "inWorld"
	    utils.custom.messageQueue.enqueue(utils.genericMessage("releaseBalls"){})
		                                                                  		           
	    }
	]))
		
	component(utils.components.genericComponent(id:"dumpDequeHandler", messageId:["dumpDeque"]){ message ->
		println "DUMPINGDEQUE:${entity.deque.collect{it.color}}"
	})
		
	component(utils.components.genericComponent(id:"spawnedSegmentHandler", messageId:["spawnedSegment"]){ message ->
		def deque = entity.deque
		message.balls.each { ball ->
			deque.addLast(ball)
		}
		def segment = message.segment
		entity.segment = segment
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(segment, entity.parent))
		
		messageQueue.enqueue(utils.genericMessage("releaseBalls"){})
	})
}

