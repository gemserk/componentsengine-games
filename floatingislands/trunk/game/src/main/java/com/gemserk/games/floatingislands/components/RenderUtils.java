package com.gemserk.games.floatingislands.components;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.utils.OpenGlUtils;

public class RenderUtils {
	
	public static void renderArrow(Vector2f start, Vector2f end, float startWidth, float endWidth, Color startColor, Color endColor) {
		Vector2f difference = end.copy().sub(start);

		float length = difference.length();
		float angle = (float) difference.getTheta();

		Vector2f midpoint = difference.copy().scale(0.5f).add(start);

		renderArrow(midpoint, length, startWidth, endWidth, angle, startColor, endColor);
	}

	public static void renderArrow(Vector2f position, float length, float startWidth, float endWidth, float angle, Color startColor, Color endColor) {
		glPushMatrix();
		{
			glTranslatef(position.x, position.y, 0);
			glRotatef(angle, 0, 0, 1);
			glBegin(GL_POLYGON);
			{
				OpenGlUtils.glColor(startColor);
				glVertex3f(-length / 2, -startWidth / 2, 0);
				OpenGlUtils.glColor(startColor);
				glVertex3f(-length / 2, startWidth / 2, 0);
				OpenGlUtils.glColor(endColor);
				glVertex3f(length / 2, endWidth / 2, 0);

				OpenGlUtils.glColor(endColor);
				glVertex3f((length / 2) + 10, 0, 0);

				OpenGlUtils.glColor(endColor);
				glVertex3f(length / 2, -endWidth / 2, 0);
			}
			glEnd();
		}
		glPopMatrix();
	}

}
