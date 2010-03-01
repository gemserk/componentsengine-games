package towerofdefense.scenes;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.games.towerofdefense.LabelComponent;

builder.entity("menu") {
	
	property("value",new Integer(1));
	property("incrementValue",new Integer(1))
	
	def quantity = 10000;
	
	quantity.times {
		println "$it"
		component(new Component("id+${it}".toString()){
			void handleMessage(Message message){
				entity.value = entity.value + entity.incrementValue
			}
		})
	}
	
//	quantity.times {
//		println "$it"
//		component(new TestSceneComponent("id+${it}".toString()))
//	}	
	
	component(new LabelComponent("label".toString())){
		property("position",utils.vector(100,100))
		property("message", "{0}")
		//propertyRef("value","value")
		property("value",{entity.value})
	}	
	
	println entity
}