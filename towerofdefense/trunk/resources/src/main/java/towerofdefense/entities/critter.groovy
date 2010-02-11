package towerofdefense.entities;

import com.gemserk.componentsengine.messages.RemoveEntityMessage 

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	propertyRef("direction", "movement.velocity")
	
	component("movement")
		property("movement.maxVelocity", parameters.maxVelocity)
		propertyRef("movement.position", "position")
	
	component("followpath")
		property("followpath.pathEntityId", parameters.pathEntityId);
		property("followpath.path", parameters.pathProperty);
		property("followpath.pathindex", 0);
		propertyRef("followpath.force", "movement.force");
		propertyRef("followpath.position", "position");
	
	component("imagerenderer")
		property("image", image("towerofdefense.images.critter1"))
		property("color", parameters.color)
		
		
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def entity = message.entity		
		if (message.getProperty("targets").get().contains(entity))
			messageQueue.enqueue(new RemoveEntityMessage(entity))
			
	}
	
}
