package com.gemserk.games.towerofdefense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class GenericHitComponent extends ReflectionComponent {

	private PropertyLocator<MessageBuilder> messageBuilderProperty;

	private PropertyLocator<String> targetTagProperty;

	private PropertyLocator<Predicate<Entity>> predicateProperty;
	
	@Inject	@Root 
	Entity  rootEntity;
	
	@Inject
	MessageQueue messageQueue;


	public GenericHitComponent(String id) {
		super(id);

		messageBuilderProperty = Properties.property(id, "messageBuilder");
		targetTagProperty = Properties.property(id, "targetTag");
		predicateProperty = Properties.property(id,"predicate");

	}

	public void handleMessage(final UpdateMessage message) {


		String targetTags = targetTagProperty.getValue(entity);

		Predicate<Entity> areaPredicate = predicateProperty.getValue(entity);
		final Collection<Entity> candidates = rootEntity.getEntities(Predicates.and(EntityPredicates.withAllTags(targetTags), areaPredicate));

		if (candidates.size() == 0)
			return;

		MessageBuilder messageBuilder = messageBuilderProperty.getValue(entity);

		Message hitMessage = messageBuilder.build(new HashMap<String, Object>() {
			{
				put("source", entity);
				put("targets", new ArrayList<Entity>(candidates));
				put("delta",message.getDelta());
			}
		});

		messageQueue.enqueue(hitMessage);
	}

}