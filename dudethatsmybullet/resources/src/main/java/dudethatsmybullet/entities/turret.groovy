package dudethatsmybullet.entities


import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 


builder.entity("turret-${Math.random()}") {
	
	tags("turret","hittable")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	
	property("radius",20)
	
	property("bulletTemplate",parameters.bulletTemplate)
	property("target",parameters.target)
	property("fireRadius",100f)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("turret-base"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	
	
	component(new WeaponComponent("shooter")) {
		property("reloadTime", 300)
		propertyRef("position", "position")
		property("shouldFire", {entity.target.position.distance(entity.position) < entity.fireRadius})
		
		property("trigger", utils.custom.triggers.closureTrigger { turret ->
			def bulletTemplate = entity.bulletTemplate
			def target = entity.target
			
			def direction = target.position.copy().sub(turret.position).normalise()
			
			def bullet = bulletTemplate.get([position:turret.position.copy(),direction:direction])
				
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
		})
	}
}
