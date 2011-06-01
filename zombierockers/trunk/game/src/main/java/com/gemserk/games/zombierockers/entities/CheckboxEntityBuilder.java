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
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.triggers.NullTrigger;
import com.google.inject.Inject;

public class CheckboxEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;

	@Inject
	MessageQueue messageQueue;

	@SuppressWarnings("serial")
	@Override
	public void build() {

		HashMap<String, Object> newParameters = new HashMap<String, Object>() {
			{
				put("onEnterTrigger", new NullTrigger() {
					@Override
					public void trigger(Object... parameters) {
						Entity labelEntity = (Entity) parameters[0];
						Properties.setValue(labelEntity, "color", slick.color(0f, 0f, 1f, 1f));
						Properties.setValue(labelEntity, "size", slick.vector(1.1f, 1.1f));
					}
				});
				put("onLeaveTrigger", new NullTrigger() {
					@Override
					public void trigger(Object... parameters) {
						Entity labelEntity = (Entity) parameters[0];
						Properties.setValue(labelEntity, "color", slick.color(0f, 0f, 1f, 0.75f));
						Properties.setValue(labelEntity, "size", slick.vector(1f, 1f));
					}
				});
				put("onPressedTrigger", new NullTrigger() {
					@Override
					public void trigger(Object... parameters) {
						Entity labelEntity = (Entity) parameters[0];
						Properties.setValue(labelEntity, "size", slick.vector(0.9f, 0.9f));
					}
				});
				put("onReleasedTrigger", new NullTrigger() {
					@Override
					public void trigger(Object... parameters) {
						final Entity entity = (Entity) parameters[0];
						Properties.setValue(entity, "size", slick.vector(1.1f, 1.1f));
						Boolean value = Properties.getValue(entity, "value");
						Properties.setValue(entity, "value", !value);
						messageQueue.enqueue(new Message("buttonReleased", new PropertiesMapBuilder() {
							{
								property("source", entity);
								property("buttonId", entity.getId());
							}
						}.build()));
					}
				});
			}
		};

		newParameters.putAll(parameters.getWrappedParameters());

		parent("gemserk.gui.button", newParameters);

		tags("checkbox");

		final UpdateableTimeProvider timeProvider = new UpdateableTimeProvider();

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});

		property("color", new InterpolatedProperty<Color>(new AutoUpdateableTransition<Color>(slick.color(0f, 0f, 1f, 0.75f), new ColorInterpolator(), 0.005f, timeProvider)));
		property("size", new InterpolatedProperty<Vector2f>(new AutoUpdateableTransition<Vector2f>(slick.vector(1f, 1f), new Vector2fInterpolator(), 0.005f, timeProvider)));

//		property("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(slick.color(0f, 0f, 1f, 0.75f)), 0.005f, timeProvider));
//		property("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(slick.vector(1f, 1f)), 0.005f, timeProvider));

		property("position", parameters.get("position"));

		property("value", parameters.get("value", false));

		property("imageFalse", parameters.get("imageFalse"));
		property("imageTrue", parameters.get("imageTrue"));
		property("layer", parameters.get("layer", 0));

		property("image", new FixedProperty(entity) {
			@Override
			public Object get() {
				Boolean value = Properties.getValue(getHolder(), "value");
				if (value)
					return Properties.getValue(getHolder(), "imageTrue");
				else
					return Properties.getValue(getHolder(), "imageFalse");
			}
		});

		component(new ImageRenderableComponent("renderer")).withProperties(new ComponentProperties() {
			{
				propertyRef("image");
				propertyRef("color", "color");
				propertyRef("size", "size");
				propertyRef("position", "position");
				property("direction", new Vector2f(1, 0));
			}
		});
	}
}