package towerofdefense

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.properties.Properties;

class GroovyBootstrapper {
	public GroovyBootstrapper() {
		println "Bootstrapping groovy"
		Entity.metaClass {
			propertyMissing << {String name -> delegate.properties[name].get()}
			propertyMissing << {String name, Object value -> delegate.properties[name].set(value)}
		}
		
		GenericMessage.metaClass {
			propertyMissing << {String name ->
				delegate.properties[name].get()
			}
			propertyMissing << {String name, Object value ->
				Properties.property(name).setValue(delegate, value);
			}
		}
		
	}
}