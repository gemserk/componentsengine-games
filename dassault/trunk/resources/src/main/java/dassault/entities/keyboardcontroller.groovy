package dassault.entities

import org.newdawn.slick.Input 

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates;

builder.entity {
	
	tags("controller", parameters.player)
	
	property("ownerId", parameters.ownerId)
	property("camera", parameters.camera)
	
	property("leftKey", parameters.leftKey)
	property("rightKey", parameters.rightKey)
	property("upKey", parameters.upKey)
	property("downKey", parameters.downKey)
	
	property("secondaryLeftKey", parameters.secondaryLeftKey)
	property("secondaryRightKey", parameters.secondaryRightKey)
	property("secondaryUpKey", parameters.secondaryUpKey)
	property("secondaryDownKey", parameters.secondaryDownKey)
	
	// TODO: separate move from fire and transfer logics
	
	component(utils.components.genericComponent(id:"updateMoveDirection", messageId:"update"){ message ->
		
		Input input = utils.custom.gameContainer.input
		
		def player = entity.root.getEntityById(entity.ownerId)
		
		def leftKey = entity.leftKey
		def rightKey = entity.rightKey
		def upKey = entity.upKey
		def downKey = entity.downKey
		
		controlledDroid = entity.root.getEntityById(player.controlledDroidId)
		
		if (!controlledDroid)
			return
		
		def moveDirection = utils.vector(0,0)
		
		if (input.isKeyDown(leftKey) || input.isKeyDown(entity.secondaryLeftKey)) 
			moveDirection.x = -1
		
		if (input.isKeyDown(rightKey) || input.isKeyDown(entity.secondaryRightKey)) 
			moveDirection.x = 1
		
		if (input.isKeyDown(upKey) || input.isKeyDown(entity.secondaryUpKey)) 
			moveDirection.y = -1
		
		if (input.isKeyDown(downKey) || input.isKeyDown(entity.secondaryDownKey)) 
			moveDirection.y = 1
		
		controlledDroid.moveDirection = moveDirection
	})
	
	component(utils.components.genericComponent(id:"updateShouldFire", messageId:"update"){ message ->
		Input input = utils.custom.gameContainer.input
		def player = entity.root.getEntityById(entity.ownerId)
		
		controlledDroid = entity.root.getEntityById(player.controlledDroidId)
		
		if (!controlledDroid)
			return
		
		// set entity instead of id
		def camera = entity.root.getEntityById(entity.camera)
		def mouseRelative = camera.mouseRelativePosition
		
		controlledDroid.fireDirection = mouseRelative.copy()
		controlledDroid.shouldFire = input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
		
	})
	
	component(utils.components.genericComponent(id:"updateShouldTransfer", messageId:"update"){ message ->
		Input input = utils.custom.gameContainer.input
		def player = entity.root.getEntityById(entity.ownerId)
		
		controlledDroid = entity.root.getEntityById(player.controlledDroidId)
		
		if (!controlledDroid)
			return
		
		// set entity instead of id
		def camera = entity.root.getEntityById(entity.camera)
		
		def mousePosition = utils.vector(input.mouseX, input.mouseY)
		
		def cameraPosition = camera.position
		def mouseAbsolutePosition = mousePosition.copy().sub(cameraPosition)
		
		def transferEnabled = input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)
		
		if (!controlledDroid.transfering) {
			
			if (!transferEnabled)
				return
			
			def droids = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), // 
					{ droid -> droid.bounds.contains(mouseAbsolutePosition.x, mouseAbsolutePosition.y)} as Predicate))
			if (droids.isEmpty())
				return
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("startTransfering"){ newMessage ->
				newMessage.droidId = controlledDroid.id
				newMessage.selectedDroid = droids[0]
			})
			
		} else { 
			if (!transferEnabled) {
				// send message stop transfering 
				utils.custom.messageQueue.enqueue(utils.genericMessage("stopTransfering"){ newMessage ->
					newMessage.droidId = controlledDroid.id
				})
			}
		}
		
	})
	
}
