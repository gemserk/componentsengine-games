package towerofdefense.scenes;
import org.newdawn.slick.Graphics;


import com.gemserk.componentsengine.messages.SlickRenderMessage;

import towerofdefense.GroovyBootstrapper;
import towerofdefense.components.TowerDeployer;

import com.gemserk.componentsengine.components.ReflectionComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.componentsengine.messages.AddEntityMessage 
import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.MessageQueue 
import com.gemserk.componentsengine.templates.EntityTemplate 
import com.gemserk.componentsengine.templates.TemplateProvider 
import com.google.inject.Inject 
import org.newdawn.slick.Input 

import org.newdawn.slick.Input 
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.AddEntityMessage;
import com.gemserk.componentsengine.messages.MessageQueue 
import com.gemserk.componentsengine.templates.EntityTemplate;

import com.gemserk.componentsengine.templates.TemplateProvider;

import com.gemserk.componentsengine.messages.GenericMessage;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.LabelComponent;
import com.gemserk.games.towerofdefense.Path;
import com.gemserk.games.towerofdefense.components.OutOfBoundsRemover;
import com.gemserk.games.towerofdefense.components.TimerComponent;
import com.gemserk.games.towerofdefense.timers.PeriodicTimer;
import com.gemserk.games.towerofdefense.waves.Wave;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.scene("todh.scenes.scene1") {
	
	new GroovyBootstrapper();
	
	images("assets/images.properties")
	
	components(com.gemserk.games.towerofdefense.TowerOfDefenseComponentLoader.class);
	component("groovyconsole")
	
	property("money",250f)
	property("points",0)
	property("lives",15)
	property("wavesTimer", new PeriodicTimer(15000))
	
	entity(template:"towerofdefense.entities.path", id:"path")	{
		path=new Path([
		utils.vector(0, 300), 		      
		utils.vector(100, 300), 
		utils.vector(200, 400), 		      
		utils.vector(400, 400), 		      
		utils.vector(400, 300), 		      
		utils.vector(670, 300)])		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
	}
	
	entity(template:"towerofdefense.entities.base", id:"base")	{
		position=utils.vector(700,300)
		direction=utils.vector(-1,0)
		radius=30f
		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
		fillColor=utils.color(0.0f, 0.0f, 0.0f, 0.2f)
	}
	
	entity(template:"towerofdefense.entities.spawner", id:"spawner")	{
		position=utils.vector(-10,300)
		spawnDelay=utils.interval(400,1000)
		waves=new Waves().setWaves([new Wave(1000,10,new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
				utils.custom.genericprovider.provide{ entity ->
					[
					position:entity.position.copy(),
					maxVelocity:0.06f,
					pathEntityId:"path",
					pathProperty:"path",
					color:utils.color(1.0f, 0.5f, 0.5f, 0.95f),
					health:utils.container(100,100),
					points: 5,
					reward:25f					
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
					health:utils.container(125,125),
					points: 10,
					reward:30f
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
					health:utils.container(350,350),
					points: 15,
					reward:35f
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
					health:utils.container(335,335),
					points: 20,
					reward:40f
					]	
				}	
				))])
	}
	
	entity(template:"towerofdefense.entities.generic", id:"towerDeployer")	{
		
		property_template="towerofdefense.entities.tower"
		property_instanceParameters = utils.custom.genericprovider.provide{
			[
			direction:utils.vector(-1,0),
			radius:100f,
			lineColor:utils.color(0.0f, 0.0f, 0.0f, 0.2f),
			fillColor:utils.color(0.0f, 0.0f, 0.0f, 0.0f),
			color:utils.color(0.0f, 0.2f, 0.0f, 1.0f),
			template:"towerofdefense.entities.bullet",
			reloadTime:800,
			cost: 50f,
			instanceParameters: utils.custom.genericprovider.provide{
				[
				damage:25.0f,
				radius:10.0f,
				maxVelocity:0.5f,
				color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
				]
			}	
			]
		}
		
		component_towerdeployer=new TowerDeployer("towerdeployer")
	}
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		def reward = message.critter.reward
		def points = message.critter.points
		scene.money+=reward
		scene.points+=points
	}
	
	genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		def scene = message.scene
		scene.lives--;
		
		if(scene.lives <= 0)
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
		message.scene.wavesTimer.reset()
	}
	
	
	component(new LabelComponent("timerlabel")){
		property("position",utils.vector(660,100))
		property("message","Timer: {0}")
		propertyRef("value","wavesTimer")
	}
	
	
	input("inputmapping"){
		keyboard {
			press(button:"w", eventId:"nextWave")
			press(button:"r", eventId:"reloadScene")
		}
		mouse {
			
			press(button:"left", eventId:"deployturret");
			
			
		}
	}
	
}