package grapplinghookus.entities

import com.gemserk.commons.animation.interpolators.Vector2fInterpolator;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject;
import com.gemserk.componentsengine.utils.OpenGlUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.SlickCallable;

builder.entity {
	
	tags("grapplinghook")
	
	property("cursor", parameters.cursor)
	
	property("position", parameters.position)
	property("endPosition", parameters.position)
	
	property("target", null)
	
	property("state", "idle")
	property("time", 1000)
	
	component(utils.components.genericComponent(id:"updateTargetOnUpdate", messageId:"update"){ message ->
		def cursorPosition = entity.cursor.position
		
		if (entity.state != "idle")
			return
		
		entity.target = null
		
		def enemies = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("enemy"), // 
				{enemy -> enemy.bounds.contains(cursorPosition.x, cursorPosition.y) } as Predicate))
		
		if (enemies.isEmpty()) 
			return
		
		entity.target = enemies[0]
	})
	
	
	component(utils.components.genericComponent(id:"shootGrapplingHookHandler", messageId:"shootGrapplingHook"){ message ->
		
		if (entity.state != "idle")
			return
		
		def target = entity.target
		
		if (target == null) 
			return
		
		entity.state = "reachingEnemy"
		entity.endPositionInterpolator = new Vector2fInterpolator(entity.time, entity.position, target.position)
		
		log.debug("shooting grappling hook to targeted enemy")
		
	})
	
	component(utils.components.genericComponent(id:"updateUntilItReachesTheEnemy", messageId:"update"){ message ->
		
		// updates the grappling hook until it reaches the enemy
		
		if (entity.state != "reachingEnemy")
			return
		
		def interpolator = entity.endPositionInterpolator
		
		interpolator.update(message.delta)
		entity.endPosition = interpolator.interpolatedValue
		
		if (interpolator.finished) {
			entity.state = "reachingBase"
			entity.endPositionInterpolator = new Vector2fInterpolator(entity.time, entity.endPosition, entity.position)
		}
		
	})
	
	component(utils.components.genericComponent(id:"updateUntilItReachesTheBase", messageId:"update"){ message ->
		
		// updates the grappling hook until it reaches the base with the enemy with it
		
		if (entity.state != "reachingBase")
			return
		
		def interpolator = entity.endPositionInterpolator
		
		interpolator.update(message.delta)
		entity.endPosition = interpolator.interpolatedValue
		
		if (interpolator.finished) {
			entity.state = "idle"
			entity.endPositionInterpolator = null
		}
		
	})
	
	input("inputmapping"){
		mouse {
			press(button:"right", eventId:"shootGrapplingHook")
		}
	}
	
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		if (entity.state == "idle")
			return
		
		def renderer = message.renderer
		
		def startPosition = entity.position
		def endPosition = entity.endPosition
		
		def layer = 6
		
		renderer.enqueue(new ClosureRenderObject(layer,  { Graphics g ->
			
			SlickCallable.enterSafeBlock();
			OpenGlUtils.renderLine(startPosition, endPosition, 3f, utils.color(0f,0f,0f,0.9f))
			SlickCallable.leaveSafeBlock();
			
		}));
		
	})
	
}
