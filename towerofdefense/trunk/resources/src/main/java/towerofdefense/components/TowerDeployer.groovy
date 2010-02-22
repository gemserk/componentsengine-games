package towerofdefense.components

import com.gemserk.componentsengine.components.ReflectionComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.componentsengine.messages.ChildMessage;
import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.MessageQueue 
import com.google.inject.Inject 
import org.newdawn.slick.Input 
import org.newdawn.slick.geom.Vector2f 

class TowerDeployer extends ReflectionComponent{
	
	@Inject Input input;
	@Inject MessageQueue messageQueue;
	
	
	public TowerDeployer(String id) {
		super(id);
	}
	
	
	public void handleMessage(GenericMessage message){
		
		if(message.id != "deployturret")
			return;
		

		
		
		def instantiationTemplate = entity."${id}.instantiationTemplate"
		
		def position = new Vector2f(input.getMouseX(), input.getMouseY())
		
	
		
		Entity tower = instantiationTemplate.get(position)
		
		
		def cost = tower.cost
		if(entity.money < cost){
			println "Not enough money"
			return
		}
			
		entity.money -= cost
		
		entity."${id}.towerCount" = entity."${id}.towerCount" + 1
		messageQueue.enqueue(ChildMessage.addEntity(tower, "world"));
	}

}
