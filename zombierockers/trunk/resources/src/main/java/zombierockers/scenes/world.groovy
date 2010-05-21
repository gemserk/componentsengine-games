package zombierockers.scenes

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.PathRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.timers.PeriodicTimer;

builder.entity {
	
	property("bounds",utils.rectangle(0,0,800,600))
	
	Random random = new Random()
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("background"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
	}
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	property("path",new Path([utils.vector(-40+60,200),utils.vector(-20+60,200),utils.vector(0+60,200),utils.vector(160,200), utils.vector(240,80),utils.vector(260,70),utils.vector(280,80), utils.vector(440,410),utils.vector(460,420),utils.vector(480,410), utils.vector(560,200), utils.vector(760,200)]))	
	
	child(entity("path"){
		
		component(new PathRendererComponent("pathrendererBorders")){
			property("lineColor", utils.color(0.2f, 0.2f, 0.7f, 1.0f))
			property("lineWidth", 40.0f)
			property("path", {entity.parent.path})		
		}
		component(new PathRendererComponent("pathrendererFill")){
			property("lineColor", utils.color(0.5f, 0.5f, 1f, 1.0f))
			property("lineWidth", 30.0f)
			property("path", {entity.parent.path})		
		}
	})
	
	
	child(entity("spawner"){
		property("position", utils.vector(-20,200))
		
		property("spawnTimer",new PeriodicTimer(4000))
		property("spawnQuantity",10)
		
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
		
		
		component(new TimerComponent("spawnerTimer")){
			property("trigger",utils.custom.triggers.genericMessage("spawnBall") {message.source = entity })
			propertyRef("timer","spawnTimer")
		}
		
		component(utils.components.genericComponent(id:"spawnBallHandler", messageId:["spawnBall"]){ message ->
			def template = entity.ballTemplate
			
			def colors = [utils.color(1,0,0,1), utils.color(0,1,0,1), utils.color(0,0,1,1)]
			
			entity.spawnQuantity.times {
				def color = getRandomItem(colors)
				
				def parameters = [position:entity.position.copy(),path:entity.parent.path,color:color]
				def ball = template.get(parameters)
				messageQueue.enqueue(utils.genericMessage("spawnedBall"){newMessage -> newMessage.ball = ball})
			}
		})
		
	})
	
	
	child(entity("limbo"){
		property("deque",new LinkedList())
		
		property("path",{entity.parent.path})
		
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
		
		component(new ComponentFromListOfClosures("nextBallPointReached",[
		                                                                  { UpdateMessage message ->
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
	})
	
	
	
	
	
	
	
	child(entity("cannon"){
		parent("zombierockers.entities.cannon",[bounds:utils.rectangle(20,20,760,560)])
	})
	
	component(new ExplosionComponent("explosions")) {
	}
	
	input("inputmapping"){
		keyboard {
			press(button:"space",eventId:"releaseBalls")
			press(button:"d",eventId:"dumpDeque")
		}
	}
}
