package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.animation.components.UpdateTimeProviderComponent;
import com.gemserk.commons.animation.properties.InterpolatedProperty;
import com.gemserk.commons.animation.properties.InterpolatedPropertyTimeProvider;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.slick.animation.timeline.ColorInterpolatedValue;
import com.gemserk.slick.animation.timeline.Vector2fInterpolatedValue;
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
		
		final Color startColor  = parameters.get("startColor", slick.color(0f, 0f, 0f, 0f));
		final Color endColor  = parameters.get("endColor", slick.color(0f, 0f, 0f, 1f));
		
		final Vector2f startSize = parameters.get("startSize", slick.vector(0.6f, 0.6f));
		final Vector2f endSize = parameters.get("endSize", slick.vector(1.0f, 1.0f));

		final InterpolatedPropertyTimeProvider timeProvider = new InterpolatedPropertyTimeProvider();

		component(new UpdateTimeProviderComponent("updateTimeProvider")).withProperties(new ComponentProperties() {
			{
				property("timeProvider", timeProvider);
			}
		});
		
		HashMap<String, Object> newParameters = new HashMap<String, Object>() {
			{
				put("color", new InterpolatedProperty<Color>(new ColorInterpolatedValue(startColor, endColor), 0.005f, timeProvider));
				put("size", new InterpolatedProperty<Vector2f>(new Vector2fInterpolatedValue(startSize, endSize), 0.005f, timeProvider));
				put("font", new FixedProperty(entity){
					public Object get() {
						return resourceManager.get("FontBonusMessage").get();
					};
				});
			}
		};

		newParameters.putAll(parameters.getWrappedParameters());

		parent("gemserk.gui.label", newParameters);
		
		
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
					Properties.setValue(entity, "color", slick.color(0f,0f,0f,0f));
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