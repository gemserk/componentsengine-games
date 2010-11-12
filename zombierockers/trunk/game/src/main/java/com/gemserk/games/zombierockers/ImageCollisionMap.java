package com.gemserk.games.zombierockers;

import java.awt.image.BufferedImage;

public class ImageCollisionMap {

	private final BufferedImage image;

	public ImageCollisionMap(BufferedImage image) {
		this.image = image;
	}

	public int collides(float x, float y) {
		int xint = (int)x;
		int yint = (int)y;
		if(xint < 0 || xint > image.getWidth() || yint < 0 || yint > image.getHeight())
			return 0;
		int value = image.getRGB(xint,yint);
		return value == -1 ? 1 : 0;
	}
	
}
