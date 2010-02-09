package com.gemserk.games.towerofdefense.gamestates;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.properties.Properties;

public final class DefenseTriggerComponent extends ReflectionComponent {

	DefenseTriggerComponent(String name) {
		super(name);
	}

	public void handleMessage(GenericMessage message) {

		Entity entity = message.getEntity();

		if (message.getId().equals("enableDefense")) {
			handleActivateDefense(entity, message);
		} else if (message.getId().equals("disableDefense")) {
			handleDeactivateDefense(entity, message);
		}

	}

	private void handleDeactivateDefense(Entity entity, GenericMessage message) {
		String defenseId = (String) Properties.property("value").getValue(
				message);
		Properties.property(defenseId + ".enabled").setValue(entity, false);
	}

	private void handleActivateDefense(Entity entity, GenericMessage message) {
		String defenseId = (String) Properties.property("value").getValue(
				message);
		Properties.property(defenseId + ".enabled").setValue(entity, true);
	}
}