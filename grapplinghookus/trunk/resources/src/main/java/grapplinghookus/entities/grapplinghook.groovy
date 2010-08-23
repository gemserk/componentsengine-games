package grapplinghookus.entities

import org.newdawn.slick.geom.Vector2f;
import com.gemserk.commons.animation.interpolators.Vector2fInterpolator;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.render.ClosureRenderObject;
import com.gemserk.componentsengine.utils.OpenGlUtils;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.SlickCallable;

builder.entity {
	
	tags("grapplinghook")
	
	property("base", parameters.base)
	
	property("cursor", parameters.cursor)
	
	property("position", parameters.position)
	property("endPosition", parameters.position)
	
	property("target", null)
	
	property("state", "idle")
	property("time", 1000)
	
	component(utils.components.genericComponent(id:"grapplingHookShootedHandler", messageId:"grapplingHookShooted"){ message ->
		if (message.grapplinghook != entity)
			return
		
		if (entity.state != "idle")
			return
		
		entity.state = "reachingEnemy"
		entity.target = message.targetEnemy
		entity.target.targeted = true
		
		entity.timeToReach = entity.time
	})
	
	// time is float between 0 and 1
	def interpolate = { Vector2f a, Vector2f b, float time -> 
		def x = (float) (a.x * time + b.x * (1-time))
		def y = (float) (a.y * time + b.y * (1-time))
		return new Vector2f(x,y)
	}	
	
	component(utils.components.genericComponent(id:"updateUntilItReachesTheEnemy", messageId:"update"){ message ->
		
		// updates the grappling hook until it reaches the enemy
		
		if (entity.state != "reachingEnemy")
			return
		
		float w = (float)( entity.timeToReach / entity.time)
		entity.endPosition = interpolate(entity.position, entity.target.position, w)
		entity.timeToReach = entity.timeToReach - message.delta
		
		if (entity.timeToReach <= 0) {
			entity.state = "reachingBase"
			entity.endPositionInterpolator = new Vector2fInterpolator(entity.time, entity.endPosition, entity.position)
			
			def grapplinghook = entity
			def targetedEnemy = entity.target
			def base = entity.base
			
			entity.trappedEnemy = entity("trappedEnemy-${Math.random()}".toString()) {
				parent("grapplinghookus.entities.trappedenemy", [
				grapplinghook:grapplinghook, 
				enemy:targetedEnemy,
				base:base])
			}
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("grapplingHookEnemyReached") { newMessage ->
				newMessage.grapplinghook = entity
				newMessage.enemy = targetedEnemy
			}) 
			
			utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(targetedEnemy))
			utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(entity.trappedEnemy, entity))
			
			// send message to enemy to set him as it is being absorbed
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
			if (entity.target != null)
				entity.state = "waitingToShoot"
			else 
				entity.state = "idle"
			entity.endPositionInterpolator = null
		}
		
	})
	
	component(utils.components.genericComponent(id:"enemyWasShootedHandler", messageId:"enemyWasShooted"){ message ->
		if (message.grapplinghook != entity)
			return
		
		log.debug("grappling hook is now free: grapplinghook.id - $entity.id")
		
		entity.state = "idle"
		entity.trappedEnemy = null
		entity.target = null
	})
	
	component(utils.components.genericComponent(id:"removeTargetWhenEnemyKilled", messageId:"enemyKilled"){ message ->
		
		if (entity.state!="reachingEnemy")
			return
		
		def enemy = message.enemy
		
		if (entity.target == enemy) {
			
			entity.state = "reachingBase"
			entity.endPositionInterpolator = new Vector2fInterpolator(entity.time, entity.endPosition, entity.position)
			
			entity.target = null
			
		}
		
	})
	
	component(utils.components.genericComponent(id:"renderer", messageId:"render"){ message ->
		
		if (entity.state == "idle")
			return
		
		if (entity.state == "waitingToShoot")
			return
		
		def renderer = message.renderer
		
		def startPosition = entity.position
		def endPosition = entity.endPosition
		
		def layer = 6
		
		renderer.enqueue(new ClosureRenderObject(layer,  { Graphics g ->
			
			SlickCallable.enterSafeBlock();
			OpenGlUtils.renderLine(startPosition, endPosition, 3f, utils.color(1f,1f,1f,0.9f))
			SlickCallable.leaveSafeBlock();
			
		}));
		
	})
	
}
