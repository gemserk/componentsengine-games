package com.gemserk.games.zombierockers.entities;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class PlaceableEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;

	@Override
	public void build() {
		
		tags("placeable");
		
		property("position", parameters.get("position"));
		property("image", parameters.get("image"));
		property("layer", parameters.get("layer"));
		
		component(new ImageRenderableComponent("logo")).withProperties(new ComponentProperties() {
			{
				propertyRef("position");
				propertyRef("image");
				propertyRef("layer");
				property("color", slick.color(1f, 1f, 1f, 1f));
				property("direction", slick.vector(1, 0));
			}
		});

	}
}
