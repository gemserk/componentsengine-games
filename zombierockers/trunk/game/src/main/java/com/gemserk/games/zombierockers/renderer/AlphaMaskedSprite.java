package com.gemserk.games.zombierockers.renderer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class AlphaMaskedSprite {
	final Image image;
	final Vector2f position;
	final Vector2f direction;
	final Color color;

	public AlphaMaskedSprite(Image image, Vector2f position, Vector2f direction, Color color) {
		super();
		this.image = image;
		this.position = position;
		this.direction = direction;
		this.color = color;
	}

	public Image getImage() {
		return image;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getDirection() {
		return direction;
	}

	public Color getColor() {
		return color;
	}
	
	
}