package com.gemserk.games.towerofdefense;

import java.util.Map;

import com.gemserk.componentsengine.messages.Message;

public interface MessageBuilder {

	Message build(Map<String, Object> map);

}