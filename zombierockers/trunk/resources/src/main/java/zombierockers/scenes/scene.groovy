package zombierockers.scenes;

import com.gemserk.componentsengine.commons.components.states.NodeStateTransitionManagerComponent;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.utils.EntityDumper 
import gemserk.utils.GroovyBootstrapper 
import net.sf.json.JSONArray 

builder.entity("game") { 
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	property("currentLevelIndex", parameters.levelIndex ?: 0)
	
	// TODO: find better way to define balls, now it is not understandable
	
	def allBallDefinitions = [
			"red":[type:"red", animation:"ballanimation_white", color:utils.color(1,0,0)],
			"blue":[type:"blue", animation:"ballanimation_white", color:utils.color(0,0,1)],
			"green":[type:"green", animation:"ballanimation_white", color:utils.color(0,1,0)],
			"yellow":[type:"yellow", animation:"ballanimation_white", color:utils.color(1,1,0)],
			"violet":[type:"violet", animation:"ballanimation_white", color:utils.color(1,0,1)],
			"white":[type:"white", animation:"ballanimation_white", color:utils.color(1,1,1)],
			]
	
	def level01 = [background:"level01", path:"levels/level01/path.svg", ballsQuantity:40, 
			pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:700f, minSpeedFactor:0.2f, maxSpeed:0.04f, speedWhenReachBase:0.4f],
			ballDefinitions:["red":allBallDefinitions.red, "blue":allBallDefinitions.blue, "green":allBallDefinitions.green]
			]
	
	def level02 = [background:"level02", path:"levels/level02/path.svg",ballsQuantity:60, 
			pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:800f, minSpeedFactor:0.2f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
			ballDefinitions:["red":allBallDefinitions.red, "blue":allBallDefinitions.blue, "green":allBallDefinitions.green]
			]	
	
	def level03 = [background:"level03", path:"levels/level03/path.svg",ballsQuantity:80, 
			pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:800f, minSpeedFactor:0.2f, maxSpeed:0.04f, speedWhenReachBase:0.4f],
			ballDefinitions:["red":allBallDefinitions.red, "blue":allBallDefinitions.blue, "green":allBallDefinitions.green]
			]			
	
	def level04 = [background:"level04", path:"levels/level04/path.svg",ballsQuantity:100, 
			pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
			ballDefinitions:["red":allBallDefinitions.red, "blue":allBallDefinitions.blue, "green":allBallDefinitions.green, "yellow":allBallDefinitions.yellow]
			]		
	
	def level05 = [background:"level05", path:"levels/level05/path.svg",ballsQuantity:100, 
			pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
			ballDefinitions:["red":allBallDefinitions.red, "blue":allBallDefinitions.blue, "green":allBallDefinitions.green, "yellow":allBallDefinitions.yellow]
			]				
	def levels = [level01, level02, level03, level04, level05]
	
	def currentLevel = levels[entity.currentLevelIndex]
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	component(new NodeStateTransitionManagerComponent("stateChanger")){
		property("transitions",[
		gameover:["gameover"],
		paused:["paused"],
		resume:["playing"],
		enterscore:["enterscore"]
		])
	}
	
	child(entity("playing"){ 
		parent("gemserk.states.stateBasedNode",[enabled:true,exclusions:[SlickRenderMessage.class]])
		parent("zombierockers.scenes.playing", [level:currentLevel]) 
	})
	
	child(entity("gameover"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("zombierockers.scenes.gameover")
	})
	
	child(entity("paused"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("zombierockers.scenes.paused")
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"x",eventId:"dumpEntities")
		}
	}
	component(utils.components.genericComponent(id:"dumpEntitiesHandler", messageId:"dumpEntities"){ message ->
		println JSONArray.fromObject(new EntityDumper().dumpEntity(entity.root)).toString(4)
	} )
	
	property("sceneTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.scenes.scene"), 
			utils.custom.genericprovider.provide{ data ->[levelIndex:data.levelIndex]}))
	
	component(utils.components.genericComponent(id:"nextLevelHandler", messageId:"nextLevel"){ message ->
		def	levelIndex = (entity.currentLevelIndex + 1) % levels.size
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
	
}