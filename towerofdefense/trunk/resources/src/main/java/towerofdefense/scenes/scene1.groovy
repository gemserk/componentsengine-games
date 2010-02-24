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
	
	child(template:"towerofdefense.entities.path", id:"path")	{
		path=new Path([
		utils.vector(100, 600), 		      
		utils.vector(100, 300), 
		utils.vector(300, 100), 		      
		utils.vector(500, 100), 		      
		utils.vector(700, 300), 		      
		utils.vector(500, 500), 		      
		utils.vector(500, 500), 		      
		utils.vector(350, 450)])		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
	}
	
	child(template:"towerofdefense.entities.base", id:"base")	{
		position=utils.vector(350, 450)
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
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.tower"),
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
				cost: 5f,
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
				cost: 5f
				]
			})
	
	def towers = ["blaster":blastTower,
			"laser": laserTower
			]
	
	property("towertype","blaster")
	
	component(new TowerDeployer("towerdeployer")){
		property("instantiationTemplate",{towers[(entity.towertype)]})
		propertyRef("towerCount","towerCount")
	}
	
	//	genericComponent(id:"towerTypeHandler", messageId:"changeTowerType"){ message ->
	//		entity.towertype = ["blaster":"laser","laser":"blaster"][entity.towertype]
	//	}
	
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
	
	component(new LabelComponent("nextWaveInstructionsLabel")){
		property("position",utils.vector(100,50))
		property("message","Press 'w' to send the next wave")
	}
	
	component(new LabelComponent("resetInstructionsLabel")){
		property("position",utils.vector(100,70))
		property("message","Press 'r' to restart the game")
	}
	
	component(new LabelComponent("exitInstructionsLabel")){
		property("position",utils.vector(100,90))
		property("message","Press 'esc' to exit the game")
	}
	
	component(new LabelComponent("moneylabel")){
		property("position",utils.vector(680,40))
		property("message","Money: {0}")
		//		propertyRef("value","money")
		property("value",{entity.money})
	}
	
	component(new LabelComponent("pointslabel")){
		property("position",utils.vector(680,60))
		property("message","Points: {0}")
		//		propertyRef("value","points")
		property("value",{entity.points})
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
	
	component(new LabelComponent("timerlabel")){
		property("position",utils.vector(640,100))
		property("message","Timer: {0}")
		property("value",{entity.wavesTimer.timeLeft})
	}
	
	component(new LabelComponent("towerCountlabel")){
		property("position",utils.vector(640,120))
		property("message","Towers: {0}")
		propertyRef("value","towerCount")
	}	
	
	component(new LabelComponent("towertypelabel")){
		property("position",utils.vector(640,140))
		property("message","TowerType: {0}")
		propertyRef("value", "towertype")
	}	
	
	property("deployTower", "deployState")
	property("guiState", "selectTowerState")
	
	component(new GuiLogicComponent("guiComponent")){
		propertyRef("state", "guiState")
		propertyRef("mousePosition", "mousePosition")
		propertyRef("deployCursorState", "deployCursorState")
		propertyRef("deployTowerEnabled", "deployTowerEnabled")
		property("path",{entity.getEntityById("path").path})
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
	
	genericComponent(id:"towerTypeHandler", messageId:"deployTowerSelected"){ message ->
		entity.towertype = message.towerType
		entity.guiState = "deployState"
	}
	
	child(template:"towerofdefense.entities.towerbutton", id:"blasterTowerButton")	{
		position=utils.vector(100,560)
		towerImage=utils.resources.image("towerofdefense.images.blastertower")
		cannonImage=utils.resources.image("towerofdefense.images.blastercannon")
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("deployTowerSelected") { 
			message.towerType = "blaster"
		}
	}
	
	child(template:"towerofdefense.entities.towerbutton", id:"laserTowerButton")	{
		position=utils.vector(160,560)
		towerImage=utils.resources.image("towerofdefense.images.lasertower")
		cannonImage=utils.resources.image("towerofdefense.images.lasercannon")
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("deployTowerSelected") { 
			message.towerType = "laser"
		}
	}
	
	
	
	component(new ComponentFromListOfClosures("cheats",[
	                                                    {GenericMessage message ->
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
			
			//press(button:"right", eventId:"changeState");
			//press(button:"right", eventId:"changeTowerType");
			press(button:"right", eventId:"rightClick");
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
			// hold(button:"right", eventId:"deployturret");
			
		}
	}
	
}