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
				hold(button:"left",eventId:"move.left")
				hold(button:"right",eventId:"move.right")
				hold(button:"up",eventId:"move.up")
				hold(button:"down",eventId:"move.down")
				hold(button:"a",eventId:"move.left")
				hold(button:"d",eventId:"move.right")
				hold(button:"w",eventId:"move.up")
				hold(button:"s",eventId:"move.down")
			}
		}
}
