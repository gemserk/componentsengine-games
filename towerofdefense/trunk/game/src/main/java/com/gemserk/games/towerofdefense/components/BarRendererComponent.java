package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.utils.Container;

public class BarRendererComponent extends ReflectionComponent {

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<Container> containerProperty;

	public BarRendererComponent(String name) {
		super(name);
		positionProperty = property(id, "position");
		containerProperty = property(id, "container");
	}

	public void handleMessage(SlickRenderMessage message) {
		Graphics g = message.getGraphics();
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