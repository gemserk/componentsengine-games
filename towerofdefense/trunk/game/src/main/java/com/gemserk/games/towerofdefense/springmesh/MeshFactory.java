package com.gemserk.games.towerofdefense.springmesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.geom.Vector2f;

public class MeshFactory {

	public static LinkedList<SpringMeshPoint> springMesh(float width, float height, int hpoints, int vpoints) {

		LinkedList<SpringMeshPoint> springPoints = new LinkedList<SpringMeshPoint>();

		float xdiff = width / hpoints;
		float ydiff = height / vpoints;

		for (int i = 0; i < hpoints; i++)
			for (int j = 0; j < vpoints; j++) {

				float x = xdiff * i;
				float y = ydiff * j;

				Vector2f position = new Vector2f(x, y);

				SpringMeshPoint springMeshPoint = new SpringMeshPoint(position);
				springPoints.add(springMeshPoint);
			}

		return springPoints;
	}

	public static QuadMesh2d quadMesh2d(int hpoints, int vpoints, FloatBuffer textureArray) {
		int size = hpoints * vpoints;

		FloatBuffer vertexArray = BufferUtils.createFloatBuffer(size * 2);
		FloatBuffer colorArray = BufferUtils.createFloatBuffer(size * 4);
		IntBuffer indicesArray = BufferUtils.createIntBuffer(size * 4);

		// create vertexs and indexes
		float xTexDiff = 1.0f / hpoints;
		float yTexDiff = 1.0f / vpoints;

		for (int i = 0; i < hpoints; i++) {
			for (int j = 0; j < vpoints; j++) {
				vertexArray.put(i);
				vertexArray.put(j);

				float xc = xTexDiff * i;
				float yc = yTexDiff * j;
				
				if (xTexDiff * i > 0.5f)
					xc = 1.0f - xTexDiff * i;

				if (yTexDiff * j > 0.5f)
					yc = 1.0f - yTexDiff * j;

				colorArray.put(0.1f);
				colorArray.put(0.1f);
				colorArray.put(xc + yc);
				colorArray.put(0.6f);
				
			}
		}

		// // to make indices for GL_QUADS
		for (int j = 0; j < vpoints - 1; j++) {
			for (int i = 0; i < hpoints - 1; i++) {

				// there is a bug in the indices mapping, something when it is not an square

				int i0 = j * hpoints + i;
				int i1 = j * hpoints + i + vpoints;
				int i2 = j * hpoints + i + vpoints + 1;
				int i3 = j * hpoints + i + 1;

				indicesArray.put(i0);
				indicesArray.put(i1);
				indicesArray.put(i2);
				indicesArray.put(i3);

				// System.out.println(MessageFormat.format("({0}, {1}, {2}, {3})", i0, i1, i2, i3));
			}
		}

		vertexArray.rewind();
		indicesArray.rewind();
		colorArray.rewind();

		if (textureArray != null)
			textureArray.rewind();

		return new QuadMesh2d(vertexArray, textureArray, colorArray, indicesArray);
	}
}