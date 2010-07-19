package com.gemserk.commons.slick.geom;

import org.newdawn.slick.geom.Shape;

public class BoundsSlickImpl implements Bounds {

	private static final long serialVersionUID = 5056611441138540196L;

	private final Shape shape;

	private final Shape childShape;

	public BoundsSlickImpl(Shape shape, Shape childShape) {
		this.shape = shape;
		this.childShape = childShape;
	}

	public void setPosition(float x, float y) {
		shape.setCenterX(x);
		shape.setCenterY(y);

		childShape.setCenterX(x);
		childShape.setCenterY(y);
	}

	public boolean collides(Bounds otherBounds) {
		BoundsSlickImpl bounds = (BoundsSlickImpl) otherBounds;
		
		if (!new ShapeUtils(shape).collides(bounds.shape))
			return false;

//		if (!new ShapeUtils(shape).collides(bounds.childShape))
//			return false;
//
//		if (!new ShapeUtils(childShape).collides(bounds.shape)) 
//			return false;
		
		if (childShape == null)
			return true;

		if (bounds.childShape == null)
			return true;
		
		return new ShapeUtils(childShape).collides(bounds.childShape);
	}

}
