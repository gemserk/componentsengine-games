package zombierockers.scenes
;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.timers.CountDownTimer;


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.TimerComponent;

builder.entity {
	
	property("bounds",utils.rectangle(0,0,800,600))
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("background"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
	}
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["cannonball"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	
	Random random = new Random()
	
	
	child(entity("cannon"){
		parent("zombierockers.entities.cannon",[bounds:utils.rectangle(20,20,760,560)])
	})
	
	component(new ExplosionComponent("explosions")) {
	}
	
	
}
