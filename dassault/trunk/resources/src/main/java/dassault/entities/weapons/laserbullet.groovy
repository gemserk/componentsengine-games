package dassault.entities.weapons

import com.gemserk.commons.animation.PropertyAnimation;
import com.gemserk.commons.collisions.AABB;
import com.gemserk.commons.collisions.EntityCollidableImpl 
import com.gemserk.commons.slick.geom.ShapeUtils 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.render.ClosureRenderObject;
import com.gemserk.componentsengine.utils.OpenGlUtils;
import com.gemserk.games.dassault.components.AnimationComponent 

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
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
	
	def consumeTime = (int)(parameters.energy / parameters.consumeEnergySpeed)
	
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
	
	component(utils.components.genericComponent(id:"updateLaserPositions", messageId:"update"){ message ->
		
		def owner = entity.owner
		def range = entity.range
		
		def position = owner.position.copy()
		def fireDirection = owner.fireDirection ?: utils.vector(1,0)
		
		def startPosition = position.copy()
		def endPosition = position.copy().add(fireDirection.copy().normalise().scale(range))
		
		entity.startPosition = startPosition
		entity.endPosition = endPosition
		
	})
	
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
		def end = entity.endPosition
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
	
//	component(utils.components.genericComponent(id:"updateCollisionsHandler", messageId:"update"){ message ->
//		
//		def collisionTree = entity.collidable.quadTree
//		
//		if (entity.collidable.outside) {
//			utils.custom.messageQueue.enqueue(utils.genericMessage("bulletDead"){ newMessage ->
//				newMessage.bullet = entity
//			})
//			return
//		}
//		
//		if (collisionTree == null)
//			return
//		
//		def collidables = collisionTree.getCollidables(entity.collidable)
//		
//		collidables = Collections2.filter(collidables, Predicates.and({collidable -> entity != collidable.entity } as Predicate, //
//		{ collidable -> entity.owner != collidable.entity } as Predicate,//
//		{ collidable -> collidable.entity != null } as Predicate,//
//		{ collidable -> entity.collidable.aabb.collide(collidable.aabb) } as Predicate, // 
//		{ collidable -> new ShapeUtils(collidable.entity.bounds).collides(entity.bounds) } as Predicate, //
//		{ collidable -> collidable.entity.tags.contains("collidable") } as Predicate))
//		
//		entity.collisions = new ArrayList(collidables)
//		
//	})
	
}
