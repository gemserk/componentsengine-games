package towerofdefense.entities;
import com.gemserk.componentsengine.messages.RemoveEntityMessage;
builder.entity("bullet-${Math.random()}") {
	
	tags("bullet", "nofriction")
	
	property("position", parameters.position);
	propertyRef("direction", "movement.velocity");
	property("damage", parameters.damage);
	property("radius", parameters.radius);
	
	component("movement"){
		property("velocity", parameters.direction.scale(parameters.maxVelocity))
		property("maxVelocity", parameters.maxVelocity)
		propertyRef("position", "position")
	}
	
	component("imagerenderer")
	property("image", image("towerofdefense.images.bullet"))
	property("color", parameters.color)
	
	component("bullethit"){
		property("targetTag", "critter")
		propertyRef("position", "position")
		propertyRef("radius", "radius")
		property("messageBuilder", utils.custom.messageBuilderFactory.messageBuilder("hit") { 
			def source = message.source
			def damage = source.damage
			message.damage = damage;
		})
	}
	
	genericComponent(id:"hithandler", messageId:"hit"){ message ->
		def entity = message.entity		
		if (message.getProperty("source").get() == entity)
			messageQueue.enqueue(new RemoveEntityMessage(entity))
	}
	
	
}
