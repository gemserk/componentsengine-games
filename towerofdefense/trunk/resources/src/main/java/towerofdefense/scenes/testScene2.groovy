package towerofdefense.scenes;

import com.gemserk.componentsengine.components.Component 
import com.gemserk.componentsengine.messages.Message 

import towerofdefense.TestPerformanceComponent.Button;

builder.entity("world") {
	
	//component(new TestPerformanceComponent("test"))
	
//	for (int i = 0; i < 100; i++) {
//		
//		child(template:"towerofdefense.entities.staticbutton", id:"button-${i}".toString())	{
////			position={utils.vector((float) Math.random() * 800, (float) Math.random() * 600)}
////			direction={utils.vector((float) Math.random(), (float) Math.random())}
////			rectangle={utils.rectangle(-30,-30, 60,60)}
//			position=utils.vector((float) Math.random() * 800, (float) Math.random() * 600)
//			direction=utils.vector((float) Math.random(), (float) Math.random())
//			rectangle=utils.rectangle(-30,-30, 60,60)
//			//label={entity.id}
//			label="hola"
//			fillColor=utils.color(1,0,0,1)
//			lineColor=utils.color(1,1,1,1)
//		}
//	}
	
	property("prueba","hola")
	
	component(new Component("hola"){
		void handleMessage(Message message){
			for(int i = 0; i<5000; i++){
				entity.prueba
			}
		}
	})
}