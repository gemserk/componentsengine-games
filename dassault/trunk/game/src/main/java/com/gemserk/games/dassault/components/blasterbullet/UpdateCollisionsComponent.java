package com.gemserk.games.dassault.components.blasterbullet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.commons.collisions.EntityCollidableImpl;
import com.gemserk.commons.collisions.QuadTree;
import com.gemserk.commons.slick.geom.ShapeUtils;
import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.games.dassault.predicates.CollidablesPredicates;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class UpdateCollisionsComponent extends FieldsReflectionComponent {

	@EntityProperty
	EntityCollidableImpl collidable;

	@EntityProperty
	Shape bounds;

	@EntityProperty
	Vector2f position;

	@Inject
	MessageQueue messageQueue;

	public UpdateCollisionsComponent(String id) {
		super(id);
	}

	@Override
	public void onAdd(Entity entity) {
		super.onAdd(entity);

		// I have no fields reflection on this method....
		Shape bounds = Properties.getValue(entity, "bounds");

		Properties.setValue(entity, "collidable", // 
				new EntityCollidableImpl(entity, new ShapeUtils(bounds).getAABB()));
	}

	@SuppressWarnings("unchecked")
	@Handles
	public void update(Message message) {

		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);

		collidable.setCenter(position.x, position.y);
		collidable.update();

		if (collidable.isOutside()) {
			messageQueue.enqueue(new Message("bulletDead", new PropertiesMapBuilder() {
				{
					property("bullet", entity);
				}
			}.build()));
			return;
		}

		QuadTree quadTree = collidable.getQuadTree();

		if (quadTree == null)
			return;

		List collidables = quadTree.getCollidables(collidable);
		Collection filteredCollidables = Collections2.filter(collidables, CollidablesPredicates.collidingWith(entity));

		Properties.setValue(entity, "collisions", new ArrayList(filteredCollidables));

	}
	
	@Handles
	public void collisionDetected(Message message) {
		if (entity != Properties.getValue(message, "target"))
			return;
		messageQueue.enqueue(new Message("bulletDead", new PropertiesMapBuilder() {
			{
				property("bullet", entity);
			}
		}.build()));
	}

}
