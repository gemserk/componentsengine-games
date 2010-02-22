package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class EntityRendererComponent extends ReflectionComponent {

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Float> sizeProperty = property("size");

	PropertyLocator<Color> colorProperty = property("color");

	public void setSizePropertyName(String name) {
		this.sizeProperty.setName(name);
	}

	public EntityRendererComponent(String name) {
		super(name);
	}

	public void handleMessage(SlickRenderMessage message) {
		Graphics g = message.getGraphics();
		float size = this.sizeProperty.getValue(entity);
		Color color = this.colorProperty.getValue(entity);
		
		g.setColor(color);
		g.pushTransform();
		Vector2f entityPosition = positionProperty.getValue(entity);
		g.translate(entityPosition.x - size / 2, entityPosition.y - size / 2);
		g.fillOval(0.0f, 0.0f, size, size, 10);
		g.popTransform();
	}

	

}