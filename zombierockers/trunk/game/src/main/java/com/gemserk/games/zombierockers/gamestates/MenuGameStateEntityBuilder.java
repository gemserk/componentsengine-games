package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.commons.animation.Animation;
import com.gemserk.commons.animation.handlers.AnimationEventHandler;
import com.gemserk.commons.animation.handlers.AnimationHandlerManager;
import com.gemserk.commons.animation.timeline.LinearInterpolatorFactory;
import com.gemserk.commons.animation.timeline.TimelineAnimation;
import com.gemserk.commons.animation.timeline.TimelineBuilder;
import com.gemserk.commons.animation.timeline.TimelineValueBuilder;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
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
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings( { "unchecked", "unused", "serial" })
public class MenuGameStateEntityBuilder extends EntityBuilder {

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
	ResourceManager resourceManager;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	GameContainer container;

	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

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

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", screenBounds);
				put("animation", fadeOutAnimation);
			}
		}));

		property("fadeInAnimation", fadeInAnimation);
		property("fadeOutAnimation", fadeOutAnimation);

		property("mainMenuScreen", null);
		property("buttonPressed", "play");

		component(new ReferencePropertyComponent("gameStateLogic") {

			@EntityProperty
			Property<Rectangle> screenBounds;

			@EntityProperty
			Property<Entity> mainMenuScreen;

			// @EntityProperty
			// Property<Entity> profileScreen;

			@EntityProperty
			Property<String> buttonPressed;

			@EntityProperty
			Property<Animation> fadeInAnimation;

			@EntityProperty
			Property<Animation> fadeOutAnimation;

			@EntityProperty
			Property<Resource<Music>> backgroundMusic;

			@Inject
			AnimationHandlerManager animationHandlerManager;

			@Handles
			public void enterNodeState(Message message) {
				mainMenuScreen.set(templateProvider.getTemplate("screens.menu").instantiate(entity.getId() + "_menuScreen", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds.get());
						put("onPlayButton", "onPlayButton");
						put("onSettingsButton", "onSettingsButton");
						put("onProfileButton", "onProfileButton");
						put("onExitButton", "onExitButton");
					}
				}));
				// profileScreen.set(templateProvider.getTemplate("screens.menu").instantiate(entity.getId() + "_menuScreen", new HashMap<String, Object>() {
				// {
				// put("screenBounds", screenBounds.get());
				// put("onProfileEntered", "onProfileEntered");
				// }
				// }));
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(mainMenuScreen.get(), entity));

				if (!backgroundMusic.get().get().playing())
					backgroundMusic.get().get().fade(1000, 1.0f, false);
				
				fadeInAnimation.get().restart();
				fadeOutAnimation.get().stop();
			}

			@Handles
			public void leaveNodeState(Message message) {
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(mainMenuScreen.get()));
				mainMenuScreen.set(null);
				fadeInAnimation.get().stop();
				// profileScreen.set(null);
			}

			@Handles
			public void onPlayButton(Message message) {
				backgroundMusic.get().get().fade(1000, 0.0f, true);
				fadeOutAnimation.get().restart();
				animationHandlerManager.with(new AnimationEventHandler() {
					@Override
					public void onAnimationFinished(Animation animation) {
						messageQueue.enqueue(messageBuilder.newMessage("resume").get());
						messageQueue.enqueueDelay(messageBuilder.newMessage("restartLevel").get());
					}
				}).handleChangesOf(fadeOutAnimation.get());

			}

			@Handles
			public void onSettingsButton(Message message) {
				fadeOutAnimation.get().restart();
				animationHandlerManager.with(new AnimationEventHandler() {
					@Override
					public void onAnimationFinished(Animation animation) {
						messageQueue.enqueue(messageBuilder.newMessage("settings").get());
					}
				}).handleChangesOf(fadeOutAnimation.get());
			}

			@Handles
			public void onProfileButton(Message message) {
				fadeOutAnimation.get().restart();
				animationHandlerManager.with(new AnimationEventHandler() {
					@Override
					public void onAnimationFinished(Animation animation) {
						messageQueue.enqueue(messageBuilder.newMessage("profile").get());
					}
				}).handleChangesOf(fadeOutAnimation.get());
			}

			//			
			// @Handles
			// public void onProfileEntered(Message message) {
			// // without animation for now...
			// messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(profileScreen.get()));
			// messageQueue.enqueue(childrenManagementMessageFactory.addEntity(mainMenuScreen.get(), entity));
			// }

			@Handles
			public void onExitButton(Message message) {
				fadeOutAnimation.get().restart();
				animationHandlerManager.with(new AnimationEventHandler() {
					@Override
					public void onAnimationFinished(Animation animation) {
						container.exit();
					}
				}).handleChangesOf(fadeOutAnimation.get());
			}

		});

		property("backgroundMusic", resourceManager.get("BackgroundMusic"));

		component(new FieldsReflectionComponent("restartMusicComponent") {

			@EntityProperty(readOnly = true)
			Integer time;

			Integer currentTime = 0;

			@EntityProperty
			Resource<Music> backgroundMusic;

			@Handles
			public void update(Message message) {
				if (currentTime <= 0) {
					Music music = backgroundMusic.get();
					if (!music.playing())
						music.play();
					currentTime += time;
				}
				Integer delta = Properties.getValue(message, "delta");
				currentTime -= delta;
			}

		}).withProperties(new ComponentProperties() {
			{
				property("time", 1000);
			}
		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

	}
}