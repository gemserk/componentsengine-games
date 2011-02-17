package com.gemserk.componentsengine.commons.entities.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class ImageEntityBuilder extends EntityBuilder {

	@Override
	public void build() {

		property("image", parameters.get("image"));
		property("position", parameters.get("position"));
		property("direction", parameters.get("direction", new Vector2f(1, 0)));
		property("color", parameters.get("color", Color.white));
		property("size", parameters.get("size", new Vector2f(1, 1)));

		component(new ImageRenderableComponent("imageComponent")).withProperties(new ComponentProperties() {
			{
				propertyRef("image");
				propertyRef("position");
				propertyRef("direction");
				propertyRef("color");
				propertyRef("size");
			}
		});
		
	}
}