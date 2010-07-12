package dassault.entities

builder.entity {
	
	// camera controller for dassault game
	
	property("cameraId", parameters.cameraId)
	property("controlledDroidId", parameters.controlledDroidId)
	
//	property("camera", {entity.root.getEntityById(entity.cameraId)})
	property("controlledDroid", {entity.root.getEntityById(entity.controlledDroidId)})
	
	component(utils.components.genericComponent(id:"changeTargetedPosition", messageId:"changeControlledDroid"){ message ->
		def targetPosition = message.controlledDroid.position
//		def camera = entity.camera
		
		entity.controlledDroidId = message.controlledDroid.id
		// camera.moveTo(target, time)
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("moveTo"){ newMessage ->
			newMessage.entityId = entity.cameraId
			newMessage.time = 250
			newMessage.target = targetPosition
		})
	})
	
	component(utils.components.genericComponent(id:"updateTargetedPosition", messageId:"update"){ message ->
		
		def controlledDroid = entity.controlledDroid
		
		if (controlledDroid == null) {
			// lost control of main droid
			//	entity.position = utils.vector(0,0)
			// left the last droid position? 
			return
		}
		
		def position = controlledDroid.position.copy()
		def cameraId = entity.cameraId
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("moveTo"){ newMessage ->
			newMessage.entityId = cameraId
			newMessage.target = position
		})
	})
}
