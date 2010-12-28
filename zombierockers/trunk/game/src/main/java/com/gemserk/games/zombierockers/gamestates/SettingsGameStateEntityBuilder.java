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
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings("unused")
public class SettingsGameStateEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	ResourceManager resourceManager;

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		component(new ReferencePropertyComponent("enterStateHandler") {

			@EntityProperty
			Property<Rectangle> screenBounds;

			@SuppressWarnings("serial")
			@Handles
			public void enterNodeState(Message message) {
				Entity settingsScreen = templateProvider.getTemplate("screens.settings").instantiate(entity.getId() + "_settingsScreen", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds.get());
						put("backButtonMessage", "onSettingsBack");
						put("background", resourceManager.get("background"));
					}
				});
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(settingsScreen, entity));
			}

			@Handles
			public void leaveNodeState(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(entity.getId() + "_settingsScreen"));
			}
			
			@Handles
			public void onSettingsBack(Message message) {
				messageQueue.enqueue(messageBuilder.newMessage("menu").get());
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

		// child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate(entity.getId() + "_fadeInEffect", new HashMap<String, Object>() {
		// {
		// put("started", false);
		// put("time", 1000);
		// put("layer", 10);
		// put("image", resourceManager.get("background"));
		// put("screenResolution", screenBounds);
		// put("effect", "fadeIn");
		// }
		// }));
		//
		// child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate(entity.getId() + "_fadeOutEffect", new HashMap<String, Object>() {
		// {
		// put("started", false);
		// put("time", 1000);
		// put("layer", 10);
		// put("image", resourceManager.get("background"));
		// put("screenResolution", screenBounds);
		// put("effect", "fadeOut");
		// }
		// }));
		//
		// component(new ReferencePropertyComponent("fadeInWhenEnterState") {
		// @Handles
		// public void enterNodeState(Message message) {
		// messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
		// {
		// property("animationId", "fadeInEffect");
		// }
		// }.build()));
		// }
		// });

		// property("buttonPressed", "back");
		//
		// component(new FieldsReflectionComponent("guiHandler") {
		//
		// @EntityProperty
		// String buttonPressed;
		//
		// @Handles
		// public void buttonReleased(Message message) {
		// String id = Properties.getValue(message, "buttonId");
		//
		// if ("backButton".equals(id)) {
		// buttonPressed = "back";
		// messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
		// {
		// property("animationId", "fadeOutEffect");
		// }
		// }.build()));
		// } else if ("fullScreenCheckbox".equals(id)) {
		//
		// }
		//
		// }
		//
		// });
		//
		// component(new FieldsReflectionComponent("animationComponentHandler") {
		//
		// @EntityProperty
		// String buttonPressed;
		//
		// @Handles
		// public void animationEnded(Message message) {
		// String animationId = Properties.getValue(message, "entityId");
		// if ("fadeOutEffect".equalsIgnoreCase(animationId)) {
		// if ("back".equals(buttonPressed)) {
		// messageQueue.enqueue(new Message("menu"));
		// }
		// }
		// }
		//
		// });
	}
}