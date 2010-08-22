package com.gemserk.commons.slick.util;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public interface SlickCamera {
	
	Vector2f getZoom();

	Vector2f getPosition();

	void moveTo(Vector2f position);
	
	void moveTo(float x, float y);

	void zoomTo(Vector2f zoom);
	
	Vector2f getWorldPositionFromScreenPosition(Vector2f position);

	void pushTransform(Graphics g);

	void popTransform(Graphics g);
	
}