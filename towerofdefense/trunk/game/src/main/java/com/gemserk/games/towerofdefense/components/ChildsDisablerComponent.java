package com.gemserk.games.towerofdefense.components;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class ChildsDisablerComponent extends Component {

	PropertyLocator<Boolean> enabledProperty;

	public ChildsDisablerComponent(String id) {
		super(id);
		enabledProperty = Properties.property(id, "enabled");
	}

	@Override
	public void handleMessage(Message message) {
		
		boolean enabled = enabledProperty.getValue(entity);
		
		if (!enabled)
			message.suspendPropagation();
		
	}
}