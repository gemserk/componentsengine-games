package zombierockers.entities

import com.gemserk.componentsengine.messages.GenericMessage;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.GenericHitComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 
import com.gemserk.componentsengine.effects.EffectFactory 


builder.entity("cannon") {
	tags("cannon","nofriction")
	
	property("yaxisConstraint", 570f)
	property("position", utils.vector(400f,570f))
	property("direction",utils.vector(0,-1))
	
	property("fileTriggered",false)
	
	
	property("bounds",parameters.bounds)
	
	property("cannonballtemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.cannonball"), 
			utils.custom.genericprovider.provide{ cannon ->
				[
				position:cannon.position.copy(),
				direction:cannon.direction.copy(),
				image:utils.resources.image("ship"),
				radius:10.0f,
				maxVelocity:0.7f,
				color:utils.color(1.0f, 0.2f, 0.2f, 1.0f)
				]
			}))
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(0,0,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	component(utils.components.genericComponent(id:"mousemovehandler", messageId:["movemouse"]){ message ->
		entity.position =  utils.vector(message.x,entity.yaxisConstraint)
	})
	
	component(utils.components.genericComponent(id:"leftmousehandler", messageId:["leftmouse"]){ message ->
		entity.fileTriggered = true
	})
	
	component(new WeaponComponent("shooter")) {
		property("reloadTime", 150)
		propertyRef("position", "position")
		propertyRef("shouldFire", "fileTriggered")
		
		property("trigger", utils.custom.triggers.closureTrigger { cannon -> 
			entity.fileTriggered = false
			def template = entity.cannonballtemplate
			def parameters = [position:cannon.position.copy(),direction:cannon.direction.copy()]
			def cannonball = template.get(parameters)
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(cannonball, entity.parent))
		})
	}
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
}
