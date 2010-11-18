package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.input.MouseMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class GameOverGameStateEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Override
	public void build() {

		final Font font = slick.getResources().getFonts().font(false, false, 28);

		final Rectangle labelRectangle = slick.rectangle(-220, -50, 440, 100);
		final Rectangle screenBounds = slick.rectangle(0, 0, 800, 600);

		property("labelText", "");
		property("win", false);

		component(new ImageRenderableComponent("gameScreenshotRenderer")).withProperties(new ComponentProperties() {
			{
				property("image", globalProperties.getProperties().get("screenshot"));
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", 900);
			}
		});

		component(new RectangleRendererComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector(0, 0));
				property("rectangle", screenBounds);
				property("lineColor", slick.color(0.2f, 0.2f, 0.2f, 0.0f));
				property("fillColor", slick.color(0.5f, 0.5f, 0.5f, 0.5f));
				property("layer", 1000);
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("deadLabel", new HashMap<String, Object>() {
			{
				put("font", font);
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				put("fontColor", slick.color(0f, 0f, 0f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1010);
				put("message", new ReferenceProperty<Object>("labelText", entity));
			}
		}));

		component(new ReferencePropertyComponent("enterNodeStateHandler") {

			@EntityProperty
			Property<Boolean> win;

			@EntityProperty
			Property<String> labelText;

			@Handles
			public void enterNodeState(Message message) {
				Message sourceMessage = Properties.getValue(message, "message");
				Boolean winValue = Properties.getValue(sourceMessage, "win");

				win.set(winValue);

				if (win.get())
					labelText.set("You win");
				else
					labelText.set("You lose");

			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("win", "win");
				propertyRef("labelText", "labelText");
			}
		});

		component(new ReferencePropertyComponent("inputMessagesHandler") {
			@Inject
			MessageQueue messageQueue;

			@EntityProperty
			Property<Boolean> win;

			@Handles
			public void restart(Message message) {
				if (win.get())
					messageQueue.enqueue(new Message("nextLevel"));
				else
					messageQueue.enqueue(new Message("restartLevel"));
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("win", "win");
			}
		});

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("return", "restart");
						press("space", "restart");
						press("escape", "restart");
					}
				});

				mouse(new MouseMappingBuilder() {
					@Override
					public void build() {
						press("left", "restart");
						press("right", "restart");
					}
				});
			}

		}));

	}
}