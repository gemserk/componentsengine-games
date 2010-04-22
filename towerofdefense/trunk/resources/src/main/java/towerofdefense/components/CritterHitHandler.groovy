package towerofdefense.components

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.effects.EffectFactory 
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.google.inject.Inject;


class CritterHitHandler extends ReflectionComponent{
	
	@Inject MessageQueue messageQueue;
	
	public CritterHitHandler(String id) {
		super(id)
	}
	
	public void handleMessage(GenericMessage message) {
		if(message.id != "hit")
			return
		
		def sourceEntity = message.source
		
		if (!sourceEntity.tags.contains("bullet"))
			return;
		
		if (entity.health.isEmpty())
			return;
		
		if (message.targets.contains(entity)) {
			entity.health.remove(message.damage)
			
			def explosionMessage = new GenericMessage("explosion")
			int dcount = (int) Math.ceil(message.damage);
			explosionMessage.explosion = EffectFactory.explosionEffect(dcount, (int) entity.position.x, (int) entity.position.y, 0f, 360f, 300, 5.0f, 20f, 30f, 1f)
			messageQueue.enqueue(explosionMessage)
			
			if (entity.health.isEmpty()){
				def deadMessage = new GenericMessage("critterdead")
				deadMessage.critter = entity
				
				messageQueue.enqueue(deadMessage)
			}
		}
	}
}
