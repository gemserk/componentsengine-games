package com.gemserk.games.dassault.components;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.google.inject.Inject;

public class TransferComponent extends FieldsReflectionComponent {

	@EntityProperty(readOnly = true)
	Integer totalTransferTime;

	@EntityProperty(required = false)
	Integer transferTime = 0;

	@EntityProperty(required = false)
	Entity selectedDroid = null;

	@EntityProperty(required = false)
	Boolean transfering = false;

	@Inject
	MessageQueue messageQueue;

	public TransferComponent(String id) {
		super(id);
	}

	@Handles
	public void startTransfering(Message message) {
		String droidId = Properties.getValue(message, "droidId");
		if (!entity.getId().equals(droidId))
			return;

		selectedDroid = Properties.getValue(message, "selectedDroid");
		transferTime = totalTransferTime;
		transfering = true;
	}

	@Handles
	public void stopTransfering(Message message) {
		String droidId = Properties.getValue(message, "droidId");
		if (!entity.getId().equals(droidId))
			return;

		selectedDroid = null;
		transfering = false;
	}

	@Handles
	public void update(Message message) {
		if (!transfering)
			return;

		Integer delta = Properties.getValue(message, "delta");

		transferTime -= delta;

		if (transferTime > 0)
			return;

		final String ownerId = Properties.getValue(entity, "ownerId");

		messageQueue.enqueue(new Message("changeControlledDroid", new PropertiesMapBuilder() {
			{
				property("controlledDroid", selectedDroid);
				property("ownerId", ownerId);
			}
		}.build()));

		messageQueue.enqueue(new Message("stopTransfering", new PropertiesMapBuilder() {
			{
				property("droidId", entity.getId());
			}
		}.build()));
	}

}
