package com.gemserk.games.zombierockers.entities;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class CursorEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slickUtils;

	@Override
	public void build() {
		
		tags("cursor");

		property("image", parameters.get("image"));
		property("color", parameters.get("color"));
		property("layer", parameters.get("layer"));

		property("position", parameters.get("position"));
		property("bounds", parameters.get("bounds"));

		component(new WorldBoundsComponent("bounds")).withProperties(new ComponentProperties() {
			{
				propertyRef("bounds", "bounds");
				propertyRef("position", "position");
			}
		});

		component(new ReferencePropertyComponent("movemouse") {

			@EntityProperty
			Property<Vector2f> position;

			@Handles
			public void movemouse(Message message) {
				Float x = Properties.getValue(message, "x");
				Float y = Properties.getValue(message, "y");
				position.get().set(x, y);
			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("position", "position");
			}
		});

		component(new ImageRenderableComponent("imagerenderer")).withProperties(new ComponentProperties() {
			{
				propertyRef("image", "image");
				propertyRef("color", "color");
				propertyRef("position", "position");
				property("direction", new Vector2f(0, 1));
				propertyRef("layer", "layer");
			}
		});
		
	}
}
