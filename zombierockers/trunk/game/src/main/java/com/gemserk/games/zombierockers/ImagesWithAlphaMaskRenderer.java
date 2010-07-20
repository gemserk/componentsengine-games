package com.gemserk.games.zombierockers;

import java.util.Collection;

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

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.render.Renderer;
import com.gemserk.componentsengine.render.SlickCallableRenderObject;

public class ImagesWithAlphaMaskRenderer extends ReflectionComponent {

	public ImagesWithAlphaMaskRenderer(String id) {
		super(id);
	}

	@Handles
	public void render(Message message) {
		Renderer renderer = Properties.getValue(message, "renderer");

		final Image transparencyMap = Properties.getValue(entity, "ballrenderer.transparencyMap");
		renderer.enqueue(new SlickCallableRenderObject(0) {

			@Override
			public void execute(Graphics graphics) {
//				transparencyMap.draw(0,0);
			}
		});
		
		
		Collection<Entity> balls = entity.getRoot().getEntities(EntityPredicates.withAllTags("ball"));

		for (Entity ball : balls) {


			final Image image = Properties.getValue(ball, "currentFrame");
			int layer = (Integer) Properties.getValue(ball, "layer");
			Vector2f position = Properties.getValue(ball, "position");
			Vector2f direction = ((Vector2f) Properties.getValue(ball, "direction"));//.copy().add(-90);

			final Color color = Properties.getValue(ball, "color");
			
			
			Shape pixels = new Rectangle(-image.getWidth() / 2f, -image.getHeight() / 2f, image.getWidth(), image.getHeight());
			Transform translation = Transform.createTranslateTransform(position.x, position.y);
			Transform rotation = Transform.createRotateTransform((float)(direction.getTheta() * Math.PI / 180));
			Transform finalTransform = translation.concatenate(rotation);
			
			pixels = pixels.transform(finalTransform);

			final float[] pixelsCoords = pixels.getPoints();
			final float[] textureCoords = new float[] { image.getTextureOffsetX(), image.getTextureOffsetY(),//
					image.getTextureOffsetX(), image.getTextureOffsetY() + image.getTextureHeight(),//
					image.getTextureOffsetX() + image.getTextureWidth(), image.getTextureOffsetY() + image.getTextureHeight(),//
					image.getTextureOffsetX() + image.getTextureWidth(), image.getTextureOffsetY() //
			};

			renderer.enqueue(new SlickCallableRenderObject(layer) {

				@Override
				public void execute(Graphics graphics) {
					SlickCallable.enterSafeBlock();
					{
						GL13.glActiveTexture(GL13.GL_TEXTURE0);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, image.getTexture().getTextureID());
						GL13.glActiveTexture(GL13.GL_TEXTURE1);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, transparencyMap.getTexture().getTextureID());
						
						

						GL11.glBegin(GL11.GL_QUADS);
						{
							for (int i = 0; i < pixelsCoords.length; i+=2) {
								GL11.glColor4f(color.r, color.g, color.b, color.a);
								GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, textureCoords[i], textureCoords[i+1]);
								
								double transMapX = transparencyMap.getTextureOffsetX() + pixelsCoords[i]/800f;
								double transMapY = transparencyMap.getTextureOffsetY() + pixelsCoords[i+1]/600f;
								GL13.glMultiTexCoord2d(GL13.GL_TEXTURE1, transMapX * transparencyMap.getTextureWidth(), transMapY * transparencyMap.getTextureHeight());
								GL11.glVertex2f(pixelsCoords[i], pixelsCoords[i+1]);
							}							
						}
						GL11.glEnd();

						
						GL13.glActiveTexture(GL13.GL_TEXTURE0);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL13.glActiveTexture(GL13.GL_TEXTURE1);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
					}
					SlickCallable.leaveSafeBlock();

				}
			});

		}

	}

	public static void main(String[] args) {
		Shape pixels = new Rectangle(-20, -20, 40, 40);
		Transform translation = Transform.createTranslateTransform(100, 100);
		pixels = pixels.transform(translation);

		float[] pixelsCoords = pixels.getPoints();

		System.out.println("Done");
	}

	// component(new ImageRenderableComponent("imagerenderer")) {
	// property("image", {entity.animation.currentFrame})
	// propertyRef("color", "color")
	// propertyRef("position", "position")
	// property("direction", {entity.direction.copy().add(-90)})
	// propertyRef("size", "size")
	// propertyRef("layer", "layer")
	// }

}
