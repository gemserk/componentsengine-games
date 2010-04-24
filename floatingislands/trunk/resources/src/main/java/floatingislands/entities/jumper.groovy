package floatingislands.entities;

import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.DisablerComponent;

import com.gemserk.componentsengine.commons.components.SuperMovementComponent;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.utils.OpenGlUtils;
import com.gemserk.games.floatingislands.components.ForceComponent;

builder.entity {
	
	tags("jumper")
	
	property("position", parameters.position)
	
	property("jumpposition", utils.vector(0,0))
	property("jumppower", 0.0f)
	
	property("velocity", utils.vector(0,0))
	property("force", utils.vector(0,0))
	
	property("bounds", utils.rectangle(-5, 8, 10, 16))
	
	property("jumpForce", utils.vector(0,0))
	property("jumpTime", 0)
	
	property("flyingImage", utils.resources.image("jumper_flying"))
	property("normalImage", utils.resources.image("jumper"))
	
	property("image", {
		if (!entity.overIsland)
			return entity.flyingImage
		else
			return entity.normalImage
	})
	
	property("direction", utils.vector(1,0))
	property("overIsland", false)
	
	property("world", {entity.parent})
	
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
	}
	
	component(new DisablerComponent(new ForceComponent("gravityLogic"))) {
		property("enabled", {!entity.overIsland})
		propertyRef("force", "force")
		property("acceleration", utils.vector(0f, 0.10f))
		property("mass", 1.0f)
	}
	
	component(utils.components.genericComponent(id:"startJumpHandler", messageId:"startJump"){ message ->
		if (!entity.overIsland)
			return
		entity.jumppower = 10.0f
	})
	
	component(utils.components.genericComponent(id:"jumpDirectionChangedHandler", messageId:"jumpDirectionChanged"){ message ->
		entity.jumpposition = utils.vector(message.x, message.y)
	})
	
	component(utils.components.genericComponent(id:"jumpHandler", messageId:"jump"){ message ->
		if (entity.jumpTime > 0)
			return 
		
		if (!entity.overIsland)
			return
		// use entity.overIsland instead 
		
		def diff = entity.jumpposition.copy().sub(entity.position)
		def length = diff.length()
		def jumpdirection = diff.normalise()
		
		entity.jumpForce = jumpdirection.scale((float)(entity.jumppower * 0.000002f))
		
		entity.jumpTime = 100
		
		entity.jumppower = 0.0f
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("jumped") { })
	})
	
		component(utils.components.genericComponent(id:"jumpedHandler", messageId:"jumped"){ message -> 
		entity.jetPackSound.loop()	
	})
	
	component(new ComponentFromListOfClosures("forceApplier",[{ UpdateMessage m->
		if (entity.jumpTime <= 0)
			return 
		
		def delta = m.delta
		def jumpForce = entity.jumpForce.copy().scale(delta)
		
		entity.force.add(jumpForce)
		entity.jumpTime -= delta
		
		if (entity.jumpTime <=0 )
			entity.overIsland = false
		
	}]))
	
	component(new ComponentFromListOfClosures("jumppower",[{ UpdateMessage m->
		if (entity.jumppower <= 0)
			return 
		
		def delta = (float)(m.delta)
		
		entity.jumppower = (float) (entity.jumppower + 0.3f * delta)
		
		if (entity.jumppower < 10.0f)
			entity.jumppower = 10.0f
		
		if (entity.jumppower > 200.0f)
			entity.jumppower = 200.0f
	}]))
	
	component(new ComponentFromListOfClosures("islandCollisionLogic",[{ UpdateMessage m->
		
		if (entity.overIsland)
			return
		
		def world = entity.world
		def position = entity.position
		
		def islands = world.getEntities(EntityPredicates.withAllTags("island"));
		
		islands.each { island ->
			def islandBounds = island.bounds
			def islandPosition = island.position
			
			def x = (float)(position.x - islandPosition.x)
			def y = (float)(position.y - islandPosition.y + 15)
			
			// TODO: use entity.bounds instead entity.position
			
			def inside = islandBounds.contains(x, y)
			entity.overIsland = entity.overIsland || inside
			
			if (inside) {
				utils.custom.messageQueue.enqueue(utils.genericMessage("islandReached") { message ->
					message.island = island
				})
			}
		}
		
		
	}]))
	
	component(utils.components.genericComponent(id:"islandReachedHandler", messageId:"islandReached"){ message -> 
		entity.velocity.set(0f,0f)
		entity.force.set(0f,0f)
		entity.jetPackSound.stop()	
	})
	
	component(new ComponentFromListOfClosures("jumpDirectionRenderer",[{ SlickRenderMessage m->
		
		def direction = entity.jumpposition.copy().sub(entity.position).normalise();
		
		def crossPosition = entity.position.copy().add(direction.scale((float)(entity.jumppower * 0.3f)))
		
		SlickCallable.enterSafeBlock();
		OpenGlUtils.renderLine(entity.position, crossPosition, 2.0f, utils.color(1f,1f,0f,0.5f))
		SlickCallable.leaveSafeBlock();
		
	}]))
	
	component(new ComponentFromListOfClosures("outsideScreen", [{ UpdateMessage m ->
		
		if (entity.position.y > 1000f) {
			utils.custom.messageQueue.enqueue(utils.genericMessage("jumperOutsideScreen") { })
			entity.jetPackSound.stop()	
		}
		
	}]))	
	
	
}
