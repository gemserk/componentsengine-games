package com.gemserk.games.grapplinghookus;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Property;

public class InnerProperty implements Property<Object> {

	public static interface PropertyGetter {

		Object get(Entity entity);

	}

	public static interface PropertySetter {

		void set(Entity entity, Object value);

	}

	private final Entity entity;

	private final PropertyGetter propertyGetter;

	private final PropertySetter propertySetter;

	public InnerProperty(Entity entity, PropertySetter propertySetter) {
		this(entity, null, propertySetter);
	}
	
	public InnerProperty(Entity entity, PropertyGetter propertyGetter) {
		this(entity, propertyGetter, null);
	}
	
	public InnerProperty(Entity entity, PropertyGetter propertyGetter, PropertySetter propertySetter) {
		this.entity = entity;
		this.propertyGetter = propertyGetter;
		this.propertySetter = propertySetter;
	}

	@Override
	public Object get() {
		if (propertyGetter != null)
			return propertyGetter.get(entity);
		return null;
	}

	@Override
	public void set(Object value) {
		if (propertySetter != null)
			propertySetter.set(entity, value);
	}

}