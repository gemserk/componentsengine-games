package game.scenes
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage 

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.componentsengine.timers.PeriodicTimer 
import com.gemserk.componentsengine.utils.OpenGlUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates 
import game.GroovyBootstrapper

builder.entity("world") {
	
	new GroovyBootstrapper();
	
	property("enabled", {entity.parent.gameState == "playing" })
	
	component(new ProcessingDisablerComponent("disableStateComponent")){
		propertyRef("enabled","enabled")
		property("exclusions",[SlickRenderMessage.class])
	}
	
	property("cursor",utils.vector(1,1))
	
	
	component(new RectangleRendererComponent("background")) {
		property("position", utils.vector(0, 0))
		property("rectangle", utils.rectangle(0,0,800,600))
		property("fillColor", utils.color((float)60/255, (float)169/255,(float)178/255, 1.0f))
		property("lineColor", utils.color(0f, 0f, 0f, 0f))
	}
	
	
	
	child(template:"game.entities.island", id:"island1")	{
		position = utils.vector(60,100)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island2")	{
		position = utils.vector(60,300)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island3")	{
		position = utils.vector(60,500)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island4")	{
		position = utils.vector(200,200)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island5")	{
		position = utils.vector(200,400)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island5")	{
		position = utils.vector(400,100)
		team = "team1"
	}
	
	child(template:"game.entities.island", id:"island6")	{
		position = utils.vector(740,100)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island7")	{
		position = utils.vector(740,300)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island8")	{
		position = utils.vector(740,500)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island9")	{
		position = utils.vector(600,200)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island10")	{
		position = utils.vector(600,400)
		team = "team2"
	}
	
	child(template:"game.entities.island", id:"island11")	{
		position = utils.vector(400,500)
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
		
		if(entity.overIsland.team != "team1")
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
				(float)(entity.island.radius)
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
			//graphics.drawLine(island.position.x, island.position.y,cursor.x,cursor.y)
			SlickCallable.enterSafeBlock()
			OpenGlUtils.renderLine(island.position, cursor, 1f, utils.color(0,1,0,1))
			SlickCallable.leaveSafeBlock()
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
	
	component(utils.components.genericComponent(id:"deselectIsland", messageId:"rightClick"){ message ->
		entity.selectedIsland = null
	})
	
	
	component(new TimerComponent("evaluateIATimer")){
		property("trigger",utils.custom.triggers.genericMessage("evaluateIA") {})
		property("timer",new PeriodicTimer(500))
	}
	
	Random random = new Random();
	
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
	def getIslandByTeam= {def container,def team ->
		container.getEntities({island ->
			island.tags.contains("island") && island.team == team
		} as Predicate);
	}
	
	def getIslandMin= {def islands ->
		return islands.min{it.units }
	}
	
	def getIslandMax= {def islands ->
		return islands.max{it.units }
	}
	
	def getMinMaxRatioIslands= {def islands ->
		def unitsMin = getIslandMin(islands).units
		def unitsMax = getIslandMax(islands).units
		
		return unitsMin/unitsMax
	}
	
	
	
	component(utils.components.genericComponent(id:"evaluateIAHandler", messageId:"evaluateIA"){ message ->
		
		if(random.nextFloat() > 0.5f)
			return
		
		println getRandomItem([0,1,2,3])
		
		
		def myIslands = getIslandByTeam(entity,"team2")
		def hisIslands = getIslandByTeam(entity,"team1")
		
		if(myIslands.isEmpty() || hisIslands.isEmpty())
			return
		
		def aiRatio = getMinMaxRatioIslands(myIslands)
		def enemyRatio = getMinMaxRatioIslands(hisIslands)
		
		println "AI: $aiRatio"
		println "ME: $enemyRatio"
		
		if(random.nextFloat()>enemyRatio){
			messageQueue.enqueue(utils.genericMessage("sendShips"){ sendShipMessage ->
				sendShipMessage.origin = getIslandMax(myIslands)
				sendShipMessage.destination = getIslandMin(hisIslands)
			})	
			println "Attack"
			return 
		}
		
		if(random.nextFloat()> aiRatio + 0.5f){
			messageQueue.enqueue(utils.genericMessage("sendShips"){ sendShipMessage ->
				sendShipMessage.origin = getIslandMax(myIslands)
				sendShipMessage.destination = getIslandMin(myIslands)
			})	
			println "Send AID"
			return
		}
		
		
		
		
		
		def origin = getRandomItem(myIslands)
		
		def destinations
		if(random.nextFloat() > 0.7f){
			destinations = myIslands.findAll{ it != origin}
			println "Random AID"
		}else{
			destinations = getIslandByTeam(entity,"team1")
			println "Random Attack"
		}
		
		if(destinations.isEmpty())
			return
			
		def destination = getRandomItem(destinations)
		
		messageQueue.enqueue(utils.genericMessage("sendShips"){ sendShipMessage ->
			sendShipMessage.origin = origin
			sendShipMessage.destination = destination
		})	                              
		
		
		
	})
	
	
	component(new ComponentFromListOfClosures("checkEnding",[ {UpdateMessage message ->
		def playerIslands = getIslandByTeam(entity,"team1")
		def aiIslands = getIslandByTeam(entity,"team2")
		
		if(aiIslands.isEmpty()){
			entity.parent.gameState = "gameover"
			entity.parent.result = "You Win"
			return
		}
		
		if(playerIslands.isEmpty()){
			entity.parent.gameState = "gameover"
			entity.parent.result = "You Loose"
			return
		}
	}
	]))
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		entity.parent.gameState = "paused"
	})
	
	input("inputmapping"){
		keyboard {
			//press(button:"w", eventId:"nextWave")
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			
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
