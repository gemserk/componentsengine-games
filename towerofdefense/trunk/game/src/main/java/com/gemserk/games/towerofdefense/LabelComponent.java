package com.gemserk.games.towerofdefense;

import java.text.MessageFormat;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class LabelComponent extends ReflectionComponent {

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<String> messageProperty;

	PropertyLocator<Object> valueProperty;

	PropertyLocator<Color> colorProperty;

	PropertyLocator<Font> fontProperty;

	public LabelComponent(String id) {
		super(id);
		positionProperty = Properties.property(id, "position");
		messageProperty = Properties.property(id, "message");
		valueProperty = Properties.property(id, "value");
		colorProperty = Properties.property(id, "color");
		fontProperty = Properties.property(id, "font");
	}

	public void handleMessage(SlickRenderMessage message) {
		Graphics g = message.getGraphics();

		Color color = colorProperty.getValue(entity, Color.white);
		Vector2f translation = positionProperty.getValue(entity);

		Font font = fontProperty.getValue(entity, g.getFont());

		String formatMessage = messageProperty.getValue(entity);
		String text = MessageFormat.format(formatMessage, valueProperty.getValue(entity));

		Color currentColor = g.getColor();

		Font currentFont = g.getFont();

		int textWidth = font.getWidth(text);
		int textHeight = font.getLineHeight();
		
		if (font != null)
			g.setFont(font);

		g.pushTransform();
		{
			g.setColor(color);
			g.translate(translation.x, translation.y);
			g.drawString(text, -textWidth / 2, -textHeight / 2);
		}
		g.popTransform();

		g.setColor(currentColor);
		g.setFont(currentFont);
	}
}