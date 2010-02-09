package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class ImageRenderableComponent extends Component {

	PropertyLocator<Image> imageLocator = property("image");

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Vector2f> directionProperty = property("direction");

	PropertyLocator<Color> renderColorProperty = Properties.property("color");

	public ImageRenderableComponent(String id) {
		super(id);
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof SlickRenderMessage) {
			SlickRenderMessage slickRenderMessage = (SlickRenderMessage) message;
			handleMessage(slickRenderMessage);
		}
	}

	public void handleMessage(SlickRenderMessage message) {
		Graphics g = message.getGraphics();
		Entity entity = message.getEntity();

		g.pushTransform();
		{
			Vector2f posEntity = positionProperty.getValue(entity);
			Color renderColor = renderColorProperty.getValue(entity, Color.white);
			g.translate(posEntity.x, posEntity.y);
			g.rotate(0, 0, (float) directionProperty.getValue(entity).getTheta());

			Image image = imageLocator.get(entity).get();
			g.drawImage(image, -(image.getWidth() / 2), -(image.getHeight() / 2), renderColor);
		}
		g.popTransform();
	}

}
