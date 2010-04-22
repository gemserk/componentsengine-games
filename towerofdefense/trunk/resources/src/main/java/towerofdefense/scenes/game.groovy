package towerofdefense.scenes;

import com.gemserk.componentsengine.messages.*;
import towerofdefense.GroovyBootstrapper;
import com.gemserk.componentsengine.commons.components.*;
import com.gemserk.games.towerofdefense.*;
import com.gemserk.games.towerofdefense.components.*;

builder.entity("game") {
	
	def utils = utils
	new GroovyBootstrapper();
	
	def buttonFont = utils.resources.fonts.font([italic:false, bold:true, size:12])
	
	property("gamestate", [paused:false])
	
	child(entity("playing") {
		
		property("gamestate", {entity.parent.gamestate})
		
		component(utils.components.genericComponent(id:"pauseHandler", messageId:"pause") { 
			entity.gamestate.paused=true
		})
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {!entity.gamestate.paused})
			property("exclusions", [SlickRenderMessage.class])
		}
		
		child(entity("world") {
			
			parent("towerofdefense.scenes.world", parameters)
			
		})
	})
	
	child(entity("paused") {
		
		property("gamestate", {entity.parent.gamestate})
		
		component(utils.components.genericComponent(id:"resumeHandler", messageId:"resume") { 
			entity.gamestate.paused=false
		})
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.gamestate.paused})
		}
		
		component(new RectangleRendererComponent("background")) {
			property("position", utils.vector(0, 0))
			property("fillColor", utils.color(0.5f, 0.5f, 0.5f, 0.5f))
			property("rectangle", utils.rectangle(0,0, 800, 600))
		}
		
	})
	
	child(entity("pauseButton"){
		
		property("gamestate", {entity.parent.gamestate})
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {!entity.gamestate.paused})
		}
		
		parent("gemserk.gui.imagebutton", [
				buttonImage:utils.resources.image("pausebutton"),
				buttonImageOver:utils.resources.image("pausebutton"),
				buttonImagePressed:utils.resources.image("pausebutton"),
				onOverSize:1.1f,
				onPressedSize:1.05f,			
				position:utils.vector(750, 540),
				direction:utils.vector(1,0),
				size:utils.vector(36, 40),
				onReleasedTrigger:utils.custom.triggers.genericMessage("pause"){},
				])	
	})
	
	child(entity("resumeButton"){
		
		property("gamestate", {entity.parent.gamestate})
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.gamestate.paused})
		}
		
		parent("gemserk.gui.imagebutton", [
				buttonImage:utils.resources.image("resumebutton"),
				buttonImageOver:utils.resources.image("resumebutton"),
				buttonImagePressed:utils.resources.image("resumebutton"),
				onOverSize:1.1f,
				onPressedSize:1.05f,			
				position:utils.vector(750, 540),
				direction:utils.vector(1,0),
				size:utils.vector(36, 48),
				onReleasedTrigger:utils.custom.triggers.genericMessage("resume"){},
				])	
	})
	
	input("inputmapping"){
		mouse {
			press(button:"left", eventId:"mouse.leftpressed")
			release(button:"left", eventId:"mouse.leftreleased")
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}	
}