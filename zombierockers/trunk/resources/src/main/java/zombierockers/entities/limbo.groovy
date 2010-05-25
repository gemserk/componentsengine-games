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
		println "Releasing Balls"
		def deque = entity.deque
		if(deque.isEmpty())
			return
			
		def ball = deque.pop()
		ball.state = "spawned"
		
		ball.position = entity.spawnPoint.copy()
		ball."followpath.pathindex" = 2
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(ball, entity.parent))
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
		
	component(utils.components.genericComponent(id:"spawnedBallHandler", messageId:["spawnedBall"]){ message ->
		entity.deque.addLast(message.ball)
	})
}

