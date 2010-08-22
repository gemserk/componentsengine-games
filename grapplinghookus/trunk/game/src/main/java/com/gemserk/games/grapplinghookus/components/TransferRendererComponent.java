package com.gemserk.games.grapplinghookus.components;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.render.Renderer;
import com.gemserk.componentsengine.render.SlickCallableRenderObject;
import com.gemserk.componentsengine.utils.OpenGlUtils;

public class TransferRendererComponent extends FieldsReflectionComponent {

	@EntityProperty(required = false)
	Entity selectedDroid = null;

	@EntityProperty(required = false)
	Boolean transfering = false;

	public TransferRendererComponent(String id) {
		super(id);
	}

	@Handles
	public void render(Message message) {
		if (!transfering)
			return;

		Renderer renderer = Properties.getValue(message, "renderer");

		final Vector2f start = Properties.getValue(entity, "position");
		final Vector2f end = Properties.getValue(selectedDroid, "position");

		int layer = 0;
		final Color color = new Color(0.2f, 0.2f, 1.0f, 0.6f);

		renderer.enqueue(new SlickCallableRenderObject(layer+2) {

			@Override
			public void execute(Graphics graphics) {

				SlickCallable.enterSafeBlock();
				OpenGlUtils.renderLine(start, end, 5.0f, color);
				SlickCallable.leaveSafeBlock();

			}
		});

	}

}
