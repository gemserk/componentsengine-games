

package dudethatsmybullet.entities


import com.gemserk.componentsengine.commons.components.BarRendererComponent 
import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.DisablerComponent 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.utils.AngleUtils;
import com.gemserk.games.dudethatsmybullet.GamePredicates 


builder.entity("ship") {
	
	tags("ship","nofriction","hittable")
	
	property("hitpoints",utils.container(400,400))
	
	
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	property("desiredDirection",utils.vector(0,0))
	
	property("radius",16f)
	
	
	property("target",utils.vector(1,0))
	property("bounds",parameters.bounds)
	
	property("isDead",{entity.hitpoints.isEmpty()})
	
	property("rotationValue",0f)
	
	component(new IncrementValueComponent("rotator")) {
		propertyRef("value", "rotationValue")
		property("maxValue", 360f)
		property("increment", 0.2f)
	}
	
	
	component(utils.components.genericComponent(id:"directionToForceComponent", messageId:["update"]){ message ->
		def distanceVector = entity.target.copy().sub(entity.position)
		
		
		if(distanceVector.lengthSquared() > 10f){
			entity."movement.force".add(distanceVector.copy().normalise().scale(0.1f))
		}else {
			entity."movement.velocity" = utils.vector(0,0)
		}
		
	})
	
	component(new SuperMovementComponent("movement")){
		property("maxVelocity", (float)(500/1000))
		propertyRef("position", "position")
		
	}
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", {utils.vector(1,0).add(entity.rotationValue)})
	}
	
	
	component(utils.components.genericComponent(id:"lookAtHandler", messageId:["lookAt"]){ message ->
		def target = utils.vector(message.x,message.y)
		entity.target = target
	})
	
	
	component(utils.components.genericComponent(id:"hithandler", messageId:"hit"){ message ->
		if (message.targets.contains(entity)){
			def bullet = message.source
			
			entity.hitpoints.remove(bullet.damage)
			
			if(entity.hitpoints.isEmpty()){
				messageQueue.enqueue(utils.genericMessage("explosion") { newMessage  ->
					newMessage.explosion =EffectFactory.explosionEffect(100, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f, utils.color(0,0,1,1), utils.color(0,0,1,1))
					newMessage.layer = 1
				})
				messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
			}
		}
	})
	
	
	
	
	property("shieldEnabled",false)
	property("shieldRadius",32f)
	
	
	component(new DisablerComponent(new CircleRenderableComponent("shieldRenderer"))) {
		propertyRef("position", "position")
		propertyRef("radius", "shieldRadius")
		property("lineColor", utils.color(1,1,0,1))
		property("fillColor", utils.color(0,0,0,0))
		property("layer",2)
		propertyRef("enabled","shieldEnabled")
	}
	
	
	
	component(utils.components.genericComponent(id:"raiseShieldHandler", messageId:"raiseShield"){ message ->
		entity.shieldEnabled = true
	})
	
	component(utils.components.genericComponent(id:"lowerShieldHandler", messageId:"lowerShield"){ message ->
		entity.shieldEnabled = false
	})
	
	component(new BarRendererComponent("hitpointsRenderer") ){
		property("position", utils.vector(30,10))
		propertyRef("container", "hitpoints")
		property("width", 44f)
		property("height", 5f)
		property("fullColor", utils.color(0.3f, 0.6f, 0.9f,1))
		property("emptyColor", utils.color(0.9f, 0.1f, 0.1f, 1))
		property("layer", 20)
	}
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
	
	component(new DisablerComponent(new GenericHitComponent("bulletDeflector"))){
		property("targetTag", "bullet")
		property("predicate",{GamePredicates.isNearWithRadius(entity.position, entity.shieldRadius)})
		property("trigger", utils.custom.triggers.closureTrigger { data ->
			def source = data.source
			def targets = data.targets
			def sourcePosition = source.position
			
			targets.each { bullet ->
				def currentVelocity = bullet."movement.velocity"
				def distanceVector = bullet.position.copy().sub(sourcePosition)
				bullet.position = sourcePosition.copy().add(distanceVector.copy().normalise().scale((float)source.shieldRadius + bullet.radius + 1))
				def newAngle = distanceVector.getTheta()
				def newVelocity = utils.vector(currentVelocity.length(),0).add(newAngle)
				bullet."movement.velocity" = newVelocity
				
			}
		})
		propertyRef("enabled","shieldEnabled")
	}
	
}
