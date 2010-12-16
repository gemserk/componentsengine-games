package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;

import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class PathEntityBuilder extends EntityBuilder {

	@Override
	public void build() {
		
		tags("path");
		
		property("path", parameters.get("path"));
		property("ballsQuantity", parameters.get("ballsQuantity"));
		property("pathProperties", parameters.get("pathProperties"));
		property("ballDefinitions", parameters.get("ballDefinitions"));
		property("subPathDefinitions", parameters.get("subPathDefinitions"));
		
		final Path path = Properties.getValue(entity, "path");

		child(templateProvider.getTemplate("zombierockers.entities.base").instantiate(entity.getId() + "_base", new HashMap<String, Object>() {
			{
				put("position", path.getPoint(path.getPoints().size() - 1));
				put("radius", 15f);
			}
		}));

		child(templateProvider.getTemplate("zombierockers.entities.spawner").instantiate(entity.getId() + "_spawner", new HashMap<String, Object>() {
			{
				put("path", new ReferenceProperty<Object>("path", entity));
				put("pathEntity", entity);
				put("ballsQuantity", new ReferenceProperty<Object>("ballsQuantity", entity));
				put("pathProperties", new ReferenceProperty<Object>("pathProperties", entity));
				put("ballDefinitions", new ReferenceProperty<Object>("ballDefinitions", entity));
				put("subPathDefinitions", new ReferenceProperty<Object>("subPathDefinitions", entity));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.entities.limbo").instantiate(entity.getId() + "_limbo", new HashMap<String, Object>() {
			{
				put("path", new ReferenceProperty<Object>("path", entity));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.entities.segmentsmanager").instantiate(entity.getId() + "_segmentsManager", new HashMap<String, Object>() {
			{
				put("path", new ReferenceProperty<Object>("path", entity));
			}
		}));
		
	}
}
