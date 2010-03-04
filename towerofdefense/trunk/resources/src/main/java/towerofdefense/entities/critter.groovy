package towerofdefense.entities;
import towerofdefense.components.CritterHitHandler;

import com.gemserk.games.towerofdefense.FollowPathComponent;
import com.gemserk.games.towerofdefense.components.IncrementValueComponent;
import com.gemserk.games.towerofdefense.components.render.BarRendererComponent;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;

import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;

builder.entity("critter-${Math.random()}") {
	
	tags("critter", "nofriction")
	
	property("position", parameters.position)
	property("health", utils.container(parameters.health,parameters.health))
	property("reward",parameters.reward)
	property("points",parameters.points)
	property("color",parameters.color)
	propertyRef("direction", "movement.velocity")
	
	property("speed", (Float)(parameters.speed / 1000f))
	
	component(new SuperMovementComponent("movement")){
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "position")
	}
	
	component(new FollowPathComponent("followpath")){
		property("path", {entity.parent.path});
		property("pathindex", 0);
		propertyRef("force", "movement.force");
		propertyRef("position", "position");
	}
	
	if (parameters.image) {
		component(new ImageRenderableComponent("imagerenderer")) {
			property("image", parameters.image)
			propertyRef("color", "color")
			propertyRef("position", "position")
			propertyRef("direction", "direction")
			property("size", utils.vector(0.7f, 0.7f))
		}
	}
	
	component(new BarRendererComponent("healthRenderer")) {
		property("position", {entity.position.copy().sub(utils.vector(5f,15f))})
		property("emptyColor", utils.color(1.0f, 0.0f, 0.0f, 0.5f))
		propertyRef("container", "health")
		property("width", 15.0f)
		property("height", 2.0f)
	}
	
	if (parameters.rotationImage) {
		
		property("rotationValue", 0f)
		
		component(new IncrementValueComponent("rotator")) {
			propertyRef("value", "rotationValue")
			property("maxValue", 360f)
			property("increment", parameters.rotationSpeed ? (float)(parameters.rotationSpeed / 10f) : 0.2f)
		}
		
		component(new ImageRenderableComponent("rotationImageRender")) {
			property("image", parameters.rotationImage)
			propertyRef("color", "color")
			propertyRef("position", "position")
			property("direction", {utils.vector(1,0).add(entity.rotationValue)})
			property("size", utils.vector(0.7f, 0.7f))
		}
		
	}
	
	component(new CritterHitHandler("hithandler"))
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		if(message.critter == entity)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	}
	
	genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		if(message.critter == entity)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity))
	}
	
}

