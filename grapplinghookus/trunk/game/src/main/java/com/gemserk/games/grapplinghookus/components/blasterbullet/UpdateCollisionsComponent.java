package com.gemserk.games.grapplinghookus.components.blasterbullet;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;

public class UpdateCollisionsComponent extends FieldsReflectionComponent {

	@EntityProperty
	Shape bounds;

	@EntityProperty
	Vector2f position;

	public UpdateCollisionsComponent(String id) {
		super(id);
	}

	@Handles
	public void update(Message message) {
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
}
