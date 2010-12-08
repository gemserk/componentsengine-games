package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.commons.animation.properties.InterpolatedProperty;
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
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.slick.animation.timeline.ColorInterpolatedValue;
import com.google.inject.Inject;
import com.google.inject.Provider;

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

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));
		property("levels", parameters.get("levels"));
		property("level", parameters.get("level"));

		final Rectangle screenResolution = Properties.getValue(entity, "screenBounds");

		// child(templateProvider.getTemplate("zombierockers.entities.world").instantiate("world", parameters));

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

			@Inject
			ChildrenManagementMessageFactory childrenManagementMessageFactory;

			@Handles
			public void changeLevel(Message message) {
				Entity world = templateProvider.getTemplate("zombierockers.entities.world").instantiate("world", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds);
						put("level", level);
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
						press("z", "accelerateSystem2000-press");
						release("z", "accelerateSystem2000-release");
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
				slick.getGameContainer().setMouseGrabbed(true);
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("shouldGrabMouse");
			}
		});

		child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate("fadeInEffect", new HashMap<String, Object>() {
			{
				put("started", false);
				put("time", 1000);
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", new ReferenceProperty<Object>("screenBounds", entity));
				put("effect", "fadeIn");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.effects.fade").instantiate("fadeOutEffect", new HashMap<String, Object>() {
			{
				put("started", false);
				put("delay", 2000);
				put("time", 1000);
				put("layer", 10);
				put("image", resourceManager.get("background"));
				put("screenResolution", new ReferenceProperty<Object>("screenBounds", entity));
				put("effect", "fadeOut");
			}
		}));

		final Entity messageLabel = templateProvider.getTemplate("gemserk.gui.label").instantiate("playButton", new HashMap<String, Object>() {
			{
				put("font", new ReferenceProperty<Object>("font", entity));
				put("position", slick.vector(screenResolution.getCenterX(), screenResolution.getCenterY() - 50f));
				put("bounds", slick.rectangle(screenResolution.getCenterX() - 160, screenResolution.getCenterY() - 25, 320, 50));
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "");
				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(slick.color(0f, 0f, 0f, 0f)), 0.004f));
			}
		});
		child(messageLabel);

		property("fontResource", resourceManager.get("FontDialogMessage"));
		property("font", new FixedProperty(entity) {
			@Override
			public Object get() {
				Resource fontResource = Properties.getValue(getHolder(), "fontResource");
				return fontResource.get();
			}
		});

		property("win", false);

		component(new FieldsReflectionComponent("fadeEffects") {

			@EntityProperty
			Boolean win;

			@Handles
			public void levelStarted(Message message) {
				Properties.setValue(messageLabel, "color", slick.color(0f, 0f, 0f, 0f));
				messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
					{
						property("animationId", "fadeInEffect");
					}
				}.build()));
			}

			@Handles
			public void levelFinished(Message message) {
				Properties.setValue(messageLabel, "color", slick.color(0f, 0f, 0f, 1f));
				win = Properties.getValue(message, "win");
				if (win)
					Properties.setValue(messageLabel, "message", "You win!");
				else
					Properties.setValue(messageLabel, "message", "You lose, try again!");
				messageQueue.enqueue(new Message("restartAnimation", new PropertiesMapBuilder() {
					{
						property("animationId", "fadeOutEffect");
					}
				}.build()));
			}
		});

		component(new FieldsReflectionComponent("nextScreenComponent") {

			@EntityProperty
			Boolean win;

			@Handles
			public void animationEnded(Message message) {
				String animationId = Properties.getValue(message, "entityId");
				if ("fadeOutEffect".equalsIgnoreCase(animationId)) {
					if (win)
						messageQueue.enqueueDelay(new Message("nextLevel"));
					else
						messageQueue.enqueueDelay(new Message("restartLevel"));
				}
			}

		});

		component(new ReferencePropertyComponent("grabMouse-enternodestate") {
			@Handles
			public void enterNodeState(Message message) {
				Properties.setValue(entity, "shouldGrabMouse", true);
				logger.info("Entering playing state");
			}
		});

		component(new ReferencePropertyComponent("grabMouse-leavenodestate") {
			@Handles
			public void leaveNodeState(Message message) {
				Properties.setValue(entity, "shouldGrabMouse", false);
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

	}
}