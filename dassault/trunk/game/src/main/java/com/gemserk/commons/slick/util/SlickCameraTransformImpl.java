package com.gemserk.commons.slick.util;

import java.text.MessageFormat;
import java.util.Locale;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class SlickCameraTransformImpl implements SlickCamera {

	Transform translationMatrix;

	Transform inverseTranslationMatrix;

	Transform scaleMatrix;

	Transform inverseScaleMatrix;

	private final float centerX;

	private final float centerY;

	public SlickCameraTransformImpl(float x, float y) {
		this.centerX = x;
		this.centerY = y;
		translationMatrix = Transform.createTranslateTransform(0, 0);
		inverseTranslationMatrix = Transform.createTranslateTransform(0, 0);

		scaleMatrix = Transform.createScaleTransform(1f, 1f);
		inverseScaleMatrix = Transform.createScaleTransform(1f, 1f);
	}

	public Vector2f getZoom() {
		return scaleMatrix.transform(new Vector2f(1f, 1f));
	}

	public Vector2f getPosition() {
		return translationMatrix.transform(new Vector2f(0f, 0f));
	}

	public void moveTo(Vector2f position) {
		moveTo(position.x, position.y);
	}

	@Override
	public void zoomTo(Vector2f zoom) {
		scaleMatrix = Transform.createScaleTransform(zoom.x, zoom.y);
		inverseScaleMatrix = Transform.createScaleTransform(1 / zoom.x, 1 / zoom.y);
	}

	public Vector2f getWorldPositionFromScreenPosition(Vector2f position) {
		Vector2f newPosition = position.sub(new Vector2f(centerX, centerY));
		return inverseTranslationMatrix.transform(inverseScaleMatrix.transform(newPosition));
	}

	@Override
	public void moveTo(float x, float y) {
		translationMatrix = Transform.createTranslateTransform(-x, -y);
		inverseTranslationMatrix = Transform.createTranslateTransform(x, y);
	}

	MessageFormat matrixFormat = new MessageFormat("[{0},{1},{2}]\n[{3},{4},{5}]", Locale.US);

	@Override
	public String toString() {
		float[] matrix = translationMatrix.getMatrixPosition();
		return matrixFormat.format(new Object[] { matrix[0], matrix[1], matrix[2],//
				matrix[3], matrix[4], matrix[5] });
	}

	@Override
	public void pushTransform(Graphics g) {
		g.pushTransform();

		Vector2f zoom = getZoom();
		Vector2f position = getPosition();

		g.translate(centerX, centerY);
		g.scale(zoom.x, zoom.y);
		g.translate(position.x, position.y);
	}

	@Override
	public void popTransform(Graphics g) {
		g.popTransform();
	}

}