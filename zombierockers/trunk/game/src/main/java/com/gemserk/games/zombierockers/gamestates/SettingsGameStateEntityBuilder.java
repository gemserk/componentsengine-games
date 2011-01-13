package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.commons.animation.Animation;
import com.gemserk.commons.animation.timeline.LinearInterpolatorFactory;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineBuilder;
import com.gemserk.commons.animation.timeline.TimelineValueBuilder;
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
		
		property("animationEndCallback", null);

		component(new ReferencePropertyComponent("stateLogic") {

			@EntityProperty
			Property<Rectangle> screenBounds;

			@EntityProperty
			Property<Animation> fadeOutAnimation;

			@EntityProperty
			Property<Animation> fadeInAnimation;

			@EntityProperty
			Property<Runnable> animationEndCallback;

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
				fadeInAnimation.get().restart();
			}

			@Handles
			public void leaveNodeState(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(entity.getId() + "_settingsScreen"));
				fadeInAnimation.get().stop();
			}

			@Handles
			public void onSettingsBack(Message message) {
				fadeOutAnimation.get().restart();
				animationEndCallback.set(new Runnable() {
					@Override
					public void run() {
						messageQueue.enqueue(messageBuilder.newMessage("menu").get());
					}
				});
			}

			@Handles
			public void update(Message message) {
				if (fadeOutAnimation.get().isFinished()) {
					fadeOutAnimation.get().stop();
					if (animationEndCallback.get() != null)
						animationEndCallback.get().run();
				}
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

		final Animation fadeInAnimation = new TimelineAnimation(new TimelineBuilder() {
			{
				delay(0);
				value("color", new TimelineValueBuilder<Color>() {
					{
						interpolator(LinearInterpolatorFactory.linearInterpolatorColor());
						keyFrame(0, slick.color(1f, 1f, 1f, 1f));
						keyFrame(1000, slick.color(1f, 1f, 1f, 0f));
					}
				});
			}
		}.build(), false);

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenBounds);
				put("animation", fadeInAnimation);
			}
		}));

		property("fadeInAnimation", fadeInAnimation);

		final Animation fadeOutAnimation = new TimelineAnimation(new TimelineBuilder() {
			{
				delay(0);
				value("color", new TimelineValueBuilder<Color>() {
					{
						interpolator(LinearInterpolatorFactory.linearInterpolatorColor());
						keyFrame(0, slick.color(1f, 1f, 1f, 0f));
						keyFrame(1000, slick.color(1f, 1f, 1f, 1f));
					}
				});
			}
		}.build(), false);

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