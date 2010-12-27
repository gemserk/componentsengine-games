package com.gemserk.componentsengine.commons.entities;

import java.util.HashMap;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class FpsEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;

	@Override
	public void build() {
		
		property("enabled", parameters.get("enabled"));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_label", new HashMap<String, Object>() {
			{
				put("position", parameters.get("position", slick.vector(60, 30)));
				put("color", parameters.get("color", slick.color(0f, 0f, 0f, 1f)));
				put("bounds", parameters.get("bounds", slick.rectangle(-50f, -20f, 100f, 40f)));
				put("align", "left");
				put("valign", "top");
				put("layer", parameters.get("layer", 10000));
				put("message", new FixedProperty(entity) {
					public Object get() {
						Boolean enabled = Properties.getValue(getHolder(), "enabled");
						if (enabled)
							return "FPS: " + slick.getGameContainer().getFPS();
						return "";
					};
				});
			}
		}));

		component(new ReferencePropertyComponent("toggleFpsHandler") {
			
			@EntityProperty
			Property<Boolean> enabled;

			@Handles
			public void toggleFps(Message message) {
				enabled.set(!enabled.get());
			}

		});

	}
}