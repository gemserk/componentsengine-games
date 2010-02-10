package com.gemserk.games.towerofdefense.components;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.world.World;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class SelectTargetWithinRangeComponent extends TodComponent {

	@Inject
	World world;

	private PropertyLocator<String> targetTagProperty;

	private PropertyLocator<Vector2f> positionProperty;

	private PropertyLocator<Float> radiusProperty;

	private PropertyLocator<Entity> targetEntityProperty;

	public SelectTargetWithinRangeComponent(String name) {
		super(name);
		targetTagProperty = Properties.property(id, "targetTag");
		positionProperty = Properties.property(id, "position");
		radiusProperty = Properties.property(id, "radius");
		targetEntityProperty = Properties.property(id, "targetEntity");
	}

	@Override
	public void update(Entity entity, int delta) {

		String targetTag = targetTagProperty.getValue(entity);

		Vector2f position = positionProperty.getValue(entity);

		float radius = radiusProperty.getValue(entity);

		Collection<Entity> targetEntities = world.getEntities(Predicates.and(EntityPredicates.withAllTags(targetTag), EntityPredicates.isNear(position, radius)));

		if (targetEntities.size() == 0) {
			targetEntityProperty.setValue(entity, null);
			return;
		}

		Entity selectedEntity = targetEntities.iterator().next();
		targetEntityProperty.setValue(entity, selectedEntity);
	}
}