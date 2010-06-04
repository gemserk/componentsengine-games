
package zombierockers.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 


builder.entity("ball-${Math.random()}") {
	
	tags("ball", "nofriction")
	
	property("color",parameters.color ?: utils.color(1,0,0))
	property("direction", utils.vector(1,0))
	property("radius",parameters.radius)
	property("state",parameters.state)
	
	property("pathTraversal", null)
	property("position", {entity.pathTraversal.position})
	
	component(new CircleRenderableComponent("circlerenderer")) {
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("lineColor", utils.color(0,0,0,1))
		property("fillColor", parameters.color)
	}
	
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

