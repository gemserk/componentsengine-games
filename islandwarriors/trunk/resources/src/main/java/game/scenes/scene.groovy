package game.scenes
import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage 

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.componentsengine.timers.PeriodicTimer 
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 
import game.GroovyBootstrapper

builder.entity("scene") {
	property("gameState", "playing");
	
	
	child(entity("world"){ parent("game.scenes.world") })
	child(entity("paused"){ parent("game.scenes.paused") })
	child(entity("gameover"){ parent("game.scenes.gameover") })
}
