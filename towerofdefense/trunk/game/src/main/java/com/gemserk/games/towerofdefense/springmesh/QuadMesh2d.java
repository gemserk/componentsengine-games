package com.gemserk.games.towerofdefense.springmesh;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.newdawn.slick.geom.Vector2f;

public class QuadMesh2d {

	private final FloatBuffer vertexArray;

	private final FloatBuffer textureArray;

	private final FloatBuffer colorArray;

	private final IntBuffer indicesArray;

	static final int VERTEX_SIZE = 2;

	public FloatBuffer getVertexArray() {
		return vertexArray;
	}

	public FloatBuffer getTextureArray() {
		return textureArray;
	}

	public FloatBuffer getColorArray() {
		return colorArray;
	}

	public IntBuffer getIndicesArray() {
		return indicesArray;
	}

	public QuadMesh2d(FloatBuffer vertexArray, FloatBuffer textureArray, FloatBuffer colorArray, IntBuffer indicesArray) {
		this.vertexArray = vertexArray;
		this.textureArray = textureArray;
		this.colorArray = colorArray;
		this.indicesArray = indicesArray;
	}

	public void setPoint(int index, float x, float y) {
		vertexArray.put(index * VERTEX_SIZE, x);
		vertexArray.put(index * VERTEX_SIZE + 1, y);
	}

	public Vector2f getPoint(int index) {
		return new Vector2f(vertexArray.get(index * VERTEX_SIZE), vertexArray.get(index * VERTEX_SIZE + 1));
	}

	public void render() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		if (textureArray != null)
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(QuadMesh2d.VERTEX_SIZE, 0, getVertexArray());
		glColorPointer(4, 0, getColorArray());
		
		if (textureArray != null)
			glTexCoordPointer(2, 0, getTextureArray());

		glDrawElements(GL_QUADS, getIndicesArray());

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);

		if (textureArray != null)
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}

}