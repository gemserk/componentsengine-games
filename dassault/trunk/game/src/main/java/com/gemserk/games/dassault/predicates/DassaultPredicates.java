package com.gemserk.games.dassault.predicates;

import org.newdawn.slick.geom.Shape;

import com.gemserk.commons.collisions.Collidable;
import com.gemserk.commons.collisions.EntityCollidableImpl;
import com.gemserk.commons.slick.geom.ShapeUtils;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class DassaultPredicates {

	@SuppressWarnings("unchecked")
	public static Predicate<EntityCollidableImpl> collidingWith(final Entity bullet) {

		return Predicates.and(new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity() != null;
			}

		}, new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity() != bullet;
			}

		}, new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity() != Properties.getValue(bullet, "owner");
			}

		}, new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				Collidable bulletCollidable = Properties.getValue(bullet, "collidable");
				return bulletCollidable.getAabb().collide(collidable.getAabb());
			}

		}, new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				Shape bulletBounds = Properties.getValue(bullet, "bounds");
				Shape bounds = Properties.getValue(collidable.getEntity(), "bounds");
				return new ShapeUtils(bounds).collides(bulletBounds);
			}

		}, new Predicate<EntityCollidableImpl>() {

			@Override
			public boolean apply(EntityCollidableImpl collidable) {
				return collidable.getEntity().getTags().contains("collidable");
			}

		});

	}

}
