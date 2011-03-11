package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.animation4j.Animation;
import com.gemserk.animation4j.event.AnimationEvent;
import com.gemserk.animation4j.event.AnimationEventHandler;
import com.gemserk.animation4j.event.AnimationHandlerManager;
import com.gemserk.animation4j.slick.interpolators.ColorInterpolator;
import com.gemserk.animation4j.timeline.TimelineAnimationBuilder;
import com.gemserk.animation4j.timeline.TimelineValueBuilder;
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

	@SuppressWarnings( { "serial", "unchecked" })
	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");
		
		component(new ReferencePropertyComponent("stateLogic") {

			@EntityProperty
			Property<Rectangle> screenBounds;

			@EntityProperty
			Property<Animation> fadeOutAnimation;

			@EntityProperty
			Property<Animation> fadeInAnimation;

			@Inject
			AnimationHandlerManager animationHandlerManager;

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
				
				fadeOutAnimation.get().stop();
				fadeInAnimation.get().restart();
			}

			@Handles
			public void leaveNodeState(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(entity.getId() + "_settingsScreen"));
			}

			@Handles
			public void onSettingsBack(Message message) {
				fadeOutAnimation.get().restart();
				animationHandlerManager.with(new AnimationEventHandler() {
					@Override
					public void onAnimationFinished(AnimationEvent e) {
						messageQueue.enqueue(messageBuilder.newMessage("menu").get());
					}
				}).handleChangesOf(fadeOutAnimation.get());
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

		final Animation fadeInAnimation = new TimelineAnimationBuilder() {
			{
				delay(0);
				value("color", new TimelineValueBuilder<Color>() {
					{
						keyFrame(0, slick.color(1f, 1f, 1f, 1f), new ColorInterpolator());
						keyFrame(1000, slick.color(1f, 1f, 1f, 0f), new ColorInterpolator());
					}
				});
			}
		}.build();

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenBounds);
				put("animation", fadeInAnimation);
			}
		}));

		property("fadeInAnimation", fadeInAnimation);

		final Animation fadeOutAnimation = new TimelineAnimationBuilder() {
			{
				delay(0);
				value("color", new TimelineValueBuilder<Color>() {
					{
						keyFrame(0, slick.color(1f, 1f, 1f, 0f), new ColorInterpolator());
						keyFrame(1000, slick.color(1f, 1f, 1f, 1f), new ColorInterpolator());
					}
				});
			}
		}.build();

		property("fadeOutAnimation", fadeOutAnimation);

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenBounds);
				put("animation", fadeOutAnimation);
			}
		}));

	}
}