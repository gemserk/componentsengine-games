package game.scenes
import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.messages.SlickRenderMessage;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicates 
import game.GroovyBootstrapper

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	property("cursor",utils.vector(1,1))
	
	
	component(new RectangleRendererComponent("background")) {
		property("position", utils.vector(0, 0))
		property("rectangle", utils.rectangle(0,0,800,600))
		property("fillColor", utils.color((float)60/256, (float)169/256,(float)178/256, 1.0f))
		property("lineColor", utils.color(0f, 0f, 0f, 0f))
	}
	
	
	
	child(template:"game.entities.island", id:"island1")	{
		position = utils.vector(100,100)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island2")	{
		position = utils.vector(100,300)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island3")	{
		position = utils.vector(100,500)
		team = "team1"
	}
	
	
	child(template:"game.entities.island", id:"island4")	{
		position = utils.vector(700,100)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island5")	{
		position = utils.vector(700,300)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island6")	{
		position = utils.vector(700,500)
		team = "team2"
	}
	
	child(template:"game.entities.whirpool", id:"whirpool1")	{
		position = utils.vector(400,300)
	}
	
	
	component(utils.components.genericComponent(id:"moveHandler", messageId:"move"){ message ->
		def targets = entity.parent.getEntities(Predicates.and(EntityPredicates.withAllTags("island"),EntityPredicates.isNear(utils.vector(message.x, message.y),50)))
		
		if(targets.isEmpty()){
			entity.overIsland = null
			return;
		}
		
		entity.overIsland = targets.first()
	})
	
	
	
	component(utils.components.genericComponent(id:"selectIslandHandler", messageId:"click"){ message ->
		if(entity.selectedIsland != null || entity.overIsland==null)
			return
		
		entity.selectedIsland = entity.overIsland
	})
	
	child(entity("islandHighlighter"){
		
		property("island",{entity.parent.overIsland })
		property("enabled",{entity.island != null })
		
		component(new ProcessingDisablerComponent("disableStateComponent")){ propertyRef("enabled","enabled") }
		
		component(new CircleRenderableComponent("image")){
			property("position",{entity.island.position })
			property("radius",{
				(float)(entity.island.radius + 10)
			})
			property("lineColor",utils.color(1,0,0,1))
		}
	})
	
	
	
	child(entity("selectedIslandHighlighter"){
		
		property("island",{entity.parent.selectedIsland })
		property("enabled",{entity.island != null })
		
		component(new ProcessingDisablerComponent("disableStateComponent")){ propertyRef("enabled","enabled") }
		
		component(new CircleRenderableComponent("image")){
			property("position",{entity.island.position })
			property("radius",{
				(float)(entity.island.radius - 10)
			})
			property("lineColor",utils.color(0,1,0,1))
		}
	})
	
	
	component(utils.components.genericComponent(id:"cursorSetter", messageId:"move"){ message ->
		entity.cursor = utils.vector(message.x, message.y)
	})
	
	child(entity("choosingDestinationRender"){
		
		property("island",{entity.parent.selectedIsland })
		property("cursor",{entity.parent.cursor})
		property("enabled",{entity.island != null })
		
		component(new ProcessingDisablerComponent("disableStateComponent")){ propertyRef("enabled","enabled") }
		
		component(new ComponentFromListOfClosures("render",[{SlickRenderMessage message ->
			Graphics graphics = message.graphics
			def cursor = entity.cursor
			def island = entity.island
			graphics.drawLine(island.position.x, island.position.y,cursor.x,cursor.y)
			
		}]))
	})
	
	
	
	component(utils.components.genericComponent(id:"selectDestinationHandler", messageId:"click"){ message ->
		if(entity.selectedIsland == null)
			return
		
		if(entity.overIsland==null)
			return
		
		if(entity.selectedIsland == entity.overIsland)
			return
		
		
		
		messageQueue.enqueue(utils.genericMessage("sendShips"){ sendShipMessage ->
			sendShipMessage.origin = entity.selectedIsland
			sendShipMessage.destination = entity.overIsland
		})	
		
		entity.selectedIsland = null
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
