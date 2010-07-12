package dassault.entities

builder.entity {
	
	property("animations", parameters.animations)
	
	component(utils.components.genericComponent(id:"restartAnimationsHandler", messageId:"restartAnimations"){ message ->
		if(!entity.id.equals(message.animationId))
			return
		entity.animations.each { animation -> 
			animation.restart();
		}
	})
	
	component(utils.components.genericComponent(id:"updateAnimationsHandler", messageId:"update"){ message ->
		entity.animations.each { animation -> 
			if (animation.paused)
				return
			animation.animate(entity, message.delta)
			if (animation.finished)
				animation.restart()
		}
	})
	
}
