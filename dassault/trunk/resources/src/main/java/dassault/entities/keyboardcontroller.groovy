package dassault.entities

import org.newdawn.slick.Input 

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicates;

builder.entity {
	
	tags("controller", parameters.player)
	
	property("player", parameters.player)
	property("leftKey", parameters.leftKey)
	property("rightKey", parameters.rightKey)
	property("upKey", parameters.upKey)
	property("downKey", parameters.downKey)
	
	component(utils.components.genericComponent(id:"controllerComponent", messageId:"update"){ message ->
		
		def player = entity.player
		
		def controlledEntities = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags(player, "droid")))
		
		def leftKey = entity.leftKey
		def rightKey = entity.rightKey
		def upKey = entity.upKey
		def downKey = entity.downKey
		
		controlledEntities.each { controlledEntity -> 
			
			Input input = utils.custom.gameContainer.input
			
			def moveDirection = utils.vector(0,0)
			
			if (input.isKeyDown(leftKey)) {
				moveDirection.x = -1
			}
			
			if (input.isKeyDown(rightKey)) {
				moveDirection.x = 1
			}
			
			if (input.isKeyDown(upKey)) {
				moveDirection.y = -1
			}
			
			if (input.isKeyDown(downKey)) {
				moveDirection.y = 1
			}
			
			controlledEntity.moveDirection = moveDirection
			
			def mousePosition = utils.vector(input.mouseX, input.mouseY)
			def fireDirection = mousePosition.sub(controlledEntity.position)
			
			controlledEntity.fireDirection = fireDirection
			
			// I don't like this...
			
			shouldFire = input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
			controlledEntity.shouldFire = shouldFire
		}
	})
	
}
