package com.gemserk.games.dosdewinia;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.newdawn.slick.geom.Vector2f;

public class ZonesMap {

	private int[] zoneData;
	private int height;
	private int width;
	private int baseValue;

	public static void main(String[] args) throws IOException {
		ZonesMap traversalMap = new ZonesMap(Thread.currentThread().getContextClassLoader().getResourceAsStream("levels/passage/terrainMap.png"));
		System.out.println("DONE");
	}

	
	public ZonesMap(InputStream imageStream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(imageStream);
		height = bufferedImage.getHeight();
		width = bufferedImage.getWidth();

		zoneData = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
		baseValue = zoneData[0];
	}

	
	int getOffset(int column, int line) {
		return width * line + column;
	}
	
	
	
	int getZoneValue(int x, int y){
		if(x < 0 || y < 0 || x >= width || y >= height)
			return baseValue;
		
		return zoneData[getOffset(x,y)];
	}
	
	int getZoneValue(Vector2f position){
		return getZoneValue((int)position.x, (int)position.y);
	}
}
