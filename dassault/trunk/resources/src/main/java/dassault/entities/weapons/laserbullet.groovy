package dassault.entities.weapons

import com.gemserk.commons.animation.PropertyAnimation;
import com.gemserk.commons.collisions.AABB;
import com.gemserk.commons.collisions.EntityCollidableImpl 
import com.gemserk.commons.slick.geom.ShapeUtils 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.render.ClosureRenderObject;
import com.gemserk.componentsengine.utils.OpenGlUtils;
import com.gemserk.games.dassault.components.AnimationComponent 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 
import com.google.common.collect.Collections2 

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.GeomUtil.HitResult;
import org.newdawn.slick.opengl.SlickCallable;


builder.entity {
	
	tags("bullet", "laserbullet")
	
	property("startPosition", utils.vector(0,0))
	property("endPosition", utils.vector(0,0))
	
	property("player", parameters.player)
	property("owner", parameters.owner)
	
	property("range", parameters.range)
	property("energy", parameters.energy)
	property("consumeEnergySpeed", parameters.consumeEnergySpeed)
	
	property("width", 0f)
	property("color", utils.color(0,0,0,0f))
	
	property("damage", parameters.damage)
	
	def consumeTime = (int)(parameters.energy / parameters.consumeEnergySpeed)
	
	property("damagePerTime", (float) parameters.damage / consumeTime)
	
	PropertyAnimation widthAnimation = new PropertyAnimation("width")
	
	widthAnimation.addKeyFrame (0, 0f)
	widthAnimation.addKeyFrame (((int)consumeTime/2), 10f)
	widthAnimation.addKeyFrame (consumeTime, 0f)
	
	def playerColor = parameters.player.color
	
	PropertyAnimation colorAnimation = new PropertyAnimation("color")
	
	colorAnimation.addKeyFrame (0, utils.color(playerColor.r, playerColor.g, playerColor.b, 0.0f))
	colorAnimation.addKeyFrame (((int)consumeTime * 0.5f), utils.color(playerColor.r, playerColor.g, playerColor.b, 1.0f))
	colorAnimation.addKeyFrame (consumeTime, utils.color(playerColor.r, playerColor.g, playerColor.b, 0.0f))
	
	def animations = [fire:[widthAnimation, colorAnimation]]
	
	component(new AnimationComponent("laserAnimation") ) {
		property("current", "fire")
		property("animations", animations)
	}
	
	component(utils.components.genericComponent(id:"updateEnergyLeft", messageId:"update"){ message ->
		def delta = message.delta
		def energyLeft = entity.energy
		def consumeEnergySpeed = entity.consumeEnergySpeed
		
		def energyConsumed = (float) consumeEnergySpeed * delta
		entity.energy = energyLeft - energyConsumed
		
		if (entity.energy <= 0) {
			utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
				newMessage.bullet = entity
			})
		}
	})
	
	component(utils.components.genericComponent(id:"removeWhenBulletDead", messageId:"bulletDead"){ message ->
		if (entity != message.bullet)
			return
		entity.collidable.remove()
		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	})
	
	component(utils.components.genericComponent(id:"laserBulletRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def layer = -5
		def color = entity.color
		
		def start = entity.startPosition
		def end = entity.hitPosition ?: entity.endPosition
		def width = entity.width 
		
		renderer.enqueue(new ClosureRenderObject(layer, { Graphics g ->
			SlickCallable.enterSafeBlock();
			
			OpenGlUtils.renderLine(start, end, width, color)
			
			SlickCallable.leaveSafeBlock();
		}))
		
	})
	
	property("collisionDetected", {!entity.collisions.isEmpty()})
	property("bounds", utils.rectangle(0, 0, 0, 0))
	property("collisions", [])
	property("collidable", new EntityCollidableImpl(entity, new AABB(0,0,0,0) ))
	
	component(utils.components.genericComponent(id:"updateBoundsHandler", messageId:"update"){ message ->
		def startPosition = entity.startPosition
		def endPosition = entity.endPosition 
		
		def bounds = new Line(startPosition.x, startPosition.y, endPosition.x, endPosition.y)
		entity.bounds = bounds
		
		entity.collidable.entity = entity
		entity.collidable.aabb = new ShapeUtils(bounds).getAABB()
		entity.collidable.update()
	})
	
	component(utils.components.genericComponent(id:"updateCollisionsHandler", messageId:"update"){ message ->
		
		def collisionTree = entity.collidable.quadTree
		
		if (entity.collidable.outside) {
			// should never be outside now we are limiting the endPoint...
			return
		}
		
		if (collisionTree == null)
			return
		
		def collidables = collisionTree.getCollidables(entity.collidable)
		
		collidables = Collections2.filter(collidables, Predicates.and({collidable -> entity != collidable.entity } as Predicate, //
		{ collidable -> entity.owner != collidable.entity } as Predicate,//
		{ collidable -> collidable.entity != null } as Predicate,//
		{ collidable -> entity.collidable.aabb.collide(collidable.aabb) } as Predicate, // 
		{ collidable -> new ShapeUtils(collidable.entity.bounds).collides(entity.bounds) } as Predicate, //
		{ collidable -> collidable.entity.tags.contains("collidable") } as Predicate))
		
		entity.collisions = new ArrayList(collidables)
		
	})
	
	component(utils.components.genericComponent(id:"resizeWhenCollisionWithObstacleDetected", messageId:"update"){ message ->
		if (!entity.collisionDetected) {
			entity.hitPosition = null
			return
		}
		
		def collisions = entity.collisions
		
		collisions.each { collidable ->
			
			def collidableEntity = collidable.entity
			
			if (collidableEntity.tags.contains("obstacle")) {
				HitResult hit = new GeomUtil().intersect(collidableEntity.bounds, entity.bounds)
				if (hit != null) {
					entity.hitPosition = hit.pt.copy()
				}
			}
			
		}
		
	})
	
	component(utils.components.genericComponent(id:"damageWhenCollisionDetection", messageId:"update"){ message ->
		
		def collisions = entity.collisions
		def delta = message.delta
		
		def damage = (float) entity.damagePerTime * message.delta 
		
		collisions.each { collidable ->
			
			def collidableEntity = collidable.entity
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("collisionDetected"){ newMessage ->
				newMessage.bullet = entity
				newMessage.target = collidableEntity
				newMessage.damage = damage
			})
		}
		
	})
	
	component(utils.components.genericComponent(id:"explosionsWhenHittingObstacle", messageId:"update"){ message ->
		
		if (entity.hitPosition == null)
			return
		
		def position = entity.hitPosition
		
		def startColor = utils.color(playerColor.r, playerColor.g, playerColor.b, 1.0f)
		def endColor = utils.color(playerColor.r, playerColor.g, playerColor.b, 0.3f)
		
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(2, (int) position.x, (int) position.y, 0f, 360f, 400, 5.0f, 20f, 60f, 1f, startColor, endColor) 
			newMessage.layer = 1
		})
		
		
	})
	
	component(utils.components.genericComponent(id:"updateLaserPositions", messageId:"update"){ message ->
		
		def owner = entity.owner
		def range = entity.range
		
		def position = owner.position.copy()
		def fireDirection = owner.fireDirection ?: utils.vector(1,0)
		
		def startPosition = position.copy()
		def endPosition = position.copy().add(fireDirection.copy().normalise().scale(range))
		
		// limit end position to be inside the quadtree.
		
		// TODO: use the limits based on the collisiontree
		def limits = [new Line(-1000, -1000, -1000, 1000), // 
				new Line(1000, -1000, 1000, 1000), // 
				new Line(-1000, -1000, 1000, -1000), // 
				new Line(-1000, 1000, 1000, 1000)]
		
		def fireLine = new Line(startPosition, endPosition)
		
		limits.each { limit ->
			HitResult hit = new GeomUtil().intersect(fireLine, limit)
			if (hit != null) {
				endPosition.x = hit.pt.x
				endPosition.y = hit.pt.y
				entity.hitPosition = endPosition.copy()
			}
		}
		
		entity.startPosition = startPosition
		entity.endPosition = endPosition
	})
	
}
