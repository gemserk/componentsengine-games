package dassault.entities

import org.newdawn.slick.Input 

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

builder.entity {
	
	tags("controller", parameters.player)
	
	property("owner", parameters.owner)
	property("camera", parameters.camera)
	
	property("leftKey", parameters.leftKey)
	property("rightKey", parameters.rightKey)
	property("upKey", parameters.upKey)
	property("downKey", parameters.downKey)
	
	component(utils.components.genericComponent(id:"controllerComponent", messageId:"update"){ message ->
		
		def owner = entity.root.getEntityById(entity.owner)
		
		// set entity instead of id
		def camera = entity.root.getEntityById(entity.camera)
		
		//		def controlledEntities = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags(player, "droid")))
		
		def leftKey = entity.leftKey
		def rightKey = entity.rightKey
		def upKey = entity.upKey
		def downKey = entity.downKey
		
		controlledEntity = owner
		
		Input input = utils.custom.gameContainer.input
		
		def moveDirection = utils.vector(0,0)
		
		if (input.isKeyDown(leftKey)) 
			moveDirection.x = -1
		
		if (input.isKeyDown(rightKey)) 
			moveDirection.x = 1
		
		if (input.isKeyDown(upKey)) 
			moveDirection.y = -1
		
		if (input.isKeyDown(downKey)) 
			moveDirection.y = 1
		
		controlledEntity.moveDirection = moveDirection
		
		def mousePosition = utils.vector(input.mouseX, input.mouseY)
		def droidPosition = controlledEntity.position.copy()
		
		def centerPosition = utils.vector(400f, 300f)
		
		def mouseRelative = camera.mouseRelativePosition.copy()
		
		controlledEntity.fireDirection = mouseRelative
		
		shouldFire = input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
		controlledEntity.shouldFire = shouldFire

		def cameraPosition = camera.position
		def mouseAbsolutePosition = mousePosition.copy().sub(cameraPosition)
		
		controlledEntity.selectedDroid = null
		def droids = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), // 
				{ droid -> droid.bounds.contains(mouseAbsolutePosition.x, mouseAbsolutePosition.y)} as Predicate))
		if (droids.isEmpty())
			return
		controlledEntity.selectedDroid = droids[0]
		
		println "droid targeted"
	})
	
}
