package jylonwars.scenes;

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

import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	
	child(entity("world"){ 
		parent("jylonwars.scenes.stateBasedNode",[enabled:{entity.parent.gameState == "playing"},exclusions:[SlickRenderMessage.class]])
		parent("jylonwars.scenes.world") 
		
	})
	
	child(entity("gameover"){ 
		parent("jylonwars.scenes.stateBasedNode",[enabled:{entity.parent.gameState == "gameover"}])
		parent("jylonwars.scenes.gameover")
	})
	
	child(entity("paused"){ 
		parent("jylonwars.scenes.stateBasedNode",[enabled:{entity.parent.gameState == "paused"}])
		parent("jylonwars.scenes.paused")
	})
	
	child(entity("highscore"){
		parent("jylonwars.scenes.stateBasedNode",[enabled:{entity.parent.gameState != "playing"}])
		parent("jylonwars.scenes.highscore")
	})
}

