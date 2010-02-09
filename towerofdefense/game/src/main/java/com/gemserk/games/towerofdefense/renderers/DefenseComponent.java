package com.gemserk.games.towerofdefense.renderers;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Collection;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.componentsengine.world.World;
import com.gemserk.games.towerofdefense.components.Component;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class DefenseComponent extends Component {

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<Float> sizeProperty;

	PropertyLocator<Color> colorProperty;

	PropertyLocator<Boolean> enabledProperty;

	PropertyLocator<Float> energyRateProperty;

	PropertyLocator<Container> energyProperty;

	World world;

	@Inject
	public void setWorld(World world) {
		this.world = world;
	}

	public DefenseComponent(String name) {
		super(name);
		this.positionProperty = property(id + ".position");
		this.sizeProperty = property(id + ".size");
		this.colorProperty = property(id + ".color");
		this.enabledProperty = property(id + ".enabled");
		this.energyRateProperty = property(id + ".energyRate");
		this.energyProperty = property(id + ".energy");
	}

	public void init() {

	}

	@Override
	public void update(Entity entity, int delta) {

		boolean enabled = enabledProperty.getValue(entity);

		if (!enabled)
			return;

		float energyRate = energyRateProperty.getValue(entity);
		Container energy = energyProperty.getValue(entity);

		energy.remove(energyRate * delta);

		Vector2f position = positionProperty.getValue(entity);

		float range = sizeProperty.getValue(entity);

		final Color color = colorProperty.getValue(entity);

		Collection<Entity> nearBullets = world.getEntities(Predicates.and(
				EntityPredicates.withAllTags("bullet"), EntityPredicates
						.isNear(position, range), new Predicate<Entity>() {

					@Override
					public boolean apply(Entity target) {
						Property<Object> targetColorProperty = Properties
								.property("color").get(target);
						if (targetColorProperty == null)
							return false;
						return targetColorProperty.get().equals(color);
					}
				}));

		for (Entity bullet : nearBullets) {
			world.queueRemoveEntity(bullet);
		}

	}

	@Override
	public void render(Graphics g, Entity entity) {

		boolean enabled = enabledProperty.getValue(entity);

		if (!enabled)
			return;

		Vector2f position = positionProperty.getValue(entity);
		float radius = sizeProperty.getValue(entity);

		Color lineColor = this.colorProperty.getValue(entity);
		Color fillColor = new Color(lineColor.r, lineColor.g, lineColor.b,
				0.25f);

		g.pushTransform();
		{
			g.translate(position.getX(), position.getY());

			g.setColor(fillColor);
			g.fillOval(-radius, -radius, 2 * radius, 2 * radius);

			g.setColor(lineColor);
			float lineWidth = g.getLineWidth();
			g.setLineWidth(2.0f);
			g.drawOval(-radius, -radius, 2 * radius, 2 * radius);
			g.setLineWidth(lineWidth);

		}
		g.popTransform();
	}

}