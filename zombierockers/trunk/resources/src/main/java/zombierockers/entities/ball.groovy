package zombierockers.entities

import com.gemserk.componentsengine.messages.UpdateMessage 

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.games.zombierockers.AnimationHelper;


builder.entity("ball-${Math.random()}") {
	
	tags("ball", "nofriction")
	
	property("color",parameters.color ?: utils.color(1,0,0))
	property("direction", utils.vector(1,0))
	property("radius",parameters.radius)
	property("state",parameters.state)
	
	property("pathTraversal", null)
	property("newPathTraversal", null)
	
	property("position", {entity.pathTraversal.position})
	
	property("animation", utils.resources.animation("ballanimation"))
	
	// 2 * pi * radius / totalFramesOfTheAnimation
	property("animationHelper", new AnimationHelper(entity.animation, (float) 2 * Math.PI * parameters.radius / entity.animation.frameCount))
	
	property("direction", {entity.pathTraversal.tangent})
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {entity.animation.currentFrame})
		 propertyRef("color", "color")
		propertyRef("position", "position")
		property("direction", {entity.direction.copy().add(-90)})
		property("size", utils.vector(1f, 1f))
	}
	
	component(utils.custom.components.closureComponent("updatePositionHandler"){ UpdateMessage message ->
		def newPathTraversal = entity.newPathTraversal
		def pathTraversal = entity.pathTraversal
		
		def distance = (float) (newPathTraversal.distanceFromOrigin - pathTraversal.distanceFromOrigin)
		
		// println "DISTANCE: $distance"
		
		entity.animationHelper.add(distance)
		
		entity.pathTraversal = newPathTraversal
	})
	
	component(utils.components.genericComponent(id:"explosionsWhenRemoveBallsHandler", messageId:["explodeBall"]){ message ->
		def ball = entity
		if(!message.balls.contains(ball))
		return
		
		log.info("Exploding ball - ball.id: $ball.id - ball.color: $ball.color")
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(ball))
		
		messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
			newMessage.explosion =EffectFactory.explosionEffect(100, (int) ball.position.x, (int) ball.position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f, ball.color, ball.color) 
		})
		
	})
	
}

