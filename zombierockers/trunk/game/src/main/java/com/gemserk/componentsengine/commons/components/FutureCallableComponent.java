package com.gemserk.componentsengine.commons.components;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.timers.Timer;
import com.google.inject.Inject;

public class FutureCallableComponent extends ReferencePropertyComponent {

	protected static final Logger logger = LoggerFactory.getLogger(FutureCallableComponent.class);

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@SuppressWarnings("unchecked")
	@EntityProperty
	Property<Future> future;

	@EntityProperty
	Property<Timer> timer;

	// TODO: add an id

	public FutureCallableComponent(String id) {
		super(id);
	}

	@Handles
	public void update(Message message) {

		if (future.get() == null)
			return;

		Integer delta = Properties.getValue(message, "delta");

		boolean triggered = timer.get().update(delta);

		if (future.get().isDone()) {
			try {
				messageQueue.enqueue(messageBuilder.newMessage("futureDone").property("data", future.get().get()).get());
			} catch (Exception e) {
				logger.error("failed to get data from future", e);
				messageQueue.enqueue(messageBuilder.newMessage("futureFailed").get());
			}
		} else {
			if (!triggered)
				return;
			if (logger.isInfoEnabled())
				logger.info("future timed out");
			messageQueue.enqueue(messageBuilder.newMessage("futureTimedOut").get());
		}

		future.set(null);
	}
}