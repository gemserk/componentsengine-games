package com.gemserk.games.towerofdefense;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.resources.ResourceLoader;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TowerOfDefenseComponentLoader implements ResourceLoader {

	public static class PathRendererComponent extends ReflectionComponent {

		PropertyLocator<Color> lineColorProperty = Properties.property("path.lineColor");

		PropertyLocator<List<Vector2f>> pathProperty = Properties.property("path.path");

		public PathRendererComponent(String id) {
			super(id);
		}

		public void handleMessage(SlickRenderMessage slickRenderMessage) {
			Graphics g = slickRenderMessage.getGraphics();
			Entity entity = slickRenderMessage.getEntity();

			List<Vector2f> path = pathProperty.getValue(entity);
			Color lineColor = lineColorProperty.getValue(entity, Color.white);

			if (path.size() == 0)
				return;

			g.pushTransform();
			{
				g.setColor(lineColor);
				for (int i = 0; i < path.size(); i++) {
					Vector2f source = path.get(i);
					int j = i + 1;

					if (j >= path.size())
						continue;

					Vector2f target = path.get(j);

					float lineWidth = g.getLineWidth();
					g.setLineWidth(3.0f);
					g.drawLine(source.x, source.y, target.x, target.y);
					g.setLineWidth(lineWidth);
				}
			}
			g.popTransform();
		}
	}

	@Inject
	ComponentManager componentManager;

	@Inject
	Injector injector;

	@Inject
	Input input;

	@Override
	public void load() {

		Component[] components = { new PathRendererComponent("pathrenderer"), new CircleRenderableComponent("circlerenderer") };

		for (com.gemserk.componentsengine.components.Component component : components) {
			injector.injectMembers(component);
		}

		componentManager.addComponents(components);

	}
}