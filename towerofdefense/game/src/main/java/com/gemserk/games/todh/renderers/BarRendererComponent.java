package com.gemserk.games.todh.renderers;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.todh.components.Component;

public class BarRendererComponent extends Component {

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<Container> containerProperty;

	public BarRendererComponent(String name) {
		super(name);
		positionProperty = property(id, "position");
		containerProperty = property(id, "container");
	}

	@Override
	public void render(Graphics g, Entity entity) {

		Vector2f position = positionProperty.getValue(entity);
		Container hitpoints = containerProperty.getValue(entity);

		{
			g.pushTransform();

			g.translate(position.x, position.y - 10);
			g.scale(10.0f, 3.0f);

			g.setColor(Color.red);
			g.fillRect(0, 0, hitpoints.getTotal() / 10.0f, 2);

			g.setColor(Color.green);
			g.fillRect(0, 0, hitpoints.getCurrent() / 10.0f, 2);

			g.popTransform();
		}
	}

}