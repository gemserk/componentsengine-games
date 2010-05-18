package zombierockers.scenes;

import com.gemserk.componentsengine.commons.components.states.NodeStateTransitionManagerComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import gemserk.utils.GroovyBootstrapper 

builder.entity("game") { 
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	component(new NodeStateTransitionManagerComponent("stateChanger")){
		property("transitions",[
		gameover:["gameover","highscore"],
		paused:["paused","highscore"],
		resume:["playing"],
		enterscore:["enterscore"]
		])
	}

	child(entity("playing"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false,exclusions:[SlickRenderMessage.class]])
		parent("zombierockers.scenes.playing") 
	})
	
	child(entity("gameover"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("zombierockers.scenes.gameover")
	})
	
	child(entity("paused"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("zombierockers.scenes.paused")
	})
	
	utils.custom.messageQueue.enqueue(utils.genericMessage("resume"){})	
}