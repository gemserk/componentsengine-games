package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Collection;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.templates.EntityTemplate;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.componentsengine.world.World;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class FireComponent extends Component {

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Float> radiusProperty = property("radius");

	PropertyLocator<Float> damageProperty = property("damage");

	PropertyLocator<Color> colorProperty = property("color");

	PropertyLocator<Integer> reloadTimeProperty = property("reloadTime");

	PropertyLocator<Integer> currentReloadTimeProperty = property("currentReloadTime");

	PropertyLocator<String> templateProperty = property("template");

	PropertyLocator<Boolean> fireEnabledProperty = property("laser.enabled");

	World world;

	TemplateProvider templateProvider;

	@Inject
	public void setWorld(World world) {
		this.world = world;
	}

	@Inject
	public void setTemplateProvider(TemplateProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	public FireComponent(String name) {
		super(name);
	}

	@Override
	public void onAdd(Entity entity) {
		super.onAdd(entity);
		currentReloadTimeProperty.setValue(entity, 0);
	}

	@SuppressWarnings("serial")
	@Override
	public void update(final Entity entity, int delta) {

		Integer currentReloadTime = currentReloadTimeProperty.getValue(entity);

		fireEnabledProperty.setValue(entity, false);

		if (currentReloadTime > 0) {
			currentReloadTime -= delta;
			currentReloadTimeProperty.setValue(entity, currentReloadTime);
			return;
		}

		final Vector2f position = positionProperty.getValue(entity);
		Float radius = radiusProperty.getValue(entity);
		String templateName = templateProperty.getValue(entity);

		Collection<Entity> targets = world.getEntities(Predicates.and(
				EntityPredicates.withAllTags("hero"), EntityPredicates.isNear(
						position, radius)));

		if (targets.size() == 0)
			return;

		final float damage = damageProperty.getValue(entity);

		EntityTemplate template = templateProvider.getTemplate(templateName);

		for (final Entity target : targets) {
			Entity bullet = template.instantiate("bullet"
					+ System.currentTimeMillis(),
					new HashMap<String, Object>() {
						{
							put("position", position.copy());
							put("targetEntity", target);
							put("color", colorProperty.getValue(entity));
							put("damage", damage);
						}
					});

			world.queueAddEntity(bullet);

			currentReloadTime = reloadTimeProperty.getValue(entity);
			currentReloadTimeProperty.setValue(entity, currentReloadTime);

			fireEnabledProperty.setValue(entity, true);

			return;
		}

	}
}