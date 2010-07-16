package com.gemserk.commons.slick.geom;

import org.newdawn.slick.geom.Shape;

/**
 * Slick shapes utilities
 * 
 * @author acoppes
 * 
 */
public class ShapeUtils {

	private final Shape shape;

	public ShapeUtils(Shape shape) {
		this.shape = shape;
	}

	/**
	 * Returns true if the internal shape collides with the otherShape.
	 * 
	 * @param otherShape
	 * @return
	 */
	public boolean collides(Shape otherShape) {

		if (shape.intersects(otherShape))
			return true;

		return (contains(otherShape) || new ShapeUtils(otherShape).contains(shape));
	}

	/**
	 * Returns true if the internal shape contains all points of otherShape.
	 * 
	 * @param otherShape
	 * @return
	 */
	public boolean contains(Shape otherShape) {
		for (int i = 0; i < shape.getPointCount(); i++) {
			float[] pt = shape.getPoint(i);
			if (!otherShape.contains(pt[0], pt[1]))
				return false;
		}
		return true;
	}

}