package com.gemserk.commons.slick.geom;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

import com.gemserk.commons.collisions.AABB;

/**
 * Slick shapes utilities
 * 
 * @author arielsan
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
		for (int i = 0; i < otherShape.getPointCount(); i++) {
			float[] pt = otherShape.getPoint(i);
			if (!shape.contains(pt[0], pt[1]))
				return false;
		}
		return true;
	}

	/**
	 * Returns an AABB containing all the points of the shape.
	 * @return
	 */
	public AABB getAABB() {
		AABB aabb = new AABB();

		for (int i = 0; i < shape.getPointCount(); i++) {
			float[] pt = shape.getPoint(i);

			float x = pt[0];
			float y = pt[1];

			aabb.include(x, y);
		}
		
		return aabb;
	}

	/**
	 * Return a clone of the shape
	 * 
	 * @param shape
	 * @return
	 */
	public Shape clone(Shape shape) {
		return shape.transform(new Transform());
	}

}