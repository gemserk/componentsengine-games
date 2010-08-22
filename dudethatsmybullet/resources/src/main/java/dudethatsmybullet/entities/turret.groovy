package dudethatsmybullet.entities


import com.gemserk.componentsengine.commons.components.BarRendererComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 


builder.entity("turret-${Math.random()}") {
	
	tags("turret","hittable")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	
	property("radius",16f)
	
	property("bulletTemplate",parameters.bulletTemplate)
	property("target",parameters.target)
	property("fireRadius",150f)
	
	property("hitpoints", utils.container(100,100))
	
	property("damage", parameters.damage)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("turret-base"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	
	
	component(new WeaponComponent("shooter")) {
		property("reloadTime", 300)
		propertyRef("position", "position")
		property("shouldFire", {entity.target != null && !entity.target.isDead && entity.target.position.distance(entity.position) < entity.fireRadius})
		
		property("trigger", utils.custom.triggers.closureTrigger { turret ->
			def bulletTemplate = entity.bulletTemplate
			def target = entity.target
			
			def direction = target.position.copy().sub(turret.position).normalise()
			
			
			def bullet = bulletTemplate.get([position:turret.position.copy(),direction:direction, damage: entity.damage])
			
			
			bullet.position.add(direction.copy().scale((float)turret.radius + bullet.radius + 1f))
			
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
		})
	}
	
	
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
	component(utils.components.genericComponent(id:"gameOverHandler", messageId:"gameOver"){ message ->
		entity.target = null
	})
	component(new BarRendererComponent("hitpointsRenderer") ){
		property("position", {entity.position.copy().add(utils.vector(-22f, -25f))})
		propertyRef("container", "hitpoints")
		property("width", 44f)
		property("height", 5f)
		property("fullColor", utils.color(0.3f, 0.6f, 0.9f,1))
		property("emptyColor", utils.color(0.9f, 0.1f, 0.1f, 1))
		property("layer", 20)
	}
}
