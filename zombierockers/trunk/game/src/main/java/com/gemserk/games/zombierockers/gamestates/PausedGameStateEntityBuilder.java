package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class PausedGameStateEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	ResourceManager resourceManager;

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));

		property("pauseScreen", null);
		property("settingsScreen", null);

		component(new ReferencePropertyComponent("enterStateHandler") {

			@EntityProperty
			Property<Rectangle> screenBounds;

			@EntityProperty
			Property<Entity> pauseScreen;

			@EntityProperty
			Property<Entity> settingsScreen;

			@SuppressWarnings("serial")
			@Handles
			public void enterNodeState(Message message) {
				pauseScreen.set(templateProvider.getTemplate("screens.paused").instantiate(entity.getId() + "_pauseScreen", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds.get());
						put("onSettingsButton", "onSettingsButton");
					}
				}));
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(pauseScreen.get(), entity));
			}

			@Handles
			public void leaveNodeState(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(pauseScreen.get()));
			}

			@Handles
			public void onSettingsButton(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(pauseScreen.get()));
				settingsScreen.set(templateProvider.getTemplate("screens.settings").instantiate(entity.getId() + "_settingsScreen", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds.get());
						put("backButtonMessage", "onSettingsBack");
						put("background", globalProperties.getProperties().get("screenshot"));
					}
				}));
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(settingsScreen.get(), entity));
			}

			@Handles
			public void onSettingsBack(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(settingsScreen.get()));
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(pauseScreen.get(), entity));
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

	}
}