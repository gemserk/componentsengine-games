package towerofdefense.scenes;
import com.gemserk.componentsengine.messages.GenericMessage;


import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import towerofdefense.GroovyBootstrapper;
import towerofdefense.components.TowerDeployer;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.LabelComponent;
import com.gemserk.games.towerofdefense.Path;
import com.gemserk.games.towerofdefense.components.GuiLogicComponent;
import com.gemserk.games.towerofdefense.components.OutOfBoundsRemover;
import com.gemserk.games.towerofdefense.components.RectangleRendererComponent;
import com.gemserk.games.towerofdefense.components.TimerComponent;
import com.gemserk.games.towerofdefense.timers.PeriodicTimer;
import com.gemserk.games.towerofdefense.waves.Wave;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity("world") {
	
	new GroovyBootstrapper();
	
	property("money", 15)
	property("points",0)
	property("lives",15)
	property("wavesTimer", new PeriodicTimer(15000))
	property("towerCount",0)
	
	def defx = 0
	def defy = 30
	
	def gameBoundsToRender = utils.rectangle(0, 120, 800, 480)
	def borderSize = 20
	def gameBounds = utils.rectangle((float)gameBoundsToRender.x+borderSize,(float)gameBoundsToRender.y+borderSize,(float)gameBoundsToRender.width-2*borderSize,(float)gameBoundsToRender.height-2*borderSize)

	property("gameBoundsToRender", gameBoundsToRender)
	property("gameBounds",gameBounds)
	
	component(new RectangleRendererComponent("gameBoundsToRenderRenderer")) {
		property("position", utils.vector(0, 0))
		propertyRef("rectangle", "gameBoundsToRender")
		property("fillColor", utils.color(0.0f, 0.0f, 0.0f, 1.0f))
		property("lineColor", utils.color(0f, 0f, 0f, 0f))
	}
	
	
	child(template:"towerofdefense.entities.path", id:"path")	{
		path=new Path([
    	utils.vector(0, 450 + defy), 		      
		utils.vector(100, 450 + defy), 		      
		utils.vector(100, 300 + defy), 
		utils.vector(300, 150 + defy), 		      
		utils.vector(500, 150 + defy), 		      
		utils.vector(620, 300 + defy), 		      
		utils.vector(500, 500 + defy), 		      
		utils.vector(500, 500 + defy), 		      
		utils.vector(350, 450 + defy)])		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
	}
	
	child(template:"towerofdefense.entities.base", id:"base")	{
		position=utils.vector(350, 450 + defy)
		direction=utils.vector(-1,0)
		radius=30f
		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 0.0f)
		fillColor=utils.color(0.2f, 0.2f, 0.7f, 1.0f)
	}
	
	child(template:"towerofdefense.entities.spawner", id:"spawner")	{
		position={entity.parent.children["path"].path.getPoint(0)}
		spawnDelay=utils.interval(400,1000)
		waves=new Waves().setWaves([new Wave(1000,10,new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
				utils.custom.genericprovider.provide{ entity ->
					[
					position:entity.position.copy(),
					maxVelocity:0.05f,
					pathEntityId:"path",
					pathProperty:"path",
					color:utils.color(1.0f, 0.5f, 0.5f, 0.95f),
					health:utils.container(8,8),
					points: 5,
					reward:1			
					]	
				}	
				)), new Wave(1200,5,new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
				utils.custom.genericprovider.provide{ entity ->
					[
					position:entity.position.copy(),
					maxVelocity:0.07f,
					pathEntityId:"path",
					pathProperty:"path",
					color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
					health:utils.container(12,12),
					points: 10,
					reward:2
					]	
				}	
				)), new Wave(2500,5,new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
				utils.custom.genericprovider.provide{ entity ->
					[
					position:entity.position.copy(),
					maxVelocity:0.02f,
					pathEntityId:"path",
					pathProperty:"path",
					color:utils.color(0.0f, 1.0f, 0.0f, 1.0f),
					health:utils.container(20,20),
					points: 15,
					reward:3
					]	
				}	
				)), new Wave(1200,1000,new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
				utils.custom.genericprovider.provide{ entity ->
					[
					position:entity.position.copy(),
					maxVelocity:0.09f,
					pathEntityId:"path",
					pathProperty:"path",
					color:utils.color(0.0f, 0.0f, 1.0f, 1.0f),
					health:utils.container(15,15),
					points: 20,
					reward:4
					]	
				}	
				))])
	}
	
	
	def blastTower = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.blastertower"),
			utils.custom.genericprovider.provide{ position ->
				[
				position:position,
				direction:utils.vector(-1,0),
				radius:52f,
				lineColor:utils.color(0.0f, 0.8f, 0.0f,0.5f),
				fillColor:utils.color(0.0f, 0.8f, 0.0f,0.25f),
				color:utils.color(0.2f, 1.0f, 0.2f, 1.0f),
				template:"towerofdefense.entities.bullet",
				reloadTime:250,
				cost:5,
				instanceParameters: utils.custom.genericprovider.provide{
					[
					damage:1.0f,
					radius:10.0f,
					maxVelocity:0.6f,
					color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
					]
				}	
				]
			})
	
	def laserTower = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.lasertower"),
			utils.custom.genericprovider.provide{ position ->
				[
				position:position,
				direction:utils.vector(-1,0),
				radius:200f,
				lineColor:utils.color(0.0f, 0.0f, 0.8f,0.5f),
				fillColor:utils.color(0.0f, 0.0f, 0.8f,0.25f),
				color:utils.color(0.2f, 0.2f, 1.0f, 1.0f),
				reloadTime:250,
				cost:7
				]
			})
	
	def towerDescriptions = [blaster:[icon:"towerofdefense.images.blastertower_icon", cost:5, instantiationTemplate:blastTower], 
			laser:[icon:"towerofdefense.images.lasertower_icon", cost:7, instantiationTemplate:laserTower]]
	property("towerDescriptions", towerDescriptions )
	
	def towers = ["blaster":blastTower,
			"laser": laserTower
			]
	
	property("towerType","blaster")
	
	component(new TowerDeployer("towerdeployer")){
		property("instantiationTemplate",{towers[(entity.towerType)]})
		propertyRef("towerCount","towerCount")
	}
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		def reward = message.critter.reward
		def points = message.critter.points
		entity.money+=reward
		entity.points+=points
	}
	
	genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		entity.lives--;
		
		if(entity.lives <= 0)
			messageQueue.enqueue(utils.genericMessage("reloadScene",{}))
	}
	
	
	genericComponent(id:"reloadSceneHandler", messageId:"reloadScene"){ message ->
		utils.custom.game.loadScene("towerofdefense.scenes.scene1");
	}
	
	def labelX = 300;
	def labelY = 20;
	
	component(new LabelComponent("moneylabel")){
		property("position",utils.vector(labelX,labelY + 0))
		property("message","Money: {0}")
		property("value",{entity.money})
	}
	
	component(new LabelComponent("pointslabel")){
		property("position",utils.vector(labelX,labelY + 20))
		property("message","Points: {0}")
		property("value",{entity.points})
	}
	
	component(new LabelComponent("timerlabel")){
		property("position",utils.vector(labelX,labelY + 40))
		property("message","Timer: {0}")
		property("value",{entity.wavesTimer.timeLeft})
	}
	
	component(new LabelComponent("towerCountlabel")){
		property("position",utils.vector(labelX,labelY + 60))
		property("message","Towers: {0}")
		propertyRef("value","towerCount")
	}	
	
	component(new LabelComponent("towertypelabel")){
		property("position",utils.vector(labelX,labelY + 80))
		property("message","TowerType: {0}")
		propertyRef("value", "towerType")
	}	
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		property("bounds", utils.rectangle(0,0, 800, 600));
	}
	
	component(new TimerComponent("wavesTimerComponent")){
		property("messageBuilder",utils.custom.messageBuilderFactory.messageBuilder("nextWave") { })
		propertyRef("timer","wavesTimer")
	}
	
	genericComponent(id:"nextWaveMessageHandler", messageId:"nextWave"){ message ->
		entity.wavesTimer.reset()
	}
	

	property("deployTower", "deployState")

	
	component(new GuiLogicComponent("guiComponent")){
		// propertyRef("state", "guiState")
		propertyRef("mousePosition", "mousePosition")
		propertyRef("deployCursorState", "deployCursorState")
		propertyRef("deployTowerEnabled", "deployTowerEnabled")
		property("path",{entity.getEntityById("path").path})
		
		property("towerDescriptions", towerDescriptions)
		propertyRef("gameBounds","gameBounds")
	}
	
	property("deployTowerEnabled", false)
	property("deployCursorState", "candeploy")
	property("mousePosition", utils.vector(0f, 0f))
	
	def mapeo = [
			"candeploy":utils.color(0.0f, 0.8f, 0.0f,0.25f),
			"cantdeploy":utils.color(0.8f, 0.0f, 0.0f,0.25f)
			]
	
	component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
		property("lineColor", utils.color(0.5f, 0.5f, 0.5f, 0.1f))
		property("radius", 52.0f)
		property("fillColor", {mapeo[(entity.deployCursorState)]})
		propertyRef("position", "mousePosition")
		propertyRef("enabled", "deployTowerEnabled")
	}
	
	component(new LabelComponent("towersLabel")){
		property("position",utils.vector(40,20))
		property("message", "Towers")
	}	
	
	def towerButtonsX = 40
	def towerButtonsY = 70
	
	towerDescriptions.each { key, value -> 
	
		child(template:"towerofdefense.entities.towerbutton", id:"button-${key}".toString())	{
			position=utils.vector(towerButtonsX, towerButtonsY)
			icon=utils.resources.image(value.icon)
			mouseNotOverFillColor=utils.color(0.0f, 1.0f, 0.0f, 0.4f)
			mouseOverFillColor=utils.color(0.0f, 1.0f, 0.0f, 0.7f)
			messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("deployTowerSelected") {  message.towerType = "${key}".toString() }
		}
		
		component(new LabelComponent("towerCostLabel-${key}".toString())){
			property("position",utils.vector(towerButtonsX-10,towerButtonsY+25))
			property("message", "\$${value.cost}".toString())
		}	

		towerButtonsX+=60
	}
	
	def commandButtonX = 660
	def commandButtonY = towerButtonsY

	child(template:"towerofdefense.entities.towerbutton", id:"button-nextWave")	{
		position=utils.vector(commandButtonX, commandButtonY)
		icon=utils.resources.image("towerofdefense.images.nextwave_icon")
		mouseNotOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.4f)
		mouseOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.7f)
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("nextWave") {  }
	}

	child(template:"towerofdefense.entities.towerbutton", id:"button-restart")	{
		position=utils.vector(commandButtonX + 60, commandButtonY)
		icon=utils.resources.image("towerofdefense.images.restart_icon")
		mouseNotOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.4f)
		mouseOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.7f)
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("reloadScene") {  }
	}

	component(new ComponentFromListOfClosures("cheats",[ {GenericMessage message ->
		switch(message.id){
			case "cheatMoney":
			entity.money+=10
			break;
			case "cheatLives":
			entity.lives+=10
			break;
		}
	}
	]))
	
	input("inputmapping"){
		keyboard {
			press(button:"w", eventId:"nextWave")
			press(button:"r", eventId:"reloadScene")
			press(button:"m",eventId:"cheatMoney")
			press(button:"l",eventId:"cheatLives")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			
			press(button:"right", eventId:"rightClick");
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}
	
}