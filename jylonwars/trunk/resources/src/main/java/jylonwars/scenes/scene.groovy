package jylonwars.scenes;

import com.gemserk.componentsengine.commons.components.states.NodeStateTransitionManagerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage;

import gemserk.utils.GroovyBootstrapper 


builder.entity("game") { 
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	component(new NodeStateTransitionManagerComponent("stateChanger")){
		property("transitions",[gameover:["gameover","highscore"],
		paused:["paused","highscore"],
		resume:["playing"],
		enterscore:["enterscore"]
		])
	}

	child(entity("playing"){ 
		parent("gemserk.states.stateBasedNode",[enabled:true,exclusions:[SlickRenderMessage.class]])
		parent("jylonwars.scenes.world") 
	})
	
	child(entity("gameover"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.gameover")
	})
	
	child(entity("paused"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.paused")
	})
	
	child(entity("highscore"){
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.highscore")
	})
	
	child(entity("enterscore"){
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("jylonwars.scenes.enterscore")
	})
}

