package com.gemserk.games.towerofdefense;

import java.text.MessageFormat;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class LabelComponent extends ReflectionComponent {
	
	private PropertyLocator<Vector2f> positionProperty;
	private PropertyLocator<String> messageProperty;
	private PropertyLocator<Object> valueProperty;
	private PropertyLocator<Color> colorProperty;

	public LabelComponent(String id) {
		super(id);
		positionProperty = Properties.property(id, "position");
		messageProperty = Properties.property(id, "message");
		valueProperty = Properties.property(id, "value");
		colorProperty = Properties.property(id, "color");
	}

	public void handleMessage(SlickRenderMessage message) {
		Graphics graphics = message.getGraphics();
		graphics.pushTransform();
		{
			Color color = colorProperty.getValue(entity, Color.white);
			Vector2f position = positionProperty.getValue(entity);
			String formatMessage = messageProperty.getValue(entity);
			String text = MessageFormat.format(formatMessage, valueProperty.getValue(entity));

			graphics.setColor(color);
			graphics.translate(position.x, position.y);
			graphics.drawString(text, 0, 0);
		}
		graphics.popTransform();
	}
}