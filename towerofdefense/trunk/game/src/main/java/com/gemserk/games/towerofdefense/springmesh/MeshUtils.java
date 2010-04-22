package com.gemserk.games.towerofdefense.springmesh;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class MeshUtils {

	public static FloatBuffer createTextureArrayForMesh(int hpoints, int vpoints, float x0, float x1, float y0, float y1) {

		int size = hpoints * vpoints;

		FloatBuffer textureArray = BufferUtils.createFloatBuffer(size * 2);

		float xTexDiff = (x1 - x0) / hpoints;
		float yTexDiff = (y1 - y0) / vpoints;

		for (int i = 0; i < hpoints; i++) {
			for (int j = 0; j < vpoints; j++) {
				textureArray.put(i * xTexDiff);
				textureArray.put(j * yTexDiff);
			}
		}

		return textureArray;
	}

}