package jylonwars.entities
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
import com.gemserk.games.jylonwars.WorldBoundsComponent;


builder.entity("ship") {
	
	tags("ship","nofriction")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	property("desiredDirection",utils.vector(0,0))
	property("player",parameters.player)
	
	property("radius",20)
	
	property("shotSound",utils.resources.sounds.sound("shot"))
	property("gameoverSound",utils.resources.sounds.sound("gameover"))
	
	property("target",utils.vector(1,0))
	property("bounds",parameters.bounds)
	
	property("bulletTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("jylonwars.entities.bullet"), 
			utils.custom.genericprovider.provide{ tower ->
				[
				position:tower.position.copy(),
				direction:tower.direction.copy(),
				image:utils.resources.image("bullet"),
				damage:tower.damage,
				radius:10.0f,
				maxVelocity:2f,
				color:utils.color(1.0f, 0.2f, 0.2f, 1.0f)
				]
			}))
	
	
	
	component(new ComponentFromListOfClosures("directionToForceComponent",[ {UpdateMessage message ->
		entity.direction = entity.target.copy().sub(entity.position)
		
		def desiredDirection = entity.desiredDirection
		if(desiredDirection.lengthSquared() > 0){
			entity."movement.force".add(desiredDirection.copy().normalise().scale(0.1f))
			desiredDirection.set(0,0)
		}else {
			entity."movement.force".add(entity."movement.velocity".copy().negate().scale(0.01f))
		}
		
	}
	]))
	
	component(new SuperMovementComponent("movement")){
		property("maxVelocity", (float)(300/1000))
		propertyRef("position", "position")
	}
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(0,0,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	component(utils.components.genericComponent(id:"lookAtHandler", messageId:["lookAt"]){ message ->
		def target = utils.vector(message.x,message.y)
		entity.target = target
	})
	
	
	component(new ComponentFromListOfClosures("moveHandler",[{ GenericMessage message ->
		if(!message.id.startsWith("${entity.player}.move"))
			return
		
		def command = message.id.replaceAll("${entity.player}.".toString(),"")
		def moveDirection
		switch(command){
			case "move.left":
				moveDirection = utils.vector(-1,0)
				break;
			case "move.right":
				moveDirection = utils.vector(1,0)
				break;
			case "move.up":
				moveDirection = utils.vector(0,-1)
				break;
			case "move.down":
				moveDirection = utils.vector(0,1)
				break;
			default:
				moveDirection = utils.vector(0,0)
		}
		
		entity.desiredDirection.add(moveDirection)
	}
	]))
	
	
	component(new WeaponComponent("shooter")) {
		property("reloadTime", 100)
		propertyRef("position", "position")
		property("shouldFire", true)
		
		property("trigger", utils.custom.triggers.closureTrigger { tower -> 
			def bulletTemplate = entity.bulletTemplate
			
			def bullet = bulletTemplate.get(tower)
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
			
			//entity.sound.play(1.0f, 0.3f);
			
			entity.shotSound.play();
		})
	}
	
	component(new GenericHitComponent("crashandburn")) {
		property("targetTag", "critter")
		property("predicate",{EntityPredicates.isNear(entity.position, entity.radius)})
		property("trigger", utils.custom.triggers.genericMessage("shipcollision") {})
	}
	
	component(utils.components.genericComponent(id:"shipcollision", messageId:"shipcollision"){ message ->
		//if(entity.id != message.source)
		//	return
			
		entity.gameoverSound.play()
	})
	
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
	
}
