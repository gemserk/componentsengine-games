package dassault.entities


builder.entity("areatrigger") {
	
	property("position", parameters.position)
	property("area", parameters.area)
	
	property("enterAreaTrigger", parameters.enterAreaTrigger ?: utils.custom.triggers.nullTrigger())
	property("leaveAreaTrigger", parameters.leaveAreaTrigger ?: utils.custom.triggers.nullTrigger())
	
	property("pointerposition", utils.vector(0,0))
	property("pointerinside", false)
	
	component(utils.components.genericComponent(id:"pointerMoveHandler", messageId:"movemouse"){ message ->
		entity.pointerposition = utils.vector(message.x, message.y)
	})
	
	component(utils.components.genericComponent(id:"detectMouseOverHandler", messageId:"update"){ message ->
		def position = entity.position
		def point = entity.pointerposition
		def area = entity.area
		
		def internalPosition = point.copy().sub(position)
		
		if (area.contains(internalPosition.x, internalPosition.y)) {
			
			if (entity.pointerinside) 
				return
			
			entity.pointerinside = true
			entity.enterAreaTrigger.trigger([areaid:entity.id])
		} else {
			
			if (!entity.pointerinside)
				return
			
			entity.pointerinside = false
			entity.leaveAreaTrigger.trigger([areaid:entity.id])
		}
	})
	
}
