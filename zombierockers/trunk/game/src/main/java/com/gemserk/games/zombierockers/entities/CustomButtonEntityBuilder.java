package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.animation4j.componentsengine.UpdateableTimeProvider;
import com.gemserk.animation4j.componentsengine.components.UpdateTimeProviderComponent;
import com.gemserk.animation4j.componentsengine.properties.InterpolatedProperty;
import com.gemserk.animation4j.slick.values.ColorInterpolatedValue;
import com.gemserk.animation4j.slick.values.Vector2fInterpolatedValue;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.google.inject.Inject;

public class CustomButtonEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@SuppressWarnings("serial")
	@Override
	public void build() {

		final UpdateableTimeProvider timeProvider = new UpdateableTimeProvider();

		property("buttonReleasedSound", parameters.get("buttonReleasedSound"), null);

		property("notFocusedColor", parameters.get("notFocusedColor"), slick.color(0.2f, 0.2f, 1f, 0.9f));
		property("focusedColor", parameters.get("focusedColor"), slick.color(0.2f, 0.2f, 1f, 1f));

		property("notFocusedSize", parameters.get("notFocusedSize"), slick.vector(1f, 1f));
		property("focusedSize", parameters.get("focusedSize"), slick.vector(1.1f, 1.1f));

		property("buttonReleasedMessageId", parameters.get("buttonReleasedMessageId"), "buttonReleased");

		final Color startColor = Properties.getValue(entity, "notFocusedColor");
		final Vector2f startSize = Properties.getValue(entity, "notFocusedSize");

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});

		HashMap<String, Object> newParameters = new HashMap<String, Object>() {
			{
				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(new Color(startColor)), 0.005f, timeProvider));
				put("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(startSize.copy()), 0.005f, timeProvider));
			}
		};

		component(new ReferencePropertyComponent("buttonHandler") {

			@EntityProperty
			Property<Color> focusedColor;

			@EntityProperty
			Property<Color> notFocusedColor;

			@EntityProperty
			Property<Vector2f> notFocusedSize;

			@EntityProperty
			Property<Vector2f> focusedSize;

			@EntityProperty
			Property<String> buttonReleasedMessageId;
			
			@EntityProperty
			Property<Resource<Sound>> buttonReleasedSound;

			@Handles
			public void onButtonFocused(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Properties.setValue(entity, "color", focusedColor.get());
				Properties.setValue(entity, "size", focusedSize.get());
			}

			@Handles
			public void onButtonLostFocus(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Properties.setValue(entity, "color", notFocusedColor.get());
				Properties.setValue(entity, "size", notFocusedSize.get());
			}

			@Handles
			public void onButtonPressed(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Properties.setValue(entity, "size", notFocusedSize.get());
			}

			@Handles
			public void onButtonReleased(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Properties.setValue(entity, "size", focusedSize.get());
				if (buttonReleasedSound.get().get() != null)
					buttonReleasedSound.get().get().play();
				messageQueue.enqueue(messageBuilder.newMessage(buttonReleasedMessageId.get()).property("source", entity).property("buttonId", entity.getId()).get());
			}

		});

		Map<String, Object> wrappedParameters = parameters.getWrappedParameters();
		wrappedParameters.putAll(newParameters);
		parent("gemserk.gui.labelbutton", wrappedParameters);
	}
}