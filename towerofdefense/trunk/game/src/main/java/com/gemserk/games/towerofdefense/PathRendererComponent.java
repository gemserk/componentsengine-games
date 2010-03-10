/**
 * 
 */
package com.gemserk.games.towerofdefense;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class PathRendererComponent extends ReflectionComponent {

	private PropertyLocator<Color> lineColorProperty;
	
	private PropertyLocator<Float> lineWidthProperty;
	
	private PropertyLocator<Path> pathProperty;

	public PathRendererComponent(String id) {
		super(id);
		lineColorProperty = Properties.property(id, "lineColor");
		pathProperty = Properties.property(id, "path");
		lineWidthProperty = Properties.property(id, "lineWidth");
	}

	public void handleMessage(SlickRenderMessage slickRenderMessage) {
		Graphics g = slickRenderMessage.getGraphics();

		List<Vector2f> points = pathProperty.getValue(entity).getPoints();
		Color lineColor = lineColorProperty.getValue(entity, Color.white);
		Float lineWidth = lineWidthProperty.getValue(entity, 1.0f);

		if (points.size() == 0)
			return;

		g.pushTransform();
		{
			for (int i = 0; i < points.size(); i++) {
				Vector2f source = points.get(i).copy();
				
				int j = i + 1;

				if (j >= points.size())
					continue;

				Vector2f target = points.get(j);
				
				renderLine(g, source, target, lineWidth, lineColor);
			}
		}
		g.popTransform();
	}
	
	protected void renderLine(Graphics g, Vector2f p0, Vector2f p1, float width, Color color) {
		Vector2f d = p1.copy().sub(p0);
		Vector2f m = d.copy().scale(0.5f).add(p0);

		float w = d.length();
		float h = width;
		
		g.pushTransform();
		g.translate(m.x, m.y);
		g.rotate(1f, 1f, (float)d.getTheta());
		g.setColor(color);
		g.fill(new Rectangle(-w/2, -h/2, w, h));
		g.popTransform();
		
		float r = width;
		
		g.pushTransform();
		g.setColor(color);
		g.translate(p0.x, p0.y);
		// g.rotate(1f, 1f, (float)d.getTheta());
		g.fillOval(-r/2, -r/2, r, r);
		g.popTransform();
		
		g.pushTransform();
		g.setColor(color);
		g.translate(p1.x, p1.y);
		// g.rotate(1f, 1f, (float)d.getTheta());
		g.fillOval(-r/2, -r/2, r, r);
		g.popTransform();
	}
}