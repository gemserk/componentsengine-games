package com.gemserk.games.zombierockers.entities;

import java.util.Map;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover;
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class WorldEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(WorldEntityBuilder.class);

	@Inject
	MessageQueue messageQueue;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	SlickSvgUtils slickSvgUtils;

	@Inject
	SlickUtils slick;

	@Override
	public void build() {

		tags("world");

		final Rectangle screenBounds = (Rectangle) parameters.get("screenBounds");
		final Map<String, Object> level = (Map<String, Object>) parameters.get("level");

		property("bounds", screenBounds);
		property("ballsQuantity", 0);
		property("baseReached", false);

		property("level", parameters.get("level"));

		property("path", new Path(slickSvgUtils.loadPoints((String) level.get("path"), "path")));

		component(new OutOfBoundsRemover("outofboundsremover")).withProperties(new ComponentProperties() {
			{
				property("tags", new String[] { "bullet" });
				propertyRef("bounds");
			}
		});

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("image", slick.getResources().image((String) level.get("background")));
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", -1000);
			}
		});
		
		

	}
}
