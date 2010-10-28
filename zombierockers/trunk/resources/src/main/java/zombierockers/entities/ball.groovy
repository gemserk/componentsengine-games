
package zombierockers.entities



import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.slick.effects.EffectFactory 
import com.gemserk.games.zombierockers.AnimationHelper;


builder.entity("ball-${Math.random()}") {
	
	tags("ball", "nofriction")
	
	property("type", parameters.definition.type)
	property("color", parameters.definition.color)
	property("animation", utils.slick.resources.animation(parameters.definition.animation))
	
//	property("direction", utils.slick.vector(1,0))
	property("radius", parameters.radius)
	property("finalRadius", parameters.finalRadius ?: parameters.radius)
	property("state",parameters.state)
	
	property("fired", parameters.fired)
	property("isGrownUp", {entity.radius == entity.finalRadius})
	
	property("pathTraversal", null)
	property("newPathTraversal", null)
	
	property("position", {entity.pathTraversal.position})
	
	// property("animation", utils.slick.resources.animation("ballanimation"))
	
	property("animationHelper", new AnimationHelper(entity.animation, (float) 2 * Math.PI * entity.finalRadius / entity.animation.frameCount))
	
	property("direction", {entity.pathTraversal.tangent})
	property("alive",true)
	property("segment",null)
	
	property("size", {
		def size = (float)entity.radius/entity.finalRadius
		return utils.slick.vector(size, size)
	})
	
	property("subPathDefinitions", parameters.subPathDefinitions)
	
	property("layer", 0)
	property("collisionMask", 0)
	
	property("currentFrame",{entity.animation.currentFrame})
	
//	component(new ImageRenderableComponent("imagerenderer")) {
//		propertyRef("image", "currentFrame")
//		propertyRef("color", "color")
//		propertyRef("position", "position")
//		property("direction", {entity.direction.copy().add(-90)})
//		propertyRef("size", "size")
//		propertyRef("layer", "layer")
//	}
	
	component(utils.components.genericComponent(id:"updatePositionHandler", messageId:["update"]){ message ->
		def newPathTraversal = entity.newPathTraversal
		def pathTraversal = entity.pathTraversal
		
		def distance = (float) (newPathTraversal.distanceFromOrigin - pathTraversal.distanceFromOrigin)
		
		// println "DISTANCE: $distance"
		
		entity.animationHelper.add(distance)
		
		entity.pathTraversal = newPathTraversal
		
		def subPathDefinition = entity.subPathDefinitions.getSubPathDefinition(newPathTraversal)
		entity.layer = subPathDefinition.metadata.layer
		entity.collisionMask = subPathDefinition.metadata.collisionMask
	})
	
	component(utils.components.genericComponent(id:"explosionsWhenRemoveBallsHandler", messageId:["explodeBall"]){ message ->
		def ball = entity
		if(!message.balls.contains(ball))
			return
		
		entity.alive = false
		log.info("Exploding ball - ball.id: $ball.id - ball.color: $ball.color")
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(ball))
		
		messageQueue.enqueue(utils.messages.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(100, (int) ball.position.x, (int) ball.position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f, ball.color, ball.color) 
			newMessage.layer = entity.layer+1
		})
		
	})
	
}

