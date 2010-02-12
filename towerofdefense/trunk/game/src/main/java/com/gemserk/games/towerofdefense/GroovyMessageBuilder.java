package com.gemserk.games.towerofdefense;

import groovy.lang.Closure;

import java.util.Map;

import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;

public class GroovyMessageBuilder implements MessageBuilder {

	String messageId;

	Closure closure;

	public GroovyMessageBuilder(String messageId, Closure closure) {
		this.messageId = messageId;
		this.closure = closure;
	}

	@Override
	public Message build(Map<String, Object> map) {
		GenericMessage message = new GenericMessage(messageId, new PropertiesMapBuilder().addProperties(map).build());

		closure.setProperty("message", message);
		closure.call();

		return message;
	}

}
