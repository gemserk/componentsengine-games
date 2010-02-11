package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.RemoveEntityMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.world.World;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class RemoveWhenNearComponent extends ReflectionComponent {

	@Inject
	World world;
	
	@Inject
	MessageQueue messageQueue;

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<Float> rangeProperty;

	public RemoveWhenNearComponent(String name) {
		super(name);

		positionProperty = property(name, "position");
		rangeProperty = property(name, "range");
	}

	public void update(Entity entity, int delta) {

		Vector2f position = positionProperty.getValue(entity);

		float range = rangeProperty.getValue(entity);

		Collection<Entity> entities = world.getEntities(Predicates.and(EntityPredicates.withAllTags("critter"), EntityPredicates.isNear(position, range)));

		for (Entity candidate : entities) {
			messageQueue.enqueue(new RemoveEntityMessage(candidate));
		}
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof UpdateMessage) {
			UpdateMessage update = (UpdateMessage) message;
			this.update(message.getEntity(), update.getDelta());
		}
	}
}