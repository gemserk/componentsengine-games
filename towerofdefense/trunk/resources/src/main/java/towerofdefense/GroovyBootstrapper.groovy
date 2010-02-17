package towerofdefense

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesHolder;

class GroovyBootstrapper {
	public GroovyBootstrapper() {
		println "Bootstrapping groovy"
		
		PropertiesHolder.metaClass {
			propertyMissing << {String name ->
				delegate.properties[name].get()
			}
			propertyMissing << {String name, Object value ->
				Properties.property(name).setValue(delegate, value);
			}
		}
		
	}
}