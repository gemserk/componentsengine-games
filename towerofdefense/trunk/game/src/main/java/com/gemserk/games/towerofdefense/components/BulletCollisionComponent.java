package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.componentsengine.world.World;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class BulletCollisionComponent extends Component {

	World world;

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Float> damageProperty = property("damage");

	PropertyLocator<Container> hitpointsProperty = property("hitpoints");

	@Inject
	public void setWorld(World world) {
		this.world = world;
	}

	public BulletCollisionComponent(String name) {
		super(name);
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof UpdateMessage) {
			UpdateMessage updateMessage = (UpdateMessage) message;
			update(updateMessage.getEntity(), updateMessage.getDelta());			
		}
	}

	public void update(final Entity entity, int delta) {

		Vector2f position = positionProperty.getValue(entity);

		Collection<Entity> bullets = world.getEntities(Predicates.and(
				EntityPredicates.withAllTags("bullet"), EntityPredicates
						.isNear(position, 5.0f)));

		if (bullets.size() == 0)
			return;

		Container hitpoints = hitpointsProperty.getValue(entity);

		for (Entity bullet : bullets) {
			float damage = damageProperty.getValue(bullet);
			hitpoints.remove(damage);

			world.queueRemoveEntity(bullet);
		}

	}
}