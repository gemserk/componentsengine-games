package com.gemserk.games.dassault.predicates;

import org.newdawn.slick.geom.Shape;

import com.gemserk.commons.collisions.Collidable;
import com.gemserk.commons.collisions.EntityCollidableImpl;
import com.gemserk.commons.slick.geom.ShapeUtils;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Custom predicates to help collision testing.
 * @author acoppes
 *
 */
public class CollidablesPredicates {

	public static Predicate<EntityCollidableImpl> isNotEntityCollidable(final Entity entity) {
		return new Predicate<EntityCollidableImpl>() {
			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity() != entity;
			}
		};
	}

	public static Predicate<EntityCollidableImpl> collidesWithAabb(final Entity entity) {
		return new Predicate<EntityCollidableImpl>() {
			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				Collidable entityCollidable = Properties.getValue(entity, "collidable");
				return entityCollidable.getAabb().collide(collidable.getAabb());
			}
		};
	}

	private static Predicate<EntityCollidableImpl> collidesWithBounds(final Entity entity) {
		return new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				Shape entityBounds = Properties.getValue(entity, "bounds");
				Shape bounds = Properties.getValue(collidable.getEntity(), "bounds");
				return new ShapeUtils(bounds).collides(entityBounds);
			}

		};
	}

	private static Predicate<EntityCollidableImpl> collidableEntityNotNull() {
		return new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity() != null;
			}

		};
	}

	private static Predicate<EntityCollidableImpl> withCollidableTag() {
		return new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity().getTags().contains("collidable");
			}

		};
	}

	@SuppressWarnings("unchecked")
	public static Predicate<EntityCollidableImpl> collidingWith(final Entity entity) {

		return Predicates.and(collidableEntityNotNull(), isNotEntityCollidable(entity), // 
				new Predicate<EntityCollidableImpl>() {

					@Override
					public boolean apply(EntityCollidableImpl collidable) {
						return collidable.getEntity() != Properties.getValue(entity, "owner");
					}

				}, collidesWithAabb(entity), collidesWithBounds(entity), withCollidableTag());

	}

	@SuppressWarnings("unchecked")
	public static Predicate<EntityCollidableImpl> collidingWith2(final Entity entity) {
		return Predicates.and(collidableEntityNotNull(), isNotEntityCollidable(entity), // 
				collidesWithAabb(entity), collidesWithBounds(entity), withCollidableTag());
	}

}
