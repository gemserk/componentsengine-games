package jylonwars.scenes;

import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 

builder.entity("statebasednode") {
	
	property("enabled", parameters.enabled)
	
	component(new ProcessingDisablerComponent("disableStateComponent")){
		propertyRef("enabled","enabled")
		property("exclusions",parameters.exclusions ?: [])
	}
	
}

