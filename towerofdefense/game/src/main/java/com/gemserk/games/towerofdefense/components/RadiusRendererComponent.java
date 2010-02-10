package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class RadiusRendererComponent extends TodComponent {

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Float> radiusProperty = property("radius");

	public RadiusRendererComponent(String name) {
		super(name);
	}

	@Override
	public void render(Graphics g, Entity entity) {

		Vector2f position = positionProperty.getValue(entity);
		float radius = radiusProperty.getValue(entity);

		Color lineColor = new Color(0.0f, 0.0f, 0.7f, 0.7f);
		Color fillColor = new Color(0.0f, 0.0f, 1.0f, 0.15f);

		g.pushTransform();
		{
			g.translate(position.getX(), position.getY());

			g.setColor(fillColor);
			g.fillOval(-radius, -radius, 2 * radius, 2 * radius);

			g.setColor(lineColor);
			float lineWidth = g.getLineWidth();
			g.setLineWidth(2.0f);
			g.drawOval(-radius, -radius, 2 * radius, 2 * radius);
			g.setLineWidth(lineWidth);
		}
		g.popTransform();
	}

}