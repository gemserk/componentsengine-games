
package floatingislands.entities;

import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.DisablerComponent;

import com.gemserk.componentsengine.commons.components.SuperMovementComponent;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.games.floatingislands.components.ForceComponent;
import com.gemserk.games.floatingislands.components.RenderUtils;

import static org.lwjgl.opengl.GL11.*;

builder.entity {
	
	tags("jumper")
	
	property("position", parameters.position)
	
	property("jumpDirection", utils.vector(0,0))
	property("jumpEnabled", true)
	property("minAngle", parameters.minAngle)
	property("maxAngle", parameters.maxAngle)
	
	property("jumppower", 0.0f)
	property("maxJumpPower", 300.0f)
	
	property("velocity", utils.vector(0,0))
	property("force", utils.vector(0,0))
	
	property("bounds", utils.rectangle(-5, 8, 10, 16))
	
	property("jumpForce", utils.vector(0,0))
	property("jumpTime", 0)
	
	property("flyingImageRight", utils.resources.image("jumper_flying"))
	property("normalImageRight", utils.resources.image("jumper"))
	
	property("flyingImageLeft", utils.resources.image("jumper_flying").getFlippedCopy(true, false))
	property("normalImageLeft", utils.resources.image("jumper").getFlippedCopy(true, false))
	
	property("frictionFactor", {
		if (entity.overIsland)
			return 0.01f
		else
			return 0.0005f
		
	})
	
	property("normalImage", {
		if (entity.velocity.x < 0)
			return entity.normalImageLeft
		else 
			return entity.normalImageRight
	})
	
	property("flyingImage", {
		if (entity.velocity.x < 0)
			return entity.flyingImageLeft
		else 
			return entity.flyingImageRight
	})
	
	property("image", {
		if (!entity.overIsland) {
			return entity.flyingImage
		}
		else {
			return entity.normalImage
		}
	})
	
	property("direction", utils.vector(1,0))
	property("overIsland", false)
	property("currentIsland", null)
	
	property("world", {entity.parent })
	
	property("jetPackSound", utils.resources.sounds.sound("jetpack"))
	
	component(new ImageRenderableComponent("imageRender")) {
		propertyRef("image", "image")
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	component(new SuperMovementComponent("movementLogic")) {
		propertyRef("position", "position")
		propertyRef("velocity", "velocity")
		propertyRef("force", "force")
		property("maxVelocity", 10.0f)
		propertyRef("frictionFactor", "frictionFactor")
	}
	
	component(new DisablerComponent(new ForceComponent("gravityLogic"))) {
		property("enabled", {!entity.overIsland })
		propertyRef("force", "force")
		property("acceleration", utils.vector(0f, 0.10f))
		property("mass", 1.0f)
	}
	
	property("charging", false)
	property("chargePower", 0.2f)
	
	component(utils.components.genericComponent(id:"chargeHandler", messageId:"charge"){ message ->
		if (!entity.overIsland)
			return
		entity.charging = true
		entity.chargePower = 0.2f
	})
	
	component(new ComponentFromListOfClosures("jumppower",[{ UpdateMessage m->
		
		if (!entity.charging)
			return
		
		def delta = (float)(m.delta)
		
		entity.jumppower = (float) (entity.jumppower + entity.chargePower * delta)
		
		if (entity.jumppower < 10.0f) {
			entity.jumppower = 10.0f
			entity.chargePower = -entity.chargePower
		}
		
		if (entity.jumppower > entity.maxJumpPower) {
			entity.jumppower = entity.maxJumpPower
			entity.chargePower = -entity.chargePower
		}
	}]))
	
	component(utils.components.genericComponent(id:"jumpDirectionChangedHandler", messageId:"jumpDirectionChanged"){ message ->
		entity.jumpDirection = utils.vector(message.x, message.y).sub(entity.position).normalise()
		
		def angle = 360f - entity.jumpDirection.theta
		
		if (angle < entity.minAngle || angle > entity.maxAngle) {
			entity.jumpEnabled = false
			return
		}
		
		entity.jumpEnabled = true
	})
	
	component(utils.components.genericComponent(id:"jumpHandler", messageId:"jump"){ message ->
		
		if (!entity.jumpEnabled) {
			entity.charging = false
			entity.jumppower = 0.0f
			return
		}
	
		if(!entity.charging)
			return
		
		println "jumpPower: $entity.jumppower"
		
		def jumpDirection = entity.jumpDirection
		
		entity.jumpForce = jumpDirection.copy().scale((float)(entity.jumppower * 0.0000025f))
		entity.jumpTime = 100
		
		entity.charging = false
		entity.jumppower = 0.0f
		
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("jumped") {
		})
	})
	
	component(utils.components.genericComponent(id:"jumpedHandler", messageId:"jumped"){ message -> 
		entity.jetPackSound.loop()	
		entity.currentIsland = null
	})
	
	component(new ComponentFromListOfClosures("forceApplier",[{ UpdateMessage m->
		if (entity.jumpTime <= 0)
			return 
		
		def delta = m.delta
		
		entity.jumpTime -= delta		
		def diff = delta
		
		if (entity.jumpTime < 0)
			diff += entity.jumpTime
		
		println "delta = $delta, remainingTime = $entity.jumpTime, diff = $diff"
		
		def jumpForce = entity.jumpForce.copy().scale(diff)
		entity.force.add(jumpForce)
		
		if (entity.jumpTime <=0 )
			entity.overIsland = false
		
	}]))
	
	
	
	component(new ComponentFromListOfClosures("islandCollisionLogic",[{ UpdateMessage m->
		
		// I am over an island right now...
		if (entity.overIsland ) {
			
			// entity.lastPositionIslandX = island.position.x - entity.position.x
			if (entity.currentIsland) {
				
				def island = entity.currentIsland
				
				entity.position.y = (float)(island.position.y + island.bounds.minY - island.bounds.height + 5)
				entity.position.x += island.position.x - entity.lastPositionIslandX
				
				entity.lastPositionIslandX = island.position.x
			}
			
			return
		}
		
		// I am going up here...
		if (entity.velocity.y < 0) 
			return
		
		def world = entity.world
		def position = entity.position
		
		def islands = world.getEntities(EntityPredicates.withAllTags("island"));
		
		def overIsland = false
		
		islands.each { island ->
			
			if (overIsland) 
				return
			
			def islandBounds = island.bounds
			def islandPosition = island.position
			
			def diffy = 15
			
			def x1 = (float)(position.x - islandPosition.x)
			def y1 = (float)(position.y - islandPosition.y + diffy)
			
			// TODO: use entity.bounds instead entity.position
			
			def inside = islandBounds.contains(x1, y1)
			overIsland = overIsland || inside
			
			if (inside) {
				utils.custom.messageQueue.enqueue(utils.genericMessage("islandReached") { message ->
					message.island = island
				})
			}
		}
		
		if (!overIsland) {
			entity.overIsland = false
			entity.currentIsland=null
		}
		
		
	}]))
	
	component(new ComponentFromListOfClosures("currentIslandCollision",[{ UpdateMessage m->
		
		if (entity.currentIsland == null) {
			return
		}
		
		def currentIsland = entity.currentIsland
		def currentPosition = entity.position
		def islandPosition = currentIsland.position
		def islandBounds = currentIsland.bounds
		
		def x = (float)(currentPosition.x - islandPosition.x)
		
		if (x < (islandBounds.minX) || x > (islandBounds.maxX)) {
			entity.overIsland = false
			entity.currentIsland=null		
		}
		
		
	}]))
	
	component(utils.components.genericComponent(id:"islandReachedHandler", messageId:"islandReached"){ message ->
		def island = message.island
		
		entity.lastPositionIslandX = island.position.x
		
		entity.position.y = (float)(island.position.y + island.bounds.minY - island.bounds.height + 5)
		entity.velocity.y = 0.0f
		entity.force.set(0f,0f)
		entity.jetPackSound.stop()	
		
		entity.overIsland = true
		entity.currentIsland=island
	})
	
	component(new ComponentFromListOfClosures("jumpDirectionRenderer",[{ SlickRenderMessage m->
		
		if (!entity.charging)
			return
	
		def direction = entity.jumpDirection.copy();
		
		def crossPosition = entity.position.copy().add(direction.scale((float)(entity.jumppower * 0.3f)))
		def calpha = (float)(entity.jumppower / entity.maxJumpPower * 0.8f) + 0.2f
		
		def startColor = utils.color(0f,1f,0.0f,0.4f)
		def endColor = utils.color(calpha,(float)(1f - calpha),0.0f,calpha)
		
		if (!entity.jumpEnabled) {
			startColor = utils.color(0f,0f,0.0f,0.1f)
			endColor = utils.color(0,0,0.0f,0.3f)
		}
		
		SlickCallable.enterSafeBlock();
		RenderUtils.renderArrow(entity.position, crossPosition, 3.0f, 10.0f, startColor, endColor)
		// OpenGlUtils.renderLine(entity.position, crossPosition, 3.0f, utils.color(1f,0.7f,0.2f,0.9f))
		SlickCallable.leaveSafeBlock();
		
	}]))
	
	component(new ComponentFromListOfClosures("outsideScreen", [{ UpdateMessage m ->
		def outside = entity.outsideScreen ?: false
		
		if (outside)
			return
		
		if (entity.position.y > 700f) {
			entity.jetPackSound.stop()
			utils.custom.messageQueue.enqueue(utils.genericMessage("jumperOutsideScreen") {
			})
			entity.outsideScreen = true
		}
		
	}]))	
	
	
}
