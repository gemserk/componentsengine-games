package com.gemserk.games.towerofdefense.components;

import java.util.Collection;

import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.RemoveEntityMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.world.World;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class OutOfBoundsRemover extends Component {

	private PropertyLocator<Rectangle> boundsProperty;

	private PropertyLocator<String[]> tagsProperties;

	@Inject
	World world;

	@Inject
	MessageQueue messageQueue;

	public OutOfBoundsRemover(String id) {
		super(id);
		boundsProperty = Properties.property(id, "bounds");
		tagsProperties = Properties.property(id, "tags");
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof UpdateMessage) {

			String[] tags = tagsProperties.getValue(message.getScene());

			Rectangle worldBounds = boundsProperty.getValue(message.getScene());

			Collection<Entity> entitiesToRemove = world.getEntities(Predicates.and(EntityPredicates.withAnyTag(tags), Predicates.not(EntityPredicates.isIn(worldBounds))));

			for (Entity entityToRemove : entitiesToRemove) {
				System.out.println("removing entity from out of bounds: " + entitiesToRemove);
				messageQueue.enqueue(new RemoveEntityMessage(entityToRemove));
			}

		}
	}

}
