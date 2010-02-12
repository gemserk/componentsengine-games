package com.gemserk.games.towerofdefense;

import java.util.Map;

import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;

public class CustomMessageBuilder implements MessageBuilder {

	public Message build(Map<String, Object> map) {
		return new GenericMessage("hit", new PropertiesMapBuilder().addProperties(map).build());
	}

}