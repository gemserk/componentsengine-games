package zombierockers.scenes
import java.util.ArrayList;



import org.newdawn.slick.Graphics;


import com.google.common.base.Predicate;


import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.componentsengine.groovy.render.ClosureRenderObject 
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSprite 
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSpritesRenderObject 
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 


builder.entity {
	
	def gameProperties = utils.slick.stateBasedGame.gameProperties
	
	property("bounds", parameters.screenBounds)
	property("ballsQuantity",0)
	property("baseReached",false)
	
	property("level", parameters.level)
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	def offset = 0f
	
	property("path",new Path(utils.svg.loadPoints(entity.level.path, "path")))	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.slick.resources.image(entity.level.background))
		property("color", utils.slick.color(1,1,1,1))
		property("position", utils.slick.vector(400,300))
		property("direction", utils.slick.vector(1,0))
		property("layer", -1000)
	}
	
	
	
	component(utils.components.genericComponent(id:"placeablesRender", messageId:["render"]){ message ->
		
		def renderer = message.renderer
		
		def placeables = entity.level.placeables
		placeables.each { placeable ->
			def position = placeable.position
			def layer = placeable.layer
			def image = utils.slick.resources.image(placeable.image)
			def input = utils.slick.gameContainer.input
			//position = utils.slick.vector(input.mouseX, input.mouseY)
			//println position
			renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
				g.pushTransform()
				g.translate((float) position.x + 5, (float)position.y + 5)
				g.drawImage(image, (float)-(image.getWidth() / 2), (float)-(image.getHeight() / 2))
				g.popTransform()
			}))
		}
	})
	
	component(new ExplosionComponent("explosions")) { }
	
	child(id:"base", template:"zombierockers.entities.base") {
		position = entity.path.points[-1]
		radius = 15f
	}
	
	child(id:"spawner", template:"zombierockers.entities.spawner") { 
		path = entity.path
		ballsQuantity = entity.level.ballsQuantity
		ballDefinitions = entity.level.ballDefinitions
		pathProperties = entity.level.pathProperties
		subPathDefinitions = entity.level.subPathDefinitions
	}
	
	child(id:"limbo", template:"zombierockers.entities.limbo") { path = entity.path }
	
	child(id:"cannon", template:"zombierockers.entities.cannon") {
		bounds=utils.slick.rectangle((float)20+offset,20,(float)760-offset,560)
		ballDefinitions = entity.level.ballDefinitions
		collisionMap = entity.level.collisionMap
		subPathDefinitions = entity.level.subPathDefinitions
	}
	
	child(id:"segmentsManager",template:"zombierockers.scenes.segmentsmanager") {
		baseReached = new ReferenceProperty<Object>("baseReached", entity)
	}
	
	//	child(entity("segmentsManager") {
	//		
	//		def getSortedSegments = { entity ->
	//			def segments = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), {segment-> !segment.isEmpty } as Predicate))
	//			return segments.sort { it.pathTraversal }
	//		}
	//		
	//		component(utils.components.genericComponent(id:"checkSameColorSegmentsHandler", messageId:["checkSameColorSegments"]){ message ->
	//			
	//			def reverseSpeed = -0.3f
	//			
	//			def sortedSegments = getSortedSegments(entity)
	//			
	//			log.info("Segments not empty: $sortedSegments.size")
	//			
	//			sortedSegments.size().times { index ->
	//				if (index == sortedSegments.size() -1 )
	//					return
	//				
	//				def segment = sortedSegments[index]
	//				def nextSegment = sortedSegments[index+1]
	//				def lastBallColor = segment.lastBall.color      
	//				def nextSegmentColor = nextSegment.firstBall.color
	//				if (lastBallColor == nextSegmentColor) {
	//					log.info("SegmentManager detected color coincidence between segments ends segment1.id: $segment.id - segment2.id - $nextSegment.id - colorCoincidence: $lastBallColor")
	//					utils.messageQueue.enqueue(utils.messages.genericMessage("segmentChangeSpeed"){newMessage ->
	//						newMessage.segment = nextSegment
	//						newMessage.speed = reverseSpeed
	//					})
	//				}
	//			}
	//			
	//		})
	//		
	//		component(utils.components.genericComponent(id:"checkFirstSegmentShouldAdvanceHandler", messageId:["checkFirstSegmentSholdAdvance"]){ message ->
	//			
	//			if (entity.parent.baseReached)
	//				return
	//			
	//			def sortedSegments = getSortedSegments(entity)
	//			
	//			log.info("Checking first segment should advance - cantSegments: $sortedSegments.size")
	//			
	//			if (sortedSegments.isEmpty())
	//				return
	//			
	//			def firstSegment = sortedSegments[0]
	//			
	//			if (firstSegment.speed <= 0) {
	//				utils.messageQueue.enqueue(utils.messages.genericMessage("segmentChangeSpeed"){newMessage ->
	//					newMessage.segment = firstSegment
	//					newMessage.speed = 0.04f
	//				})
	//			}
	//			
	//		})
	//		
	//		component(utils.components.genericComponent(id:"destroySegmentHandler", messageId:["destroySegment"]){ message ->
	//			def segment = message.segment 
	//			utils.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(segment))
	//			utils.messageQueue.enqueue(utils.messages.genericMessage("checkFirstSegmentSholdAdvance"){
	//			})
	//		})
	//		
	//		component(utils.components.genericComponent(id:"collisionBetweenSegmentsDetector", messageId:["update"]){ message ->
	//			def sortedSegments = getSortedSegments(entity)
	//			
	//			def collisionFound = false
	//			
	//			sortedSegments.size().times { index ->
	//				if (index == sortedSegments.size() -1 || collisionFound)
	//					return
	//				
	//				def segment = sortedSegments[index]
	//				def nextSegment = sortedSegments[index+1]
	//				
	//				def segmentLastBall = segment.lastBall
	//				def nextSegmentFirstBall = nextSegment.firstBall
	//				
	//				if(segmentLastBall.position.distance(nextSegmentFirstBall.position) < (float)segmentLastBall.radius * 2){
	//					log.info("Collision detected with other segment - masterSegment.id: $segment.id - slaveSegment.id: $nextSegment.id")
	//					
	//					utils.messageQueue.enqueue(utils.messages.genericMessage("mergeSegments"){newMessage ->
	//						newMessage.masterSegment = segment
	//						newMessage.slaveSegment = nextSegment
	//					})
	//					
	//					collisionFound = true
	//				}
	//			}
	//			
	//		})
	//		
	//	})
	
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
		property("trigger",utils.triggers.genericMessage("spawn") {
		})
		propertyRef("timer","startTimer")
	}
	
	
	child(entity("ballsQuantityLAbel"){
		
		parent("gemserk.gui.label", [
		//font:utils.slick.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.slick.vector(740f, 30f),
		fontColor:utils.slick.color(0f,0f,0f,1f),
		bounds:utils.slick.rectangle(-50f, -20f, 100f, 40f),
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
		
		utils.messageQueue.enqueue(utils.messages.genericMessage("gameover"){newMessage ->
			newMessage.win = win
		})
	})
	
	
	property("ballShadowImage", utils.slick.resources.image("ballshadow"))
	
	component(utils.components.genericComponent(id:"ballRenderer", messageId:["render"]){ message ->
		def allBalls = entity.getEntities(Predicates.and(EntityPredicates.withAllTags("ball"), {ball -> ball.alive } as Predicate))
		
		if(allBalls.isEmpty())
			return
		
		def renderer = message.renderer
		def ballShadowImage = entity.ballShadowImage
		
		def level = entity.level
		
		ballsByLayer = allBalls.groupBy {it.layer}
		
		ballsByLayer.each { layer, balls ->
			def alphaMask = level.alphaMasks?.get(layer)
			
			
			AlphaMaskedSpritesRenderObject ballsRenderer = new AlphaMaskedSpritesRenderObject(layer, alphaMask, new ArrayList<AlphaMaskedSprite>(balls.size()));
			AlphaMaskedSpritesRenderObject shadowRenderer = new AlphaMaskedSpritesRenderObject(layer -1, alphaMask, new ArrayList<AlphaMaskedSprite>(balls.size()));
			
			def ballsSprites = ballsRenderer.sprites
			def shadowSprites = shadowRenderer.sprites
			def shadowColor = utils.slick.color(1,1,1,1)
			
			def shadowDisplacement = utils.slick.vector(3,3)
			
			balls.each { ball ->
				
				def image = ball.currentFrame
				def position = ball.position
				def direction = ball.direction
				def color = ball.color
				def size = ball.size
				
				ballsSprites << new AlphaMaskedSprite(image,position,direction,size, color)
				shadowSprites << new AlphaMaskedSprite(ballShadowImage, position.copy().add(shadowDisplacement),direction,size, shadowColor)
			}
			
			renderer.enqueue(ballsRenderer)
			renderer.enqueue(shadowRenderer)
			
		}
		
		
	})
}
