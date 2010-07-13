package com.gemserk.games.dassault.components;

import java.util.List;

import com.gemserk.commons.animation.PropertyAnimation;
import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;

public class AnimationComponent extends FieldsReflectionComponent {

	@EntityProperty
	List<PropertyAnimation> animations;

	@EntityProperty
	String id;

	public AnimationComponent(String id) {
		super(id);
	}

	@Handles
	public void startAnimation(Message message) {
		String entityId = Properties.getValue(message, "entityId");
		if (!entity.getId().equals(entityId))
			return;
		String animationId = Properties.getValue(message, "animationId");
		if (!id.equals(animationId))
			return;
		for (PropertyAnimation animation : animations)
			animation.play();
	}

	@Handles
	public void stopAnimation(Message message) {
		String entityId = Properties.getValue(message, "entityId");
		if (!entity.getId().equals(entityId))
			return;
		String animationId = Properties.getValue(message, "animationId");
		if (!id.equals(animationId))
			return;
		for (PropertyAnimation animation : animations)
			animation.stop();
	}

	@Handles
	public void update(Message message) {
		int delta = (Integer) Properties.getValue(message, "delta");
		for (PropertyAnimation animation : animations) {
			if (animation.isPaused())
				continue;
			animation.animate(entity, delta);

			// it depends if animation.loop?

			if (animation.isFinished())
				animation.restart();
		}
	}

}
