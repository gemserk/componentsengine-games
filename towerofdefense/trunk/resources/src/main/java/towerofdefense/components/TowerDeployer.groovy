package towerofdefense.components

import com.gemserk.componentsengine.components.ReflectionComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.componentsengine.messages.AddEntityMessage 
import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.MessageQueue 
import com.gemserk.componentsengine.templates.EntityTemplate 
import com.gemserk.componentsengine.templates.TemplateProvider 
import com.google.inject.Inject 
import org.newdawn.slick.Input 
import org.newdawn.slick.geom.Vector2f 

class TowerDeployer extends ReflectionComponent{
	
	@Inject TemplateProvider templateProvider
	@Inject Input input;
	@Inject MessageQueue messageQueue;
	
	public TowerDeployer(String id) {
		super(id);
	}
	
	
	public void handleMessage(GenericMessage message){
		
		if(message.id != "deployturret")
			return;
		
		def instanceParameters = message.entity.instanceParameters.get()
		def template = message.entity.template
		
		instanceParameters.put("position", new Vector2f(input.getMouseX(), input.getMouseY()))
		
		EntityTemplate bulletTemplate = templateProvider.getTemplate(template);
		
		Entity tower = bulletTemplate.instantiate("tower-${Math.random()}", instanceParameters);
		
		messageQueue.enqueue(new AddEntityMessage(tower));
	}

}
