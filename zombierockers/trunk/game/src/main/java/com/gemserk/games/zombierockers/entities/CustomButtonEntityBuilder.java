package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.components.UpdateTimeProviderComponent;
import com.gemserk.commons.animation.properties.InterpolatedProperty;
import com.gemserk.commons.animation.properties.InterpolatedPropertyTimeProvider;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.triggers.NullTrigger;
import com.gemserk.slick.animation.timeline.ColorInterpolatedValue;
import com.gemserk.slick.animation.timeline.Vector2fInterpolatedValue;
import com.google.inject.Inject;

public class CustomButtonEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;
	
	@Inject
	MessageQueue messageQueue;

	@SuppressWarnings("serial")
	@Override
	public void build() {

		final InterpolatedPropertyTimeProvider timeProvider = new InterpolatedPropertyTimeProvider();

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});

		HashMap<String, Object> newParameters = new HashMap<String, Object>() {
			{
				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(slick.color(0f, 0f, 1f, 0.75f)), 0.005f, timeProvider));
				put("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(slick.vector(1f, 1f)), 0.005f, timeProvider));
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
						final Entity labelEntity = (Entity) parameters[0];
						Properties.setValue(labelEntity, "size", slick.vector(1.1f, 1.1f));
						messageQueue.enqueue(new Message("buttonReleased", new PropertiesMapBuilder(){{
							property("source", labelEntity);
							property("buttonId", labelEntity.getId());
						}}.build()));
					}
				});
			}
		};
		
		newParameters.putAll(parameters);
		
		parent("gemserk.gui.labelbutton", newParameters);
	}
}