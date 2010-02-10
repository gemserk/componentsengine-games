package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class EntityRendererComponent extends TodComponent {

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Float> sizeProperty = property("size");

	PropertyLocator<Color> colorProperty = property("color");

	public void setSizePropertyName(String name) {
		this.sizeProperty.setName(name);
	}

	public EntityRendererComponent(String name) {
		super(name);
	}

	@Override
	public void render(Graphics g, Entity entity) {

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