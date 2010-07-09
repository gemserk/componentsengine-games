package com.gemserk.games.dosdewinia;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.newdawn.slick.geom.Vector2f;

public class TraversalMap {

	private boolean[] traversalData;
	private int height;
	private int width;

	public static void main(String[] args) throws IOException {
		TraversalMap traversalMap = new TraversalMap(Thread.currentThread().getContextClassLoader().getResourceAsStream("levels/passage/terrainMap.png"));
		System.out.println(traversalMap.getTraversable(new Vector2f(650, 400)));

		BufferedImage image = new BufferedImage(traversalMap.width, traversalMap.height, BufferedImage.TYPE_INT_ARGB);
		for (int line = 0; line < traversalMap.height; line++) {
			for (int column = 0; column < traversalMap.width; column++) {
				image.setRGB(column, line, traversalMap.getTraversable(column, line) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
			}

		}
		System.out.println("Width: " + traversalMap.width + " - Height: " + traversalMap.height);
		ImageIO.write(image, "png", File.createTempFile("test","image"));
		System.out.println("DONE");
	}

	public TraversalMap(InputStream imageStream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(imageStream);
		height = bufferedImage.getHeight();
		width = bufferedImage.getWidth();

		int[] data = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

		traversalData = new boolean[data.length];
		long iniTime = System.nanoTime();
		for (int line = 0; line < height; line++) {
			for (int column = 0; column < width; column++) {
				int offset = getOffset(column, line);
				int value = data[offset];
				int r = (value & 0x00FF0000) >> 16;
				// int g = (value & 0x0000FF00) >> 8;
				// int b = (value & 0x000000FF);
				// int a = (value & 0xFF000000) >> 24;

				// if (a < 0) {
				// a += 256;
				// }
				// if (a == 0) {
				// a = 255;
				// }

				if (r > 0)
					traversalData[offset] = true;
				else
					traversalData[offset] = false;

				// System.out.println("(" + r + "," + g + "," + b + "," + a + ")");
			}
		}

		System.out.println("TIMETOLOAD: " + (System.nanoTime() - iniTime) / 1000000f);

	}

	int getOffset(int column, int line) {
		return width * line + column;
	}

	boolean getTraversable(int x, int y) {
		if (x < 0 || y < 0 || x > width || y > height)
			return false;

		return traversalData[getOffset(x, y)];
	}

	boolean getTraversable(Vector2f position) {
		return getTraversable((int) position.x, (int) position.y);
	}
}
