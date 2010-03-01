package towerofdefense.scenes;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;

public class TestSceneComponent extends Component {

	public TestSceneComponent(String id) {
		super(id);
	}

	@Override
	public void handleMessage(Message message) {
		Integer value = Properties.getValue(entity, "value");
		Integer increment = Properties.getValue(entity, "incrementValue");
		Integer newValue = value + increment;
		Properties.setValue(entity, "value", newValue);
	}

}
