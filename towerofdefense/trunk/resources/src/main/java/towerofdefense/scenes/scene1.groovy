package towerofdefense.scenes;

import towerofdefense.GroovyBootstrapper;
import towerofdefense.components.TowerDeployer;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.components.ReflectionComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.MessageQueue 
import com.gemserk.componentsengine.templates.EntityTemplate 
import com.gemserk.componentsengine.templates.TemplateProvider 
import org.newdawn.slick.Input 
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
	
	property("money",252220)
	property("points",0)
	property("lives",15000)
	property("wavesTimer", new PeriodicTimer(15000))
	property("towerCount",0)
	
	child(template:"towerofdefense.entities.path", id:"path")	{
		path=new Path([
		utils.vector(0, 300), 		      
		utils.vector(100, 300), 
		utils.vector(200, 400), 		      
		utils.vector(400, 400), 		      
		utils.vector(400, 300), 		      
		utils.vector(670, 300)])		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
	}
	
	child(template:"towerofdefense.entities.base", id:"base")	{
		position=utils.vector(700,300)
		direction=utils.vector(-1,0)
		radius=30f
		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
		fillColor=utils.color(0.0f, 0.0f, 0.0f, 0.2f)
	}
	
	child(template:"towerofdefense.entities.spawner", id:"spawner")	{
		position=utils.vector(-10,300)
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
					reward:25			
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
					reward:30
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
					reward:35
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
					reward:40
					]	
				}	
				))])
	}
	
	component(new TowerDeployer("towerdeployer")){
		property("instantiationTemplate",new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.tower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(-1,0),
			radius:52f,
			lineColor:utils.color(0.0f, 0.1f, 0.0f, 0.8f),
			fillColor:utils.color(0.0f, 0.2f, 0.0f, 0.1f),
			color:utils.color(0.0f, 0.2f, 0.0f, 1.0f),
			template:"towerofdefense.entities.bullet",
			reloadTime:250,
			cost: 50f,
			instanceParameters: utils.custom.genericprovider.provide{
				[
				damage:1.0f,
				radius:10.0f,
				maxVelocity:0.6f,
				color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
				]
			}	
			]
		}))
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
		propertyRef("value","money")
	}
	
	component(new LabelComponent("pointslabel")){
		property("position",utils.vector(680,60))
		property("message","Points: {0}")
		propertyRef("value","points")
	}
	
	component(new LabelComponent("liveslabel")){
		property("position",utils.vector(680,80))
		property("message","Lives: {0}")
		propertyRef("value","lives")
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
		property("position",utils.vector(660,100))
		property("message","Timer: {0}")
		propertyRef("value","wavesTimer")
	}
	
	component(new LabelComponent("towerCountlabel")){
		property("position",utils.vector(660,120))
		property("message","Towers: {0}")
		propertyRef("value","towerCount")
	}	

	component(new LabelComponent("guiStateLabel")){
		property("position",utils.vector(660,140))
		property("message","GuiState: {0}")
		propertyRef("value", "guiComponent.state")
	}	

	property("deployTower", "deployState")
	
	component(new GuiLogicComponent("guiComponent")){
		property("state", "deployState")
		propertyRef("mousePosition", "mousePosition")
		propertyRef("deployCursorColor", "deployCursorColor")
		propertyRef("deployTowerEnabled", "deployTowerEnabled")
	}
	
	property("deployTowerEnabled", false)
	property("deployCursorColor", utils.color(0f, 0f, 0f, 0f))
	property("mousePosition", utils.vector(0f, 0f))
	
	component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
		property("lineColor", utils.color(0.5f, 0.5f, 0.5f, 0.1f))
		property("radius", 52.0f)
		propertyRef("fillColor", "deployCursorColor")
		propertyRef("position", "mousePosition")
		propertyRef("enabled", "deployTowerEnabled")
	}
	
	input("inputmapping"){
		keyboard {
			press(button:"w", eventId:"nextWave")
			press(button:"r", eventId:"reloadScene")
		}
		mouse {
			
			press(button:"left", eventId:"click");
			press(button:"right", eventId:"changeState");

			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
			// hold(button:"right", eventId:"deployturret");
						
		}
	}
	
}