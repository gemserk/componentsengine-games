package com.gemserk.games.towerofdefense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class HitComponent extends ReflectionComponent {

	private PropertyLocator<Vector2f> positionProperty;

	private PropertyLocator<Float> radiusProperty;

	private PropertyLocator<MessageBuilder> messageBuilderProperty;

	private PropertyLocator<String> targetTagProperty;

	@Inject	@Root 
	Entity  rootEntity;
	
	@Inject
	MessageQueue messageQueue;

	public HitComponent(String id) {
		super(id);

		positionProperty = Properties.property(id, "position");
		radiusProperty = Properties.property(id, "radius");
		messageBuilderProperty = Properties.property(id, "messageBuilder");
		targetTagProperty = Properties.property(id, "targetTag");

	}

	public void handleMessage(UpdateMessage message) {

		final Entity entity = message.getEntity();

		Vector2f position = positionProperty.getValue(entity);
		Float radius = radiusProperty.getValue(entity);
		String targetTags = targetTagProperty.getValue(entity);

		final Collection<Entity> candidates = rootEntity.getEntities(Predicates.and(EntityPredicates.withAllTags(targetTags), EntityPredicates.isNear(position, radius)));

		if (candidates.size() == 0)
			return;

		MessageBuilder messageBuilder = messageBuilderProperty.getValue(entity);

		Message hitMessage = messageBuilder.build(new HashMap<String, Object>() {
			{
				put("source", entity);
				put("targets", new ArrayList<Entity>(candidates));
			}
		});

		messageQueue.enqueue(hitMessage);
	}

}