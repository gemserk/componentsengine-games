package com.gemserk.games.towerofdefense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.resources.ResourceLoader;
import com.gemserk.componentsengine.world.World;
import com.gemserk.games.towerofdefense.components.FaceTargetComponent;
import com.gemserk.games.towerofdefense.components.RemoveWhenNearComponent;
import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;
import com.gemserk.games.towerofdefense.components.SpawnerComponent;
import com.gemserk.games.towerofdefense.components.WeaponComponent;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TowerOfDefenseComponentLoader implements ResourceLoader {

	@Inject
	ComponentManager componentManager;

	@Inject
	Injector injector;

	@Inject
	Input input;

	public static class MessageBuilder {

		// TODO: message builder from closure

		public Message build(Map<String, Object> map) {
			return new GenericMessage("hit", new PropertiesMapBuilder().addProperties(map).build());
		}

	}

	public static class HitComponent extends ReflectionComponent {

		private PropertyLocator<Vector2f> positionProperty;

		private PropertyLocator<Float> radiusProperty;

		// private PropertyLocator<MessageBuilder> messageBuilderProperty;

		private PropertyLocator<String> targetTagProperty;

		@Inject
		World world;

		public HitComponent(String id) {
			super(id);

			positionProperty = Properties.property(id, "position");
			radiusProperty = Properties.property(id, "radius");
			// messageBuilderProperty = Properties.property(id, "messageBuilder");
			targetTagProperty = Properties.property(id, "targetTag");

		}

		public void handleMessage(UpdateMessage message) {

			final Entity entity = message.getEntity();

			Vector2f position = positionProperty.getValue(entity);
			Float radius = radiusProperty.getValue(entity);
			String targetTags = targetTagProperty.getValue(entity);

			final Collection<Entity> candidates = world.getEntities(Predicates.and(EntityPredicates.withAllTags(targetTags), EntityPredicates.isNear(position, radius)));

			if (candidates.size() == 0)
				return;

			// MessageBuilder messageBuilder = messageBuilderProperty.getValue(entity);
			MessageBuilder messageBuilder = new MessageBuilder();

			Message hitMessage = messageBuilder.build(new HashMap<String, Object>() {
				{
					put("source", entity);
					put("targets", new ArrayList<Entity>(candidates));
				}
			});

			world.handleMessage(hitMessage);
		}

	}

	@Override
	public void load() {

		Component[] components = { new PathRendererComponent("pathrenderer"), // 
				new CircleRenderableComponent("circlerenderer"),// 
				new SuperMovementComponent("movement"),//
				new FollowPathComponent("followpath"),//
				new ImageRenderableComponent("imagerenderer"),//
				new RemoveWhenNearComponent("remover"),//
				new SpawnerComponent("creator"),//
				new FaceTargetComponent("faceTarget"),//
				new SelectTargetWithinRangeComponent("selectTarget"),//
				new WeaponComponent("shooter"),//
				new HitComponent("bullethit") };

		for (com.gemserk.componentsengine.components.Component component : components) {
			injector.injectMembers(component);
		}

		componentManager.addComponents(components);

	}
}