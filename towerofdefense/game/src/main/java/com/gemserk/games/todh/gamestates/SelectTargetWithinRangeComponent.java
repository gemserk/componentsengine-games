package com.gemserk.games.todh.gamestates;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.world.World;
import com.gemserk.games.todh.components.Component;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class SelectTargetWithinRangeComponent extends Component {

	@Inject
	World world;

	public SelectTargetWithinRangeComponent(String name) {
		super(name);
	}

	@Override
	public void update(Entity entity, int delta) {

		String targetTag = (String) Properties.property(id, "targetTag")
				.getValue(entity);

		Vector2f position = (Vector2f) Properties.property("position")
				.getValue(entity);

		float radius = (Float) Properties.property(id, "visibleRadius")
				.getValue(entity);

		Collection<Entity> targetEntities = world.getEntities(Predicates
				.and(EntityPredicates.withAllTags(targetTag),
						EntityPredicates.isNear(position, radius)));

		if (targetEntities.size() == 0) {
			Properties.property(id, "targetEntity").setValue(entity, null);
			return;
		}

		Entity selectedEntity = targetEntities.iterator().next();
		Properties.property(id, "targetEntity").setValue(entity,
				selectedEntity);
	}
}