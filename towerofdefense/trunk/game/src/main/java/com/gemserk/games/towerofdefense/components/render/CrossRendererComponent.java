package com.gemserk.games.towerofdefense.components.render;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.utils.OpenGlUtils;

public class CrossRendererComponent extends FieldsReflectionComponent {

	@EntityProperty(readOnly = true)
	Vector2f position;

	@EntityProperty(readOnly = true, required = false)
	Float width = 1.0f;

	@EntityProperty(readOnly = true)
	Float radius;

	@EntityProperty(readOnly = true, required = false)
	Color color = Color.white;

	public CrossRendererComponent(String id) {
		super(id);
	}

	public void handleMessage(SlickRenderMessage message) {
		SlickCallable.enterSafeBlock();
		OpenGlUtils.renderLine(position.copy().add(new Vector2f(-radius, 0f)), position.copy().add(new Vector2f(radius, 0f)), 2f, color);
		OpenGlUtils.renderLine(position.copy().add(new Vector2f(0f, -radius)), position.copy().add(new Vector2f(0f, radius)), 2f, color);
		SlickCallable.leaveSafeBlock();
	}
}
