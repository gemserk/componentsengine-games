package com.gemserk.games.towerofdefense.components;

import java.util.HashMap;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.games.towerofdefense.MessageBuilder;
import com.gemserk.games.towerofdefense.timers.Timer;
import com.google.inject.Inject;

public class TimerComponent extends ReflectionComponent {

	private PropertyLocator<Timer> timerProperty;
	private PropertyLocator<MessageBuilder> messageBuilderProperty;
	
	@Inject 
	MessageQueue messageQueue;

	public TimerComponent(String id) {
		super(id);
		timerProperty = Properties.property(id, "timer");
		messageBuilderProperty = Properties.property(id, "messageBuilder");
	}

	public void handleMessage(UpdateMessage message) {
		Entity entity = message.getEntity();
		Timer timer = timerProperty.getValue(entity);
		boolean fired = timer.update(message.getDelta());
		if (!fired)
			return;

		MessageBuilder messageBuilder = messageBuilderProperty.getValue(entity);
		Message newMessage = messageBuilder.build(new HashMap<String, Object>());
		
		messageQueue.enqueue(newMessage);

	}

}
