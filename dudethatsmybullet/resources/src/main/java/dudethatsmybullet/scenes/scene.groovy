package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.gamestates.GemserkGameState 
import org.newdawn.slick.state.StateBasedGame 
import org.newdawn.slick.state.transition.FadeInTransition 
import org.newdawn.slick.state.transition.FadeOutTransition 
import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	child(entity("playing"){ parent("dudethatsmybullet.scenes.playing",parameters) })
	
}
