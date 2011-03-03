package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.animation4j.Animation;
import com.gemserk.animation4j.componentsengine.properties.InterpolatedProperty;
import com.gemserk.animation4j.event.AnimationEventHandler;
import com.gemserk.animation4j.event.AnimationHandlerManager;
import com.gemserk.animation4j.slick.interpolators.ColorInterpolator;
import com.gemserk.animation4j.slick.values.ColorInterpolatedValue;
import com.gemserk.animation4j.timeline.TimelineAnimation;
import com.gemserk.animation4j.timeline.TimelineBuilder;
import com.gemserk.animation4j.timeline.TimelineValueBuilder;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Scores;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings("unchecked")
public class PlayingGameStateEntityBuilder extends EntityBuilder {

	private static final Logger logger = LoggerFactory.getLogger(PlayingGameStateEntityBuilder.class);

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

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));
		property("levels", parameters.get("levels"));
		property("level", parameters.get("level"));

		final Rectangle screenResolution = Properties.getValue(entity, "screenBounds");

		component(new ReferencePropertyComponent("mouseMoveConverter") {

			@Inject
			Input input;

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void mouseMoved(Message message) {
				messageQueue.enqueue(new Message("movemouse", new PropertiesMapBuilder() {
					{
						property("x", (float) input.getMouseX());
						property("y", (float) input.getMouseY());
					}
				}.build()));
			}

		});

		component(new FieldsReflectionComponent("restartLevel") {

			@EntityProperty
			List levels;

			@EntityProperty
			Map level;

			@EntityProperty
			Rectangle screenBounds;

			@Inject
			MessageQueue messageQueue;

			@EntityProperty
			Integer points;

			@Inject
			ChildrenManagementMessageFactory childrenManagementMessageFactory;

			@Handles
			public void changeLevel(Message message) {
				points = 0;
				Entity world = templateProvider.getTemplate("zombierockers.entities.world").instantiate("world", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds);
						put("level", level);
						put("points", new ReferenceProperty<Object>("points", entity));
					}
				});
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(world, entity));
				messageQueue.enqueue(new Message("levelStarted"));
			}

		});
		
		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("escape", "pauseGame");
						press("p", "pauseGame");
						press("space", "pauseGame");
						press("g", "dumpEditorPositions");
					}
				});

				mouse(new MouseMappingBuilder() {
					@Override
					public void build() {
						press("left", "leftmouse");
						press("right", "rightmouse");
						move("mouseMoved");
					}
				});
			}

		}));

		property("shouldGrabMouse", true);

		component(new ReferencePropertyComponent("grabMouse") {

			@EntityProperty
			Property<Boolean> shouldGrabMouse;

			@Inject
			GlobalProperties globalProperties;

			@Handles
			public void update(Message message) {
				if (!shouldGrabMouse.get().booleanValue())
					return;
				if ((Boolean) globalProperties.getProperties().get("runningInDebug"))
					return;
				if (!Mouse.isGrabbed())
					slick.getGameContainer().setMouseGrabbed(true);
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("shouldGrabMouse");
			}
		});

		final Animation fadeInAnimation = new TimelineAnimation(new TimelineBuilder() {
			{
				delay(0);
				value("color", new TimelineValueBuilder<Color>() {
					{
						keyFrame(0, slick.color(1f, 1f, 1f, 1f), new ColorInterpolator());
						keyFrame(1000, slick.color(1f, 1f, 1f, 0f), new ColorInterpolator());
					}
				});
			}
		}.build(), false);

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("layer", 50);
				put("image", resourceManager.get("background"));
				put("screenResolution", new ReferenceProperty<Object>("screenBounds", entity));
				put("animation", fadeInAnimation);
			}
		}));

		property("fadeInAnimation", fadeInAnimation);

		final Animation fadeOutAnimation = new TimelineAnimation(new TimelineBuilder() {
			{
				delay(2000);
				value("color", new TimelineValueBuilder<Color>() {
					{
						keyFrame(0, slick.color(1f, 1f, 1f, 0f), new ColorInterpolator());
						keyFrame(1000, slick.color(1f, 1f, 1f, 1f), new ColorInterpolator());
					}
				});
			}
		}.build(), false);

		property("fadeOutAnimation", fadeOutAnimation);

		child(templateProvider.getTemplate("effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("layer", 50);
				put("image", resourceManager.get("background"));
				put("screenResolution", new ReferenceProperty<Object>("screenBounds", entity));
				put("animation", fadeOutAnimation);
			}
		}));

		property("backgroundMusic", resourceManager.get("PlayMusic"));

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

		property("points", 0);

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("pointsLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontPointsLabel"));
				put("position", slick.vector(screenResolution.getMinX() + 140f, screenResolution.getMinY() + 30f));
				put("bounds", slick.rectangle(-100, -20, 200, 40));
				put("align", "left");
				put("valign", "center");
				put("layer", 40);
				put("message", new FixedProperty(entity) {
					@Override
					public Object get() {
						Integer points = Properties.getValue(getHolder(), "points");
						return "Points: " + points;
					}
				});
				put("color", slick.color(0.3f, 0.3f, 1.0f, 1f));
			}
		}));

		final Entity messageLabel = templateProvider.getTemplate("gemserk.gui.label").instantiate("winMessageLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() - 50f));
				put("bounds", slick.rectangle(screenResolution.getCenterX() - 160, screenResolution.getCenterY() - 25, 320, 50));
				put("align", "center");
				put("valign", "center");
				put("layer", 40);
				put("message", "");
				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(slick.color(0f, 0f, 0f, 0f)), 0.004f));
			}
		});
		child(messageLabel);

		property("win", false);

		component(new FieldsReflectionComponent("fadeEffects") {

			@EntityProperty
			Boolean win;

			@EntityProperty
			Resource<Sound> winSoundResource;

			@EntityProperty
			Resource<Sound> loseSoundResource;

			@EntityProperty
			Animation fadeOutAnimation;

			@EntityProperty
			Animation fadeInAnimation;
			
			@Inject
			AnimationHandlerManager animationHandlerManager;

			@Handles
			public void levelStarted(Message message) {
				Properties.setValue(messageLabel, "color", slick.color(0f, 0f, 0f, 0f));
				fadeOutAnimation.stop();
				fadeInAnimation.restart();
			}

			@Handles
			public void levelFinished(Message message) {
				Properties.setValue(messageLabel, "color", slick.color(0f, 0f, 1f, 1f));
				win = Properties.getValue(message, "win");
				if (win) {
					winSoundResource.get().play();
					Properties.setValue(messageLabel, "message", "You win!");
				} else {
					loseSoundResource.get().play();
					Properties.setValue(messageLabel, "message", "You lose, try again!");
				}
				fadeOutAnimation.restart();
				animationHandlerManager.with(new AnimationEventHandler(){
					@Override
					public void onAnimationFinished(Animation animation) {
						messageQueue.enqueue(messageBuilder.newMessage("onLevelFinished").get());
					}
				}).handleChangesOf(fadeOutAnimation);
			}
		}).withProperties(new ComponentProperties() {
			{
				property("winSoundResource", resourceManager.get("WinSound"));
				property("loseSoundResource", resourceManager.get("LoseSound"));
			}
		});

		component(new FieldsReflectionComponent("backgroundMusicComponent") {

			@EntityProperty
			Resource<Music> backgroundMusic;

			@Handles
			public void levelStarted(Message message) {
				if (backgroundMusic.get().getVolume() <= 0.05f)
					backgroundMusic.get().fade(1000, 1.0f, false);
			}

			@Handles
			public void levelFinished(Message message) {
				backgroundMusic.get().fade(1000, 0.0f, true);
			}

			@Handles
			public void enterNodeState(Message message) {
				if (!backgroundMusic.get().playing())
					backgroundMusic.get().fade(1000, 1.0f, false);
			}

			@Handles
			public void leaveNodeState(Message message) {
				backgroundMusic.get().fade(1000, 0.0f, true);
			}

		});

		component(new ReferencePropertyComponent("nextScreenComponent") {

			@EntityProperty
			Property<Boolean> win;

			@EntityProperty
			Property<Integer> points;

			@EntityProperty
			Property<Map<String, Object>> level;

			@Inject
			Scores scores;

			@Inject
			GlobalProperties globalProperties;

			@Handles
			public void onLevelFinished(Message message) {
				Object levelName = level.get().get("name");

				if (win.get()) {
					messageQueue.enqueue(messageBuilder.newMessage("enterscore") //
							.property("win", win.get()) //
							.property("points", (long) points.get()) //
							.property("levelName", levelName) //
							.get());
					return;
				}

				messageQueue.enqueue(new Message("highscores", new PropertiesMapBuilder().property("win", win.get()).property("levelName", levelName).build()));

			}

		});

		component(new ReferencePropertyComponent("grabMouse-component") {
			
			@EntityProperty
			Property<Boolean> shouldGrabMouse;
			
			@Handles
			public void enterNodeState(Message message) {
				shouldGrabMouse.set(true);
				logger.info("Entering playing state");
			}
			
			@Handles
			public void leaveNodeState(Message message) {
				shouldGrabMouse.set(false);
				slick.getGameContainer().setMouseGrabbed(false);
				logger.info("Leaving playing state");
			}

		});

		component(new ReferencePropertyComponent("grabscreenshot-leavenodestate") {
			@Inject
			GlobalProperties globalProperties;

			@Handles
			public void leaveNodeState(Message message) {
				Graphics graphics = slick.getGameContainer().getGraphics();
				Resource<Image> screenshot = (Resource<Image>) globalProperties.getProperties().get("screenshot");
				graphics.copyArea(screenshot.get(), 0, 0);
			}
		});

		component(new ReferencePropertyComponent("enterPauseWhenLostFocus") {
			@Inject
			MessageQueue messageQueue;

			@Handles
			public void update(Message message) {
				if (!slick.getGameContainer().hasFocus())
					messageQueue.enqueue(new Message("paused"));
			}
		});

		component(new ReferencePropertyComponent("pauseGameHandler") {
			@Inject
			MessageQueue messageQueue;

			@Handles
			public void pauseGame(Message message) {
				messageQueue.enqueue(new Message("paused"));
			}
		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));

	}
}