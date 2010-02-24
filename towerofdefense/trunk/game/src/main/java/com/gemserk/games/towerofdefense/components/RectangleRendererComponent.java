package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.utils.Container;

public class RectangleRendererComponent extends ReflectionComponent {

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<Rectangle> rectangleProperty;

	PropertyLocator<Integer> cornerRadiusProperty;

	PropertyLocator<Color> fillColorProperty;

	PropertyLocator<Color> lineColorProperty;

	public RectangleRendererComponent(String id) {
		super(id);
		positionProperty = property(id, "position");
		rectangleProperty = property(id, "rectangle");
		cornerRadiusProperty = property(id, "cornerRadius");
		fillColorProperty = property(id, "fillColor");
		lineColorProperty = property(id, "lineColor");
	}

	public void handleMessage(SlickRenderMessage message) {
		Graphics g = message.getGraphics();
		Vector2f position = positionProperty.getValue(entity);
		Rectangle rectangle = rectangleProperty.getValue(entity);
		Integer cornerRadius = cornerRadiusProperty.getValue(entity, null);
		
		Color fillColor = fillColorProperty.getValue(entity, new Color(0f, 0f, 0f, 0f));
		Color lineColor = lineColorProperty.getValue(entity, Color.white);

		g.pushTransform();
		{
			g.translate(position.x, position.y);

			Color color = g.getColor();

			if (cornerRadius != null) {
				g.setColor(fillColor);
				g.fillRoundRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), 5);
				
				g.setColor(lineColor);
				g.drawRoundRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), 5);
			} else {
				g.setColor(fillColor);
				g.fill(rectangle);
				
				g.setColor(lineColor);
				g.draw(rectangle);
			}

			g.setColor(color);

		}
		g.popTransform();
	}

}