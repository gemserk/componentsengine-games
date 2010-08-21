package com.gemserk.games.dudethatsmybullet;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.google.common.base.Predicate;

public class GamePredicates {
	public static Predicate<Entity> isNearWithRadius(final Vector2f position, final float entityRadius) {
		final PropertyLocator<Vector2f> positionProperty = Properties.property("position");
		final PropertyLocator<Float> radiusProperty = Properties.property("radius");
		return new Predicate<Entity>() {

			@Override
			public boolean apply(Entity target) {

				Vector2f targetPos = positionProperty.getValue(target);
				float targetRadius = radiusProperty.getValue(target);
				return position.distance(targetPos) < entityRadius + targetRadius;

			}
		};
	}
}
