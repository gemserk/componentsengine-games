package com.gemserk.games.dosdewinia;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.properties.SimpleProperty;
import com.gemserk.componentsengine.utils.EntityDumper;

public class EntityBuilderFactory {

	public static abstract class EntityBuilder {

		Entity entity;

		public void tags(String... tags) {
			for (String tag : tags) {
				entity.getTags().add(tag);
			}
		}

		public void property(String key, Object value) {
			entity.addProperty(key, new SimpleProperty<Object>(value));
		}

		public void propertyRef(String key, String ref) {
			entity.addProperty(key, new ReferenceProperty<Object>(ref, entity));
		}

		public ComponentPropertiesReceiver component(Component component) {
			entity.addComponent(component);
			return new ComponentPropertiesReceiver(component.getId(), entity);
		}

		public abstract void build();

		private void setEntity(Entity currentEntity) {
			entity = currentEntity;
		}
	}

	public static class ComponentPropertiesReceiver {
		private final String componentId;
		private final Entity entity;

		public ComponentPropertiesReceiver(String id, Entity entity) {
			this.componentId = id;
			this.entity = entity;
		}

		public void withProperties(ComponentProperties componentProperties) {
			componentProperties.execute(entity, componentId);
		}
	}

	public static abstract class ComponentProperties {

		List<ExecutableWithEntityAndId> commands = new ArrayList();

		public abstract class ExecutableWithEntityAndId {
			public abstract void execute(Entity entity, String componentId);
		}

		public void property(final String key, final Object value) {
			commands.add(new ExecutableWithEntityAndId() {

				@Override
				public void execute(Entity entity, String componentId) {
					entity.addProperty(componentId + "." + key, new SimpleProperty<Object>(value));

				}
			});
		}

		public void propertyRef(final String key, final String ref) {
			commands.add(new ExecutableWithEntityAndId() {

				@Override
				public void execute(Entity entity, String componentId) {
					entity.addProperty(componentId + "." + key, new ReferenceProperty<Object>(ref, entity));

				}
			});

		}

		private void execute(Entity entity, String componentId) {
			for (ExecutableWithEntityAndId command : commands) {
				command.execute(entity, componentId);
			}
		}

	}

	public static void main(String[] args) {
		EntityBuilderFactory builderFactory = new EntityBuilderFactory();

		for (int j = 0; j < 1000; j++) {

			long iniTime = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				buildEntity(builderFactory);
			}
			System.out.println("Time: " + (System.currentTimeMillis() - iniTime));
		}

		// JSONArray jobject = JSONArray.fromObject(new EntityDumper().dumpEntity(entity));
		// System.out.println(jobject.toString(4));

	}

	private static void buildEntity(EntityBuilderFactory builderFactory) {
		Entity entity = builderFactory.entity("theentity").with(new EntityBuilder() {

			public void build() {

				tags("player1", "bullet");
				property("position", new Vector2f(10, 20));
				propertyRef("refPosition", "position");

				component(new OutOfBoundsRemover("outofbounds"));

				component(new OutOfBoundsRemover("outofboundsremoverwithprops")).withProperties(new ComponentProperties() {
					{
						property("bounds", new Rectangle(0, 0, 800, 600));
						propertyRef("position", "position");
					}
				});

			}
		});
	}

	Entity currentEntity;

	public EntityBuilderFactory entity(String id) {
		currentEntity = new Entity(id);
		return this;
	}

	public Entity with(EntityBuilder builder) {
		builder.setEntity(currentEntity);
		builder.build();

		return currentEntity;
	}

}
