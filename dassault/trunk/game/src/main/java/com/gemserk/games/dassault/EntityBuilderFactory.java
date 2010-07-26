package com.gemserk.games.dassault;

import java.util.ArrayList;
import java.util.List;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.properties.SimpleProperty;
import com.google.inject.Injector;

public class EntityBuilderFactory {

	public static abstract class EntityBuilder {

		Entity entity;
		
		private final Injector injector;
		
		public EntityBuilder(Injector injector) {
			this.injector = injector;
		}

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
		
		public void property(String key, Property property) {
			entity.addProperty(key, property);
		}

		public ComponentPropertiesReceiver component(Component component) {
			injector.injectMembers(component);
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
		
		public void property(final String key, final Property<Object> property) {
			commands.add(new ExecutableWithEntityAndId() {

				@Override
				public void execute(Entity entity, String componentId) {
					entity.addProperty(componentId + "." + key, property);

				}
			});
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
