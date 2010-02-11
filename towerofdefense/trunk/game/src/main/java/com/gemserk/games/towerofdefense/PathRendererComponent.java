/**
 * 
 */
package com.gemserk.games.towerofdefense;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class PathRendererComponent extends ReflectionComponent {



	private PropertyLocator<Color> lineColorProperty;
	private PropertyLocator<Path> pathProperty;

	public PathRendererComponent(String id) {
		super(id);
		lineColorProperty = Properties.property(id, "lineColor");

		pathProperty = Properties.property(id, "path");
	}

	public void handleMessage(SlickRenderMessage slickRenderMessage) {
		Graphics g = slickRenderMessage.getGraphics();
		Entity entity = slickRenderMessage.getEntity();

		List<Vector2f> points = pathProperty.getValue(entity).getPoints();
		Color lineColor = lineColorProperty.getValue(entity, Color.white);

		if (points.size() == 0)
			return;

		g.pushTransform();
		{
			g.setColor(lineColor);
			for (int i = 0; i < points.size(); i++) {
				Vector2f source = points.get(i);
				int j = i + 1;

				if (j >= points.size())
					continue;

				Vector2f target = points.get(j);

				float lineWidth = g.getLineWidth();
				g.setLineWidth(3.0f);
				g.drawLine(source.x, source.y, target.x, target.y);
				g.setLineWidth(lineWidth);
			}
		}
		g.popTransform();
	}
}