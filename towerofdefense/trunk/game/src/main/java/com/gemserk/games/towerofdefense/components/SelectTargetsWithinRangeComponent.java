package com.gemserk.games.towerofdefense.components;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class SelectTargetsWithinRangeComponent extends FieldsReflectionComponent {

	@Inject	@Root 
	Entity  rootEntity;

	@EntityProperty(readOnly=true)
	String targetTag;

	@EntityProperty(readOnly=true)
	Vector2f position;

	@EntityProperty(readOnly=true)
	Float radius;

	@EntityProperty(readOnly=true)
	Integer max;

	@EntityProperty
	Collection<Entity> targets;

	public SelectTargetsWithinRangeComponent(String id) {
		super(id);
	}

	public void handleMessage(UpdateMessage message) {

		targets.clear();

		Collection<Entity> targetEntities = rootEntity.getEntities(Predicates.and(EntityPredicates.withAllTags(targetTag), EntityPredicates.isNear(position, radius)));
		
		if (targetEntities.isEmpty())
			return;

		int m = max;
		for (Entity entity : targetEntities) {
			targets.add(entity);
			m--;
			if (m==0)
				return;
		}
		
		
	}
}