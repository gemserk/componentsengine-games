import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;


public class MessagePerformanceTest {

	public static void main(String[] args) {
		
		Entity root = new Entity("root");
		
		for (int i = 0; i < 1000; i++) {
			Entity entity = new Entity("child-" + i);
			entity.addComponent(new Component("test"){
				
			});
			root.addEntity(entity);
		}
		
		
		double time = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			root.handleMessage(new Message());
		}
		System.out.println("TIME: " + (System.currentTimeMillis() - time));
		
	}
}
