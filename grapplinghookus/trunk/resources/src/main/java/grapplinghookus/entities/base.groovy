package grapplinghookus.entities



builder.entity {
	
	tags("base")
	
	property("position", parameters.position)
	property("bullets", 0)
	
	component(utils.components.genericComponent(id:"trappedEnemyBaseReachedHandler", messageId:"trappedEnemyBaseReached"){ message ->
		if (message.baseId != entity.id) 
			return
		entity.bullets = entity.bullets + 1
	})
}
