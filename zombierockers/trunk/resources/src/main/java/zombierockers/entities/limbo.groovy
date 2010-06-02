package zombierockers.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.games.zombierockers.PathTraversal;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 

builder.entity("limbo-${Math.random()}") {
	
	tags("limbo")
	
	property("deque",new LinkedList())
		
	property("path", parameters.path)
		
	property("nextBallPoint",new PathTraversal(parameters.path,2,0))
	
	property("done",false)
	
	component(utils.components.genericComponent(id:"releaseBallsHandler", messageId:["releaseBalls"]){ message ->
		def deque = entity.deque
		if(deque.isEmpty())
			return
			
		def ball = deque.pop()
		ball.state = "spawned"
		
		// ball.position = entity.spawnPoint.copy()
		messageQueue.enqueue(utils.genericMessage("addNewBall"){newMessage -> 
			newMessage.segment = entity.segment
			newMessage.ball = ball
		})
		entity.parent.ballsQuantity++
		
		if(deque.isEmpty())
			entity.done = true
	})
		
	component(new ComponentFromListOfClosures("nextBallPointReached",[{ UpdateMessage message ->
		def predicates = Predicates.and(EntityPredicates.withAllTags("ball"),{ball -> ball.state == "spawned"} as Predicate,{ball -> ball.pathTraversal > entity.nextBallPoint} as Predicate)
	    def balls = entity.root.getEntities(predicates)
	    
	    if(balls.isEmpty())
	    	return		
	    	
	    def ball = balls[0]
	    ball.state = "inWorld"
	    utils.custom.messageQueue.enqueue(utils.genericMessage("releaseBalls"){})
	    }
	]))
		
		
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
	
	component(utils.components.genericComponent(id:"baseReachedHandler", messageId:["baseReached"]){ message ->
		entity.deque.clear()
	})
	
}

