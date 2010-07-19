package com.gemserk.commons.slick.geom;

public interface Bounds {

	void setPosition(float x, float y);

	boolean collides(Bounds bounds);

}
