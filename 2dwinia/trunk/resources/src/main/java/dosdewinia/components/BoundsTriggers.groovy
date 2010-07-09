package dosdewinia.components

import groovy.lang.Closure;
import com.gemserk.componentsengine.annotations.EntityProperty 
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles 
import com.gemserk.componentsengine.messages.Message 
import org.newdawn.slick.geom.Vector2f 

public class BoundsTriggers extends FieldsReflectionComponent{
	
	@EntityProperty(readOnly = true)
	Vector2f position;

	@EntityProperty()
	Boolean outsideOfBounds;
	
	@EntityProperty(readOnly=true)
	Closure traversableEvaluator;
	
	@EntityProperty(required=false, readOnly=true)
	Closure wentOutsideTrigger;
	
	@EntityProperty(required=false, readOnly=true)
	Closure wentInsideTrigger;
	
	
	public BoundsTriggers(String id) {
		super(id)
	}
	
	@Handles
	public void update(Message message){
		if(!traversableEvaluator(position)){
			if(!outsideOfBounds){
				outsideOfBounds = true
				if(wentOutsideTrigger)
					wentOutsideTrigger.call()
			}
		}else {
			if(outsideOfBounds){
				outsideOfBounds = false
				if(wentInsideTrigger)
					wentInsideTrigger.call()
			}
		}
	}
}
