package zombierockers.scenes

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.PathRendererComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.games.zombierockers.TestMessage;

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
		parent("zombierockers.entities.spawner", [path:{entity.parent.path}])
	})
	
	child(entity("limbo"){
		parent("zombierockers.entities.limbo", [path:{entity.parent.path}])
	})
	
	child(entity("cannon"){
		parent("zombierockers.entities.cannon",[bounds:utils.rectangle(20,20,760,560)])
	})
	
	component(new ExplosionComponent("explosions")) {
	}
	
	input("inputmapping"){
		keyboard {
			press(button:"space",eventId:"releaseBalls")
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
