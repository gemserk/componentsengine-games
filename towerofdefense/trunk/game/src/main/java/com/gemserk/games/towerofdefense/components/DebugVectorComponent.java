package com.gemserk.games.towerofdefense.components;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;

public class DebugVectorComponent extends FieldsReflectionComponent {

	@EntityProperty(readOnly = true)
	List<DebugVector> vectors;

	@EntityProperty(readOnly = true, required=false)
	Boolean enabled = false;

	public DebugVectorComponent(String id) {
		super(id);
	}

	public void handleMessage(SlickRenderMessage message) {
		if (enabled) {
			Graphics g = message.getGraphics();

			for (DebugVector debugVector : vectors) {
				Vector2f start = debugVector.getPosition();
				Vector2f segment = debugVector.getVector();
				Color color = debugVector.getColor() != null ? debugVector.getColor() : Color.white;

				Line line = new Line(start.x, start.y, segment.x, segment.y, true);
				Color origColor = g.getColor();
				g.setColor(color);
				g.draw(line);
				g.setColor(origColor);
			}
		}
		vectors.clear();
	}

	public static class DebugVector {
		Vector2f position;
		Vector2f vector;
		Color color;

		public DebugVector() {
		}

		public DebugVector(Vector2f position, Vector2f vector, Color color) {
			super();
			this.position = position;
			this.vector = vector;
			this.color = color;
		}

		public Vector2f getPosition() {
			return position;
		}

		public void setPosition(Vector2f position) {
			this.position = position;
		}

		public Vector2f getVector() {
			return vector;
		}

		public void setVector(Vector2f vector) {
			this.vector = vector;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

	}
}
