package jylonwars.scenes;

import com.gemserk.componentsengine.gamestates.GemserkGameState 
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import jylonwars.GroovyBootstrapper 


builder.entity("game") {
	
	new GroovyBootstrapper();
	
	child(entity("ship"){
		parent("jylonwars.entities.ship",[position:utils.vector(400,300)])
	})
	
	
	
	input("inputmapping"){
		keyboard {
			hold(button:"left",eventId:"move.left")
			hold(button:"right",eventId:"move.right")
			hold(button:"up",eventId:"move.up")
			hold(button:"down",eventId:"move.down")
			
		}
		mouse {
			move(eventId:"lookAt") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
}
