package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.gamestates.GemserkGameState 
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import gemserk.utils.GroovyBootstrapper 

builder.entity("playing") {
	
	child(entity("world"){ parent("dudethatsmybullet.scenes.world",parameters) })
	
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
		}
		mouse {
			press(button:"left",eventId:"raiseShield")
			release(button:"left",eventId:"lowerShield")
		}
	}
	
	component(utils.components.genericComponent(id:"grabscreenshot-leavenodestate", messageId:"leaveNodeState"){ message ->
		def graphics = utils.custom.gameContainer.graphics
		graphics.copyArea(utils.custom.gameStateManager.gameProperties.screenshot, 0, 0);
	})
	
	component(utils.components.genericComponent(id:"enterPauseWhenLostFocus", messageId:"update"){ message ->
		if(!utils.custom.gameStateManager.gameProperties.runningInDebug && !utils.custom.gameContainer.hasFocus())
			messageQueue.enqueue(utils.genericMessage("paused"){})
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("paused"){})
	})
}
