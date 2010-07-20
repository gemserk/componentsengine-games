package com.gemserk.games.zombierockers.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.opengl.Texture;

import com.gemserk.componentsengine.render.SlickCallableRenderObject;

public class AlphaMaskedSpritesRenderObject extends SlickCallableRenderObject {
	final Image alphaMask;
	final List<AlphaMaskedSprite> sprites;

	public AlphaMaskedSpritesRenderObject(int layer, Image alphaMask, List<AlphaMaskedSprite> sprites) {
		super(layer);
		this.alphaMask = alphaMask;
		this.sprites = sprites;
	}

	@Override
	public void execute(Graphics graphics) {
		if (sprites.isEmpty())
			return;

		SlickCallable.enterSafeBlock();

		Image spriteImage = sprites.get(0).getImage();
		Texture spriteTexture = spriteImage.getTexture();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, spriteTexture.getTextureID());
		if (alphaMask != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, alphaMask.getTexture().getTextureID());
		}

		for (AlphaMaskedSprite sprite : sprites) {
			Image image = sprite.getImage();
			Vector2f position = sprite.getPosition();
			Vector2f direction = sprite.getDirection();
			Color color = sprite.getColor();

			Shape pixels = new Rectangle(-image.getWidth() / 2f, -image.getHeight() / 2f, image.getWidth(), image.getHeight());
			Transform translation = Transform.createTranslateTransform(position.x, position.y);
			Transform rotation = Transform.createRotateTransform((float) (direction.getTheta() * Math.PI / 180));
			Transform finalTransform = translation.concatenate(rotation);

			pixels = pixels.transform(finalTransform);

			final float[] pixelsCoords = pixels.getPoints();
			final float[] textureCoords = new float[] { image.getTextureOffsetX(), image.getTextureOffsetY(),//
					image.getTextureOffsetX(), image.getTextureOffsetY() + image.getTextureHeight(),//
					image.getTextureOffsetX() + image.getTextureWidth(), image.getTextureOffsetY() + image.getTextureHeight(),//
					image.getTextureOffsetX() + image.getTextureWidth(), image.getTextureOffsetY() //
			};

			GL11.glBegin(GL11.GL_QUADS);
			{
				for (int i = 0; i < pixelsCoords.length; i += 2) {
					GL11.glColor4f(color.r, color.g, color.b, color.a);
					GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, textureCoords[i], textureCoords[i + 1]);

					if (alphaMask != null) {
						double transMapX = alphaMask.getTextureOffsetX() + pixelsCoords[i] / alphaMask.getWidth();
						double transMapY = alphaMask.getTextureOffsetY() + pixelsCoords[i + 1] / alphaMask.getHeight();
						GL13.glMultiTexCoord2d(GL13.GL_TEXTURE1, transMapX * alphaMask.getTextureWidth(), transMapY * alphaMask.getTextureHeight());
						GL11.glVertex2f(pixelsCoords[i], pixelsCoords[i + 1]);
					}
				}
			}
			GL11.glEnd();
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if (alphaMask != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		SlickCallable.leaveSafeBlock();

	}

	public Image getAlphaMask() {
		return alphaMask;
	}

	public List<AlphaMaskedSprite> getSprites() {
		return sprites;
	}
	
	
}