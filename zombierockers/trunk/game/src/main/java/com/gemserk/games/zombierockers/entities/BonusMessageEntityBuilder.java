package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.animation4j.componentsengine.components.UpdateTimeProviderComponent;
import com.gemserk.animation4j.componentsengine.properties.InterpolatedProperty;
import com.gemserk.animation4j.slick.interpolators.ColorInterpolator;
import com.gemserk.animation4j.slick.interpolators.Vector2fInterpolator;
import com.gemserk.animation4j.time.UpdateableTimeProvider;
import com.gemserk.animation4j.transitions.AutoUpdateableTransition;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;

public class BonusMessageEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;

	@Inject
	MessageQueue messageQueue;

	@Inject
	ResourceManager resourceManager;

	static int bonusMessageNumber;

	@Override
	public String getId() {
		return "bonusMessageNumber" + bonusMessageNumber;
	}

	@SuppressWarnings("serial")
	@Override
	public void build() {

		bonusMessageNumber++;

		final Color startColor = parameters.get("startColor", slick.color(0f, 0f, 0f, 0f));
		final Color endColor = parameters.get("endColor", slick.color(0f, 0f, 0f, 1f));

		final Vector2f startSize = parameters.get("startSize", slick.vector(0.6f, 0.6f));
		final Vector2f endSize = parameters.get("endSize", slick.vector(1.0f, 1.0f));

		final UpdateableTimeProvider timeProvider = new UpdateableTimeProvider();

		String[] lines = parameters.get("lines");

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});

		// property("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(startColor, endColor), 0.005f, timeProvider));
		// property("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(startSize, endSize), 0.005f, timeProvider));

		property("color", new InterpolatedProperty<Color>(new AutoUpdateableTransition<Color>(startColor, endColor, new ColorInterpolator(), 0.005f, timeProvider)));
		property("size", new InterpolatedProperty<Vector2f>(new AutoUpdateableTransition<Vector2f>(startSize, endSize, new Vector2fInterpolator(), 0.005f, timeProvider)));

		property("font", resourceManager.get("FontBonusMessage"));

		HashMap<String, Object> newParameters = new HashMap<String, Object>() {
			{
				put("color", new ReferenceProperty<Object>("color", entity));
				put("size", new ReferenceProperty<Object>("size", entity));
				put("font", new ReferenceProperty<Object>("font", entity));
			}
		};

		newParameters.putAll(parameters.getWrappedParameters());

		Vector2f position = parameters.get("position");

		for (int i = 0; i < lines.length; i++) {
			newParameters.put("message", lines[i]);
			newParameters.put("position", position.copy().add(slick.vector(0, 40f * i)));
			child(templateProvider.getTemplate("gemserk.gui.label").instantiate(getId() + "_label_" + i, newParameters));
		}

		component(new FieldsReflectionComponent("invisibleWhenTimeComponent") {

			@EntityProperty
			Integer time;

			boolean enabled = false;

			@Handles
			public void update(Message message) {

				if (enabled)
					return;

				Integer delta = Properties.getValue(message, "delta");

				time -= delta;

				if (time <= 0) {
					Properties.setValue(entity, "color", slick.color(0f, 0f, 0f, 0f));
					enabled = true;
				}

			}

		}).withProperties(new ComponentProperties() {
			{
				property("time", 1000);
			}
		});
	}
}