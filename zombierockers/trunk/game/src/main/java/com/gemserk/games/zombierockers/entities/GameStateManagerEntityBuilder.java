package com.gemserk.games.zombierockers.entities;

import java.util.Map;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class GameStateManagerEntityBuilder extends EntityBuilder {

	@Override
	public void build() {

		tags("gameStateManager");

		property("transitions", parameters.get("transitions"));

		property("stateEntities", parameters.get("stateEntities"));

		property("currentState", null);

		component(new Component("handleTransitionComponent") {

			@Inject
			MessageQueue messageQueue;

			@Override
			public void handleMessage(Message message) {

				Map<String, String> transitions = Properties.getValue(entity, getId() + ".transitions");

				if (!transitions.containsKey(message.getId()))
					return;

				String transition = transitions.get(message.getId());

				Map<String, Entity> stateEntities = Properties.getValue(entity, getId() + ".stateEntities");

				Entity stateEntity = stateEntities.get(transition);

				messageQueue.enqueueDelay(new Message("leaveNodeState", new PropertiesMapBuilder().property("message", message).build()));
				messageQueue.enqueueDelay(new Message("changeNodeState", new PropertiesMapBuilder().property("state", stateEntity).build()));
				messageQueue.enqueueDelay(new Message("enterNodeState", new PropertiesMapBuilder().property("message", message).build()));
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("transitions", "transitions");
				propertyRef("stateEntities", "stateEntities");
			}
		});

		component(new FieldsReflectionComponent("handleChangeStateComponent") {

			@Inject
			MessageQueue messageQueue;

			@EntityProperty
			Entity currentState;

			@Handles
			public void changeNodeState(Message message) {

				Entity newState = Properties.getValue(message, "state");

				if (newState == currentState)
					return;

				if (currentState != null)
					messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(currentState));

				messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newState, entity));

				currentState = newState;
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("currentState", "currentState");
			}
		});

	}

}
