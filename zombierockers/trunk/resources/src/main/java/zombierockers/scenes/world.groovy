package zombierockers.scenes
import java.awt.Shape 
import java.awt.geom.PathIterator;
import java.util.List;




import com.google.common.base.Predicate;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;


builder.entity {
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("ballsQuantity",0)
	property("baseReached",false)
	
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
	
	def level = [ballsQuantity:60, ballDefinitions:[
			0:[type:0, animation:"ballanimation_white", color:utils.color(1,0,0)],
			1:[type:1, animation:"ballanimation_white", color:utils.color(0,0,1)],
			2:[type:2, animation:"ballanimation_white", color:utils.color(0,1,0)]
			]]
	
	def offset = 0f
	
	SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(Thread.currentThread().getContextClassLoader().getResource("levels/level01/path.svg").toURI());
	SVGElement element = diagram.getElement("path");
	List vector = element.getPath(null);
	com.kitfox.svg.Path pathSVG = (com.kitfox.svg.Path) vector.get(1);
	Shape shape = pathSVG.getShape();
	PathIterator pathIterator = shape.getPathIterator(null, 0.001d);
	float[] coords = new float[2];
	def pointsInPath = [utils.vector((float)-90+offset,45),utils.vector((float)-60+offset,45)]
	def loadedPoints = []
	while (!pathIterator.isDone()) {
		int type = pathIterator.currentSegment(coords);
		loadedPoints << utils.vector(coords[0],coords[1]);
		pathIterator.next();
	}
	pointsInPath.addAll(loadedPoints)
	
	
	//property("path",new Path([utils.vector((float)-90+offset,200),utils.vector((float)-60+offset,200),utils.vector((float)-30+offset,200),utils.vector(160,200), utils.vector(240,80),utils.vector(260,70),utils.vector(280,80), utils.vector(440,410),utils.vector(460,420),utils.vector(480,410), utils.vector(560,200), utils.vector(760,200)]))	
	property("path",new Path(pointsInPath))	
	
	child(entity("path"){
		
		//		component(new PathRendererComponent("pathrendererBorders")){
		//			property("lineColor", utils.color(0.2f, 0.2f, 0.7f, 1.0f))
		//			property("lineWidth", 5.0f)
		//			property("path", {entity.parent.path})		
		//		}
		//		component(new PathRendererComponent("pathrendererFill")){
		//			property("lineColor", utils.color(0.5f, 0.5f, 1f, 1.0f))
		//			property("lineWidth", 30.0f)
		//			property("path", {entity.parent.path})		
		//		}
		
	})
	
	child(id:"base", template:"zombierockers.entities.base") {
		position = entity.path.points[-1]
		radius = 15f
	}
	
	child(id:"spawner", template:"zombierockers.entities.spawner") { 
		path = entity.path
		ballsQuantity = level.ballsQuantity
		ballDefinitions = level.ballDefinitions
	}
	
	child(id:"limbo", template:"zombierockers.entities.limbo") { path = entity.path }
	
	child(id:"cannon", template:"zombierockers.entities.cannon") {
		bounds=utils.rectangle((float)20+offset,20,(float)760-offset,560)
		ballDefinitions = level.ballDefinitions
	}
	
	child(entity("segmentsManager") {
		
		def getSortedSegments = { entity ->
			def segments = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), {segment-> !segment.isEmpty} as Predicate))
			return segments.sort { it.pathTraversal }
		}
		
		component(utils.components.genericComponent(id:"checkSameColorSegmentsHandler", messageId:["checkSameColorSegments"]){ message ->
			
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
						newMessage.speed = -0.30f
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
			messageQueue.enqueue(utils.genericMessage("checkFirstSegmentSholdAdvance"){	})
		})
		
		component(utils.custom.components.closureComponent("collisionBetweenSegmentsDetector"){ UpdateMessage message ->
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
		property("trigger",utils.custom.triggers.genericMessage("spawn") {})
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
	
	component(utils.custom.components.closureComponent("gameOverChecker"){ UpdateMessage message ->
		
		def limbosNotDone = entity.getEntities(Predicates.and(EntityPredicates.withAllTags("limbo"), {limbo -> !limbo.done} as Predicate))
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
