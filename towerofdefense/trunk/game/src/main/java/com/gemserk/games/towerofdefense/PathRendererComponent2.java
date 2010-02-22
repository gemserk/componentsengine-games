/**
 * 
 */
package com.gemserk.games.towerofdefense;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class PathRendererComponent2 extends ReflectionComponent {

	private PropertyLocator<Color> lineColorProperty;
	
	private PropertyLocator<Float> lineWidthProperty;
	
	private PropertyLocator<Path> pathProperty;

	public PathRendererComponent2(String id) {
		super(id);
		lineColorProperty = Properties.property(id, "lineColor");
		pathProperty = Properties.property(id, "path");
		lineWidthProperty = Properties.property(id, "lineWidth");
	}

	public void handleMessage(SlickRenderMessage slickRenderMessage) {
		Graphics g = slickRenderMessage.getGraphics();

		List<Vector2f> points = pathProperty.getValue(entity).getPoints();
		Color lineColor = lineColorProperty.getValue(entity, Color.white);
		Float lineWidth = lineWidthProperty.getValue(entity, 1.0f);

		if (points.size() == 0)
			return;

		g.pushTransform();
		{
			SlickCallable.enterSafeBlock();

			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_POINT_SMOOTH);
			
			FloatBuffer floatBuffer = FloatBuffer.allocate(16);
			GL11.glGetFloat(GL12.GL_SMOOTH_LINE_WIDTH_RANGE, floatBuffer);
			for (float f : floatBuffer.array()) {
				System.out.print(f + ", ");	
			}

			for (int i = 0; i < points.size(); i++) {
				Vector2f source = points.get(i);
				int j = i + 1;

				if (j >= points.size())
					continue;

				Vector2f target = points.get(j);

				GL11.glPointSize(lineWidth);
				GL11.glLineWidth(lineWidth);

//				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_COLOR);

				GL11.glColor4f(lineColor.r, lineColor.g, lineColor.b, lineColor.a);

				GL11.glBegin(GL11.GL_LINES);
				{
					GL11.glVertex2f(source.x, source.y);
					GL11.glVertex2f(target.x, target.y);
				}
				GL11.glEnd();
				
//				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_COLOR);

				GL11.glBegin(GL11.GL_POINTS);
				{
					GL11.glVertex2f(target.x, target.y);
				}
				GL11.glEnd();

			}

			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_POINT_SMOOTH);
			GL11.glDepthMask(true);

			SlickCallable.leaveSafeBlock();
		}
		g.popTransform();
	}
}