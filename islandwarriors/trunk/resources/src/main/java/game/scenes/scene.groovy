package game.scenes
import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicates 
import game.GroovyBootstrapper

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	
	child(template:"game.entities.island", id:"island1")	{
		position = utils.vector(100,100)
	}
	
	child(template:"game.entities.island", id:"island2")	{
		position = utils.vector(100,300)
	}
	
	child(template:"game.entities.island", id:"island3")	{
		position = utils.vector(100,500)
	}
	
	
	child(template:"game.entities.island", id:"island4")	{
		position = utils.vector(700,100)
	}
	
	child(template:"game.entities.island", id:"island5")	{
		position = utils.vector(700,300)
	}
	
	child(template:"game.entities.island", id:"island6")	{
		position = utils.vector(700,500)
	}
	
	
	component(utils.components.genericComponent(id:"moveHandler", messageId:"move"){ message ->
		def targets = entity.parent.getEntities(Predicates.and(EntityPredicates.withAllTags("island"),EntityPredicates.isNear(utils.vector(message.x, message.y),50)))
		
		if(targets.isEmpty())
			return;
		
		entity.overIsland = targets.first()
	})
	
	child(entity("islandHighlighter"){
		
		property("island",{entity.parent.overIsland})
		property("enabled",{entity.island != null})
		
		component(new ProcessingDisablerComponent("disableStateComponent")){
			propertyRef("enabled","enabled")
		}
		
		component(new CircleRenderableComponent("image")){
			property("position",{entity.island.position})
			property("radius",{(float)(entity.island.radius + 10)})
			property("lineColor",utils.color(1,0,0,1))
		}
	})
	
	
	
	input("inputmapping"){
		keyboard {
			//press(button:"w", eventId:"nextWave")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			press(button:"right", eventId:"rightClick")
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}
	
}
