package com.gemserk.games.grapplinghookus.components;

import java.util.List;
import java.util.Map;

import com.gemserk.commons.animation.PropertyAnimation;
import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;

public class AnimationComponent extends FieldsReflectionComponent {

	@EntityProperty
	Map<String, List<PropertyAnimation>> animations;

	@EntityProperty
	String current;

	public AnimationComponent(String id) {
		super(id);
	}

	@Handles
	public void startAnimation(Message message) {
		String entityId = Properties.getValue(message, "entityId");
		if (!entity.getId().equals(entityId))
			return;

		// must stop previous animation
		List<PropertyAnimation> propertyAnimations = animations.get(current);
		for (PropertyAnimation animation : propertyAnimations)
			animation.stop();

		current = Properties.getValue(message, "animationId");
		propertyAnimations = animations.get(current);
		for (PropertyAnimation animation : propertyAnimations)
			animation.play();
	}

	@Handles
	public void update(Message message) {
		int delta = (Integer) Properties.getValue(message, "delta");
		List<PropertyAnimation> propertyAnimations = animations.get(current);
		for (PropertyAnimation animation : propertyAnimations) {
			
			// I don't want animations to be paused, I manage different animations instead...
			if (animation.isPaused())
				animation.play();
			
			animation.animate(entity, delta);

			// it depends if animation.loop?

			if (animation.isFinished())
				animation.restart();
		}
	}

}
