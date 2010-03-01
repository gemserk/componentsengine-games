import towerofdefense.GroovyBootstrapper;

import com.gemserk.componentsengine.properties.SimpleProperty;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.ClosureProperty;


class EntityBuild {

	public Entity build(){
		new GroovyBootstrapper();
		Entity theEntity = new Entity("test");
		theEntity.addProperty("value",new SimpleProperty<Object>(new Integer(1)));
		theEntity.addProperty("incrementValue",new SimpleProperty<Object>(new Integer(1)));
		def closure = {entity.incrementValue}
		theEntity.addProperty("increment",new ClosureProperty(theEntity,closure))
		return theEntity
	}
}
