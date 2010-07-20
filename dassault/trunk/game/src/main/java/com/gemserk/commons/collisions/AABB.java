package com.gemserk.commons.collisions;

import java.text.MessageFormat;
import java.util.Locale;

import org.newdawn.slick.geom.Vector2f;

public class AABB {

	private Vector2f minPoint = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);

	private Vector2f maxPoint = new Vector2f(-Float.MAX_VALUE, -Float.MAX_VALUE);

	public float getMinX() {
		return minPoint.x;
	}

	public float getMinY() {
		return minPoint.y;
	}

	public float getMaxX() {
		return maxPoint.x;
	}

	public float getMaxY() {
		return maxPoint.y;
	}

	public float getCenterX() {
		return (getMinX() + getMaxX()) / 2;
	}

	public float getCenterY() {
		return (getMinY() + getMaxY()) / 2;
	}

	public float getWidth() {
		return getMaxX() - getMinX();
	}

	public float getHeight() {
		return getMaxY() - getMinY();
	}

	public AABB() {

	}

	public AABB(float minx, float miny, float maxx, float maxy) {
		include(minx, miny);
		include(maxx, maxy);
	}

	public void include(float x, float y) {
		if (minPoint.x > x)
			minPoint.x = x;

		if (minPoint.y > y)
			minPoint.y = y;

		if (maxPoint.x < x)
			maxPoint.x = x;

		if (maxPoint.y < y)
			maxPoint.y = y;
	}

	public void setCenter(float x, float y) {
		float diffX = x - getCenterX();
		float diffY = y - getCenterY();

		minPoint.x += diffX;
		maxPoint.x += diffX;

		minPoint.y += diffY;
		maxPoint.y += diffY;
	}

	public boolean collide(AABB aabb) {
		if (minPoint.x > aabb.maxPoint.x)
			return false;

		if (minPoint.y > aabb.maxPoint.y)
			return false;

		if (maxPoint.x < aabb.minPoint.x)
			return false;

		if (maxPoint.y < aabb.minPoint.y)
			return false;

		return true;
	}

	public boolean contains(AABB aabb) {
		if (!collide(aabb))
			return false;

		if (minPoint.x > aabb.minPoint.x)
			return false;

		if (minPoint.y > aabb.minPoint.y)
			return false;

		if (maxPoint.x < aabb.maxPoint.x)
			return false;

		if (maxPoint.y < aabb.maxPoint.y)
			return false;

		return true;
	}

	private static final MessageFormat format = new MessageFormat("AABB:[{0}|{1}|{2}|{3}]", Locale.US);

	@Override
	public String toString() {
		return format.format(new Object[] { getMinX(), getMinY(), getMaxX(), getMaxY() });
	}
}