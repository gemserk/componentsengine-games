package towerofdefense.entities;

builder.entity {
	
	parameters.each { String key, Object value -> 
		
		if (key.startsWith("property_")) {
			String newKey = key.replaceFirst("property_", "")
			property(newKey, value)
		}

		if (key.startsWith("component_")) {
			component(value);
		}

	}
	
}
