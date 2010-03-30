package gemserk.gui;


import com.gemserk.componentsengine.commons.components.LabelComponent;
import com.gemserk.componentsengine.triggers.NullTrigger;
import com.gemserk.games.towerofdefense.components.CursorOverDetector;
import com.gemserk.games.towerofdefense.components.PressedReleasedTriggerComponent;

builder.entity {
	
	tags("button")
	
	property("enabled", parameters.enabled ?: true)
	
	property("position", parameters.position)
	property("direction", utils.vector(1f,0f))
	
	property("text", parameters.label)
	property("font", parameters.font)
	
	property("bounds", parameters.bounds)
	property("cursorOver", false)
	property("pressed", false)
	
	property("onEnterTrigger", parameters.onEnterTrigger ?: new NullTrigger())
	property("onLeaveTrigger", parameters.onLeaveTrigger ?: new NullTrigger())
	
	property("onPressedTrigger", parameters.onPressedTrigger ?: new NullTrigger())
	property("onReleasedTrigger", parameters.onReleasedTrigger ?: new NullTrigger())
	
	component(new CursorOverDetector("cursorOver")) {
		propertyRef("position", "position")
		propertyRef("bounds", "bounds")
		propertyRef("cursorOver", "cursorOver")
		property("eventId", "move")
		propertyRef("onEnterTrigger", "onEnterTrigger")
		propertyRef("onLeaveTrigger", "onLeaveTrigger")
	}
	
	component(new PressedReleasedTriggerComponent("pressedReleased")) {
		property("pressedEvent", "mouse.leftpressed")
		property("releasedEvent", "mouse.leftreleased")
		propertyRef("pressed", "pressed")
		propertyRef("cursorOver", "cursorOver")
		propertyRef("onPressedTrigger", "onPressedTrigger")
		propertyRef("onReleasedTrigger", "onReleasedTrigger")
	}
	
	if (parameters.label != null) {
		component(new LabelComponent("textComponent")) {
			propertyRef("position","position")
			property("message","{0}")
			propertyRef("value", "text")
			propertyRef("font", "font")
		}
	}
}
