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
	
	component(new LabelComponent("fpslabel")){
		property("color", utils.color(0f,0f,0f,1f))
		property("position", utils.vector(50,20))
		property("message", "FPS: {0}")
		property("value",{
			utils.custom.gameContainer.getFPS()
		})
	}
	
	child(entity("world"){ parent("jylonwars.scenes.world") })
	
	child(entity("gameover"){ parent("jylonwars.scenes.gameover") })
	
	child(entity("paused"){ parent("jylonwars.scenes.paused") })
	
	child(entity("highscore"){parent("jylonwars.scenes.highscore") })
}

