import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.properties.SimpleProperty;

public class EntityTest {
	static int NUMBEROFCALLS = 10000000;

	public static void main(String[] args) {

		Entity theEntity = new EntityBuild().build();

		theEntity.addComponent(new Component("comp1") {
			@Override
			public void handleMessage(Message message) {
				Integer value = Properties.getValue(entity, "value");
				Integer increment = Properties.getValue(entity, "increment");
				Integer newValue = value + increment;
				Properties.setValue(entity, "value", newValue);
			}
		});

		Message message = new Message();

		long iniTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBEROFCALLS; i++) {
			theEntity.handleMessage(message);
		}
		long timelength = System.currentTimeMillis() - iniTime;
		System.out.println("TimeClosure :" + timelength);
		System.out.println(theEntity.getProperty("value").get());

		theEntity.addProperty("increment", new ReferenceProperty<Object>("incrementValue", theEntity));

		iniTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBEROFCALLS; i++) {
			theEntity.handleMessage(message);
		}
		timelength = System.currentTimeMillis() - iniTime;
		System.out.println("TimeRef :" + timelength);
		System.out.println(theEntity.getProperty("value").get());

		
		theEntity.addProperty("increment", new SimpleProperty(new Integer(1)));

		iniTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBEROFCALLS; i++) {
			theEntity.handleMessage(message);
		}
		timelength = System.currentTimeMillis() - iniTime;
		System.out.println("TimeSimple :" + timelength);
		System.out.println(theEntity.getProperty("value").get());
	}

}
