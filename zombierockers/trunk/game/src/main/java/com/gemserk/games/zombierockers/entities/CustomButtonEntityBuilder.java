package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.components.UpdateTimeProviderComponent;
import com.gemserk.commons.animation.properties.InterpolatedProperty;
import com.gemserk.commons.animation.properties.InterpolatedPropertyTimeProvider;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.slick.animation.timeline.ColorInterpolatedValue;
import com.gemserk.slick.animation.timeline.Vector2fInterpolatedValue;
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

		final InterpolatedPropertyTimeProvider timeProvider = new InterpolatedPropertyTimeProvider();

		property("buttonReleasedSound", parameters.get("buttonReleasedSound"), null);

		property("notFocusedColor", parameters.get("notFocusedColor"), slick.color(0.2f, 0.2f, 1f, 0.9f));
		property("focusedColor", parameters.get("focusedColor"), slick.color(0.2f, 0.2f, 1f, 1f));

		property("buttonReleasedMessageId", parameters.get("buttonReleasedMessageId"), "buttonReleased");

		final Color startColor = Properties.getValue(entity, "notFocusedColor");

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});

		HashMap<String, Object> newParameters = new HashMap<String, Object>() {
			{
				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(new Color(startColor)), 0.005f, timeProvider));
				put("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(slick.vector(1f, 1f)), 0.005f, timeProvider));
			}
		};

		component(new ReferencePropertyComponent("buttonFocusedHandler") {

			@Handles
			public void onButtonFocused(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Color focusedColor = Properties.getValue(entity, "focusedColor");
				Properties.setValue(entity, "color", focusedColor);
				Properties.setValue(entity, "size", slick.vector(1.1f, 1.1f));
			}

		});

		component(new ReferencePropertyComponent("buttonLostFocusHandler") {

			@Handles
			public void onButtonLostFocus(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Color notFocusedColor = Properties.getValue(entity, "notFocusedColor");
				Properties.setValue(entity, "color", notFocusedColor);
				Properties.setValue(entity, "size", slick.vector(1f, 1f));
			}

		});

		component(new ReferencePropertyComponent("buttonPressedHandler") {

			@Handles
			public void onButtonPressed(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;
				Properties.setValue(entity, "size", slick.vector(0.9f, 0.9f));
			}

		});

		component(new ReferencePropertyComponent("buttonReleasedHandler") {
			
			@EntityProperty
			Property<String> buttonReleasedMessageId;

			@Handles
			public void onButtonReleased(Message message) {
				Entity source = Properties.getValue(message, "source");
				if (entity != source)
					return;

				Properties.setValue(entity, "size", slick.vector(1.1f, 1.1f));
				Resource<Sound> sound = Properties.getValue(entity, "buttonReleasedSound");
				if (sound != null)
					sound.get().play();
				
//				messageQueue.enqueue(messageBuilder.newMessage("").property("source", entity));

				messageQueue.enqueue(new Message(buttonReleasedMessageId.get(), new PropertiesMapBuilder() {
					{
						property("source", entity);
						property("buttonId", entity.getId());
					}
				}.build()));

			}

		});

		Map<String, Object> wrappedParameters = parameters.getWrappedParameters();
		wrappedParameters.putAll(newParameters);
		parent("gemserk.gui.labelbutton", wrappedParameters);
	}
}