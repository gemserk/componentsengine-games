package jylonwars.scenes;
import com.gemserk.componentsengine.messages.GenericMessage;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;


import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.games.jylonwars.data.Data;
import com.gemserk.games.jylonwars.TextField;
import com.gemserk.games.jylonwars.TextFieldComponent 

import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	component(new ComponentFromListOfClosures("statechanger",[{ GenericMessage message ->
		
		def transitions = [gameover:["gameover","highscore"],
		paused:["paused","highscore"],
		resume:["playing"],
		enterscore:["enterscore"]
		]
		
		def transition = transitions[message.id]
		
		if(transition==null)
			return
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("leaveNodeState"){newMessage -> newMessage.message = message})
		utils.custom.messageQueue.enqueue(utils.genericMessage("changeNodeState"){newMessage -> newMessage.states = transition})
		utils.custom.messageQueue.enqueue(utils.genericMessage("enterNodeState"){newMessage -> newMessage.message = message})
		
	}]))
	
	child(entity("playing"){ 
		parent("jylonwars.scenes.stateBasedNode",[enabled:true,exclusions:[SlickRenderMessage.class]])
		parent("jylonwars.scenes.world") 
	})
	
	child(entity("gameover"){ 
		parent("jylonwars.scenes.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.gameover")
	})
	
	child(entity("paused"){ 
		parent("jylonwars.scenes.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.paused")
	})
	
	child(entity("highscore"){
		parent("jylonwars.scenes.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.highscore")
	})
	
	child(entity("enterscore"){
		parent("jylonwars.scenes.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.enterscore")
	})
}

