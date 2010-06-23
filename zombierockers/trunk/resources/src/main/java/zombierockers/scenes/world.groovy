package zombierockers.scenes

import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.render.ClosureRenderObject;

import com.google.common.base.Predicate;


import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 


builder.entity {
	
	def gameProperties = utils.custom.gameStateManager.gameProperties
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("ballsQuantity",0)
	property("baseReached",false)
	
	property("level", parameters.level)
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	def offset = 0f
	
	property("path",new Path(utils.custom.svg.loadPoints(entity.level.path, "path")))	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image(entity.level.background))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", -1000)
	}
	
	property("ballShadowImage", utils.resources.image("ballshadow"))
	
	component(utils.components.genericComponent(id:"drawBallShadows", messageId:["render"]){ message ->
		def balls = entity.getEntities(Predicates.and(EntityPredicates.withAllTags("ball"), {ball -> ball.alive } as Predicate))
		
		def ballShadowImage = entity.ballShadowImage
		
		def renderer = message.renderer
		
		balls.each { ball ->
			
			def position = ball.position
			def layer = -1
			def size = ball.size
			
			renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
				g.pushTransform()
				g.translate((float) position.x + 5, (float)position.y + 5)
				g.scale(size.x, size.y)
				g.drawImage(ballShadowImage, (float)-(ballShadowImage.getWidth() / 2), (float)-(ballShadowImage.getHeight() / 2))
				g.popTransform()
			}))
			
		}
		
	})
	
	component(utils.components.genericComponent(id:"placeablesRender", messageId:["render"]){ message ->
		
		def renderer = message.renderer
		
		def placeables = entity.level.placeables
		placeables.each { placeable ->
			def position = placeable.position
			def layer = placeable.layer
			def image = utils.resources.image(placeable.image)
			def input = utils.custom.gameContainer.input
			//position = utils.vector(input.mouseX, input.mouseY)
			//println position
			renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
				g.pushTransform()
				g.translate((float) position.x + 5, (float)position.y + 5)
				g.drawImage(image, (float)-(image.getWidth() / 2), (float)-(image.getHeight() / 2))
				g.popTransform()
			}))
		}
	})
	
	child(entity("path"){
		
	})
	
	child(id:"base", template:"zombierockers.entities.base") {
		position = entity.path.points[-1]
		radius = 15f
	}
	
	child(id:"spawner", template:"zombierockers.entities.spawner") { 
		path = entity.path
		ballsQuantity = entity.level.ballsQuantity
		ballDefinitions = entity.level.ballDefinitions
		pathProperties = entity.level.pathProperties
	}
	
	child(id:"limbo", template:"zombierockers.entities.limbo") { path = entity.path }
	
	child(id:"cannon", template:"zombierockers.entities.cannon") {
		bounds=utils.rectangle((float)20+offset,20,(float)760-offset,560)
		ballDefinitions = entity.level.ballDefinitions
		collisionMap = entity.level.collisionMap
	}
	
	child(entity("segmentsManager") {
		
		def getSortedSegments = { entity ->
			def segments = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), {segment-> !segment.isEmpty } as Predicate))
			return segments.sort { it.pathTraversal }
		}
		
		component(utils.components.genericComponent(id:"checkSameColorSegmentsHandler", messageId:["checkSameColorSegments"]){ message ->
			
			def reverseSpeed = -0.3f
			
			def sortedSegments = getSortedSegments(entity)
			
			log.info("Segments not empty: $sortedSegments.size")
			
			sortedSegments.size().times { index ->
				if (index == sortedSegments.size() -1 )
					return
				
				def segment = sortedSegments[index]
				def nextSegment = sortedSegments[index+1]
				def lastBallColor = segment.lastBall.color      
				def nextSegmentColor = nextSegment.firstBall.color
				if (lastBallColor == nextSegmentColor) {
					log.info("SegmentManager detected color coincidence between segments ends segment1.id: $segment.id - segment2.id - $nextSegment.id - colorCoincidence: $lastBallColor")
					utils.custom.messageQueue.enqueue(utils.genericMessage("segmentChangeSpeed"){newMessage ->
						newMessage.segment = nextSegment
						newMessage.speed = reverseSpeed
					})
				}
			}
			
		})
		
		component(utils.components.genericComponent(id:"checkFirstSegmentShouldAdvanceHandler", messageId:["checkFirstSegmentSholdAdvance"]){ message ->
			
			if (entity.parent.baseReached)
				return
			
			def sortedSegments = getSortedSegments(entity)
			
			log.info("Checking first segment should advance - cantSegments: $sortedSegments.size")
			
			if (sortedSegments.isEmpty())
				return
			
			def firstSegment = sortedSegments[0]
			
			if (firstSegment.speed <= 0) {
				utils.custom.messageQueue.enqueue(utils.genericMessage("segmentChangeSpeed"){newMessage ->
					newMessage.segment = firstSegment
					newMessage.speed = 0.04f
				})
			}
			
		})
		
		component(utils.components.genericComponent(id:"destroySegmentHandler", messageId:["destroySegment"]){ message ->
			def segment = message.segment 
			messageQueue.enqueueDelay(ChildrenManagementMessageFactory.removeEntity(segment))
			messageQueue.enqueue(utils.genericMessage("checkFirstSegmentSholdAdvance"){
			})
		})
		
		component(utils.components.genericComponent(id:"collisionBetweenSegmentsDetector", messageId:["update"]){ message ->
			def sortedSegments = getSortedSegments(entity)
			
			def collisionFound = false
			
			sortedSegments.size().times { index ->
				if (index == sortedSegments.size() -1 || collisionFound)
					return
				
				def segment = sortedSegments[index]
				def nextSegment = sortedSegments[index+1]
				
				def segmentLastBall = segment.lastBall
				def nextSegmentFirstBall = nextSegment.firstBall
				
				if(segmentLastBall.position.distance(nextSegmentFirstBall.position) < (float)segmentLastBall.radius * 2){
					log.info("Collision detected with other segment - masterSegment.id: $segment.id - slaveSegment.id: $nextSegment.id")
					
					utils.custom.messageQueue.enqueue(utils.genericMessage("mergeSegments"){newMessage ->
						newMessage.masterSegment = segment
						newMessage.slaveSegment = nextSegment
					})
					
					collisionFound = true
				}
			}
			
		})
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"s",eventId:"spawn")
			press(button:"d",eventId:"dumpDebug")
			press(button:"t",eventId:"concurrentHit")
		}
	}
	
	
	
	component(utils.components.genericComponent(id:"dumpDebugHandler", messageId:"dumpDebug"){ message ->
		Entity.times.entrySet().sort({it.count }).each { entry ->  println "$entry.element - $entry.count" }
	} )   
	
	
	
	property("startTimer",new CountDownTimer(2000))
	entity.startTimer.reset()
	component(new TimerComponent("startTimer")){
		property("trigger",utils.custom.triggers.genericMessage("spawn") {
		})
		propertyRef("timer","startTimer")
	}
	
	
	child(entity("ballsQuantityLAbel"){
		
		parent("gemserk.gui.label", [
		//font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.vector(740f, 30f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("message", {"Balls: ${entity.parent.ballsQuantity}".toString() })
	})
	
	component(utils.components.genericComponent(id:"baseReachedHandler", messageId:["baseReached"]){ message ->
		entity.baseReached = true
	})
	
	component(utils.components.genericComponent(id:"gameOverChecker", messageId:["update"]){ message ->		
		def limbosNotDone = entity.getEntities(Predicates.and(EntityPredicates.withAllTags("limbo"), {limbo -> !limbo.done } as Predicate))
		def allLimbosDone = limbosNotDone.isEmpty()
		def baseReached = entity.baseReached
		if(!baseReached && !allLimbosDone)
			return
		
		def segments = entity.getEntities(EntityPredicates.withAllTags("segment"))
		if(!segments.isEmpty())
			return
		
		def win = allLimbosDone && !baseReached
		log.info("Game over - winResult: $win")
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("gameover"){newMessage ->
			newMessage.win = win
		})
	})
	
	//	component(utils.components.genericComponent(id:"mergeSegmentsBugProvider", messageId:["mergeSegments"]){ message ->
	//		def template = new InstantiationTemplateImpl(
	//		utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
	//		utils.custom.genericprovider.provide{ data ->
	//			[
	//			radius:16.0f,
	//			color:data.color,
	//			state:"inWorld"
	//			]
	//		})
	//		
	//		
	//		def segment = message.masterSegment
	//		def lastBall = segment.lastBall
	//		//def ball = template.get([color:lastBall.color])
	//		def ball = template.get([color:utils.color(1,0,1)])
	//		
	//		messageQueue.enqueue(utils.genericMessage("bulletHit"){newMessage -> 
	//			newMessage.source = [ball:ball,position:lastBall.pathTraversal.add(10f).position]
	//			newMessage.targets = [lastBall]
	//		})
	//		
	//	})
	
	//	component(utils.components.genericComponent(id:"concurrentHitHandler", messageId:["concurrentHit"]){ message ->
	//		def template = new InstantiationTemplateImpl(
	//		utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
	//		utils.custom.genericprovider.provide{ data ->
	//			[
	//			radius:0.0f,
	//			color:data.color,
	//			state:"inWorld",
	//			finalRadius:16.0f
	//			]
	//		})
	//		
	//		def segment = entity.root.getEntities(EntityPredicates.withAllTags("segment"))[0]
	//		
	//		                                                                               
	//		def serieses = []
	//		segment.balls.each({ball -> 
	//			def balls = segment.balls
	//			def forwardIterator = balls.listIterator(balls.indexOf(ball))
	//			def newBall = forwardIterator.next()
	//			def ballsToRemove = [newBall]
	//			
	//			while(forwardIterator.hasNext()){
	//				def ballToCheck = forwardIterator.next()
	//				if(ballToCheck.color != newBall.color)
	//					break;
	//				
	//				ballsToRemove << ballToCheck			
	//			}
	//			
	//			if(ballsToRemove.size > 2)
	//				serieses << ball
	//		})  
	//		
	//		if(serieses.isEmpty()){
	//			log.info("CONCURRENTHITCHECK CANT FIND SERIES")
	//			return
	//			
	//		}
	//		def firstHitBall = serieses[-1]
	//		                                                                               
	//		                                                                               
	//		//def ball = template.get([color:lastBall.color])
	//		def ball = template.get([color:firstHitBall.color])
	//		
	//		messageQueue.enqueue(utils.genericMessage("bulletHit"){newMessage -> 
	//			newMessage.source = [ball:ball,position:firstHitBall.pathTraversal.add(10f).position]
	//			newMessage.targets = [firstHitBall]
	//		})
	//		
	//	})
}
