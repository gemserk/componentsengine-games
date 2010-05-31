package zombierockers.scenes
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.PathRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.games.zombierockers.TestMessage;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 

builder.entity {
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("ballsQuantity",0)
	
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
	
	property("path",new Path([utils.vector(-40,200),utils.vector(-20,200),utils.vector(0,200),utils.vector(160,200), utils.vector(240,80),utils.vector(260,70),utils.vector(280,80), utils.vector(440,410),utils.vector(460,420),utils.vector(480,410), utils.vector(560,200), utils.vector(760,200)]))	
	
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
	
	child(entity("base"){
		parent("zombierockers.entities.base", [position:{entity.parent.path.points[-1]},radius:15f])
	})
	
	child(entity("spawner"){
		parent("zombierockers.entities.spawner", [path:{entity.parent.path}])
	})
	
	child(entity("limbo"){
		parent("zombierockers.entities.limbo", [path:{entity.parent.path}])
	})
	
	child(entity("cannon"){
		parent("zombierockers.entities.cannon",[bounds:utils.rectangle(20,20,760,560)])
	})
	
	child(entity("segmentsManager") {
		component(utils.components.genericComponent(id:"checkSameColorSegmentsHandler", messageId:["checkSameColorSegments"]){ message ->
			
			def segments = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), {segment-> !segment.isEmpty} as Predicate))
			def sortedSegments = segments.sort { it.pathTraversal }
			
			sortedSegments.size().times { index ->
				if (index == sortedSegments.size() -1 )
					return
				
				def segment = sortedSegments[index]
				def nextSegment = sortedSegments[index+1]
				
				if (segment.lastBall.color == nextSegment.firstBall.color) {
					utils.custom.messageQueue.enqueue(utils.genericMessage("engageReverse"){newMessage ->
						newMessage.segment = nextSegment
						newMessage.speed = -0.30f
					})
				}
			}
			
		})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"s",eventId:"spawn")
			press(button:"d",eventId:"dumpDebug")
			hold(button:"l",eventId:"messageLoad")
		}
	}
	
	component(utils.components.genericComponent(id:"dumpDebugHandler", messageId:"dumpDebug"){ message ->
		Entity.times.entrySet().sort({it.count }).each { entry ->  println "$entry.element - $entry.count" }
	} )   
	
	component(utils.components.genericComponent(id:"generateMessageLoad", messageId:["messageLoad"]){ message ->
		def messageQueue = messageQueue
		100.times {
			messageQueue.enqueue(new TestMessage())
		}
		println "Generating Load - ${messageQueue.messages.size()}"
	})
	
	
	property("startTimer",new CountDownTimer(2000))
	entity.startTimer.reset()
	component(new TimerComponent("startTimer")){
		property("trigger",utils.custom.triggers.genericMessage("spawn") {})
		propertyRef("timer","startTimer")
	}
	
	
	child(entity("ballsQuantityLAbel"){
		
		parent("gemserk.gui.label", [
		//font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.vector(60f, 40f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("message", {"Balls: ${entity.parent.ballsQuantity}".toString() })
	})
	
}
