package com.gemserk.games.towerofdefense;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.resources.ResourceLoader;
import com.gemserk.componentsengine.world.World;
import com.gemserk.games.towerofdefense.components.RemoveWhenNearComponent;
import com.gemserk.games.towerofdefense.components.SpawnerComponent;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TowerOfDefenseComponentLoader implements ResourceLoader {

	public static class PathRendererComponent extends ReflectionComponent {

		PropertyLocator<Color> lineColorProperty = Properties.property("path.lineColor");

		PropertyLocator<Path> pathProperty = Properties.property("path.path");

		public PathRendererComponent(String id) {
			super(id);
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

	public static class FollowPathComponent extends ReflectionComponent {

		@Inject
		World world;

		PropertyLocator<String> pathEntityIdProperty = Properties.property("followpath", "pathEntityId");

		PropertyLocator<String> pathProperty = Properties.property("followpath", "path");

		PropertyLocator<Integer> pathIndexProperty = Properties.property("followpath", "pathindex");

		PropertyLocator<Vector2f> forceProperty = Properties.property("followpath", "force");

		PropertyLocator<Vector2f> positionProperty = Properties.property("followpath", "position");

		public FollowPathComponent(String id) {
			super(id);
		}

		public void handleMessage(UpdateMessage updateMessage) {
			Entity entity = updateMessage.getEntity();

			Path path = getPath(entity);

			Vector2f position = positionProperty.getValue(entity);
			Integer pathIndex = pathIndexProperty.getValue(entity);

			int nextPathIndex = path.getNextIndex(position, pathIndex);
			pathIndexProperty.setValue(entity, nextPathIndex);

			Vector2f nextPosition = path.getPoint(nextPathIndex);

			Vector2f direction = nextPosition.copy().sub(position).normalise();
			Vector2f currentForce = forceProperty.getValue(entity).copy();

			currentForce.add(direction.scale(1000f));

			forceProperty.setValue(entity, currentForce);
		}

		private Path getPath(Entity entity) {
			String pathEntityId = pathEntityIdProperty.getValue(entity);
			Entity pathEntity = world.getEntityById(pathEntityId);

			String pathPropertyString = pathProperty.getValue(entity);
			Path path = (Path) Properties.property(pathPropertyString).getValue(pathEntity);
			return path;
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

		Component[] components = { new PathRendererComponent("pathrenderer"), // 
				new CircleRenderableComponent("circlerenderer"),// 
				new SuperMovementComponent("movement"),//
				new FollowPathComponent("followpath"),//
				new ImageRenderableComponent("imagerenderer"),//
				new RemoveWhenNearComponent("remover"),//
				new SpawnerComponent("creator"),//
				};

		for (com.gemserk.componentsengine.components.Component component : components) {
			injector.injectMembers(component);
		}

		componentManager.addComponents(components);

	}
}