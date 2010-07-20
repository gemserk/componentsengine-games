package com.gemserk.games.zombierockers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.render.Renderer;
import com.gemserk.componentsengine.render.SlickCallableRenderObject;
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSprite;
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSpritesRenderObject;

public class ImagesWithAlphaMaskRenderer extends ReflectionComponent {

	public ImagesWithAlphaMaskRenderer(String id) {
		super(id);
	}

	@Handles
	public void render(Message message) {
		Renderer renderer = Properties.getValue(message, "renderer");

		final Image transparencyMap = Properties.getValue(entity, "ballrenderer.transparencyMap");
		renderer.enqueue(new SlickCallableRenderObject(0) {

			@Override
			public void execute(Graphics graphics) {
				// transparencyMap.draw(0,0);
			}
		});

		Collection<Entity> balls = entity.getRoot().getEntities(EntityPredicates.withAllTags("ball"));

		if(balls.isEmpty())
			return;
		
		
		int layer = (Integer) balls.iterator().next().getProperty("layer").get();
		
		AlphaMaskedSpritesRenderObject alphaMappedSpritesRenderObject = new AlphaMaskedSpritesRenderObject(layer, transparencyMap, new ArrayList<AlphaMaskedSprite>(balls.size()));
		List<AlphaMaskedSprite> sprites = alphaMappedSpritesRenderObject.getSprites();
		
		
		for (Entity ball : balls) {

			final Image image = Properties.getValue(ball, "currentFrame");
			Vector2f position = Properties.getValue(ball, "position");
			Vector2f direction = ((Vector2f) Properties.getValue(ball, "direction"));// .copy().add(-90);

			final Color color = Properties.getValue(ball, "color");

			sprites.add(new AlphaMaskedSprite(image, position, direction, color));
		}
		
		renderer.enqueue(alphaMappedSpritesRenderObject);

	}

	// component(new ImageRenderableComponent("imagerenderer")) {
	// property("image", {entity.animation.currentFrame})
	// propertyRef("color", "color")
	// propertyRef("position", "position")
	// property("direction", {entity.direction.copy().add(-90)})
	// propertyRef("size", "size")
	// propertyRef("layer", "layer")
	// }

}
