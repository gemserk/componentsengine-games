import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.messages.Message;

public class MessageHandlerInArrayTest {
	public static void main(String[] args) {

		Component[] components = new Component[1000];
		
		for (int i = 0; i < 1000; i++) {
			components[i] = new Component("test") {

			};
			
		}

		double time = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			for (Component component : components) {
				component.handleMessage(new Message());
			}
			
		}
		System.out.println("TIME: " + (System.currentTimeMillis() - time));

	}
}
