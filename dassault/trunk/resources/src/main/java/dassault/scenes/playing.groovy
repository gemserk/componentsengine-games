package dassault.scenes;

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.games.dassault.components.LinearMovementComponent;
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 
import gemserk.utils.GroovyBootstrapper 

builder.entity("playing") {
	
	new GroovyBootstrapper();
	
	child(id:"light1", template:"dassault.entities.pointlight") { 
		position = utils.vector(100,100)
		layer = 10
		size = 5f
		time = 1500
		startColor = utils.color(0.2f,0.2f,0.7f,0.2f)
		endColor = utils.color(0.2f,0.2f,0.7f,0.6f)
	}
	
	child(id:"light2", template:"dassault.entities.pointlight") { 
		position = utils.vector(700,100)
		layer = 10
		size = 5f
		time = 750
		startColor = utils.color(0.7f,0.2f,0.2f,0.2f)
		endColor = utils.color(0.7f,0.2f,0.2f,0.8f)
	}
	
	child(id:"light3", template:"dassault.entities.pointlight") { 
		position = utils.vector(100,500)
		layer = 10
		size = 8f
		time = 1000
		startColor = utils.color(0.2f,0.7f,0.2f,0.2f)
		endColor = utils.color(0.2f,0.7f,0.2f,0.8f)
	}
	
	child(entity("light4") {
		
		parent("dassault.entities.pointlight", [
		position: utils.vector(700,500),
		layer: 10,
		size: 8f,
		time: 3000,
		startColor: utils.color(0.2f,0.2f,0.7f,0.2f),
		endColor: utils.color(0.2f,0.2f,0.7f,0.8f),
		])
		
		component(new LinearMovementComponent("linearMovementComponent")) { propertyRef("position", "position") }
		
		component(utils.components.genericComponent(id:"moveLightHandler", messageId:"moveLight"){ message ->
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("moveTo"){ newMessage ->
				newMessage.target = utils.vector(100,100)
				newMessage.time = 10000
				newMessage.entityId = entity.id
			})
			
		})
		
		input("inputmapping"){
			keyboard {
				press(button:"m",eventId:"moveLight")
			}
			mouse {
			}
		}
		
	} )
	
	child(id:"camera", template:"dassault.entities.camera") { 
		position = utils.vector(0,0)
		ownerId = "player"
		screen = utils.rectangle(0,0, 600, 600)
		followMouse = false
	}
	
	child(id:"cameracontroller", template:"dassault.entities.cameracontroller") { 
		cameraId = "camera"
		controlledDroidId = "droid1"
	}
	
	child(id:"hud", template:"dassault.entities.hud") { playerId = "player" }
	
	child(entity("gameLogic") {
		
		property("playerId", "player")
		property("pointsForKillingADroid", 100)
		
		component(utils.components.genericComponent(id:"detectGameOver", messageId:"update"){ message ->
			
			//			utils.custom.messageQueue.enqueue(utils.genericMessage("zoom"){ newMessage ->
			//				newMessage.cameraId = "camera"
			//				newMessage.end = 0.5f
			//				newMessage.time = 500
			//			})	
			//			
			//			return
			
			// TODO: "game over message, press ... key to restart"
		
			def controlledDroids = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), //
			{ droid -> droid.ownerId == entity.playerId} as Predicate))
			
			if (!controlledDroids.empty)
				return
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("zoom"){ newMessage ->
				newMessage.cameraId = "camera"
				newMessage.end = 0.5f
				newMessage.time = 500
			})	
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("moveTo"){ newMessage ->
				newMessage.entityId = "camera"
				newMessage.target = utils.vector(400, 300)
				newMessage.time = 300
			})
			
			return
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("gameover"){ newMessage ->
				// newMessage.points = ...
			})
			
		})
		
		component(utils.components.genericComponent(id:"incrementPlayerPointsWhenDroidDies", messageId:"droidDead"){ message ->
			
			def droid = message.droid
			
			if (droid.ownerId == entity.playerId) 
				return
			
			def player = entity.root.getEntityById(entity.playerId)
			player.points += entity.pointsForKillingADroid
			
			//			println "points: $player.points"
		})
		
	})
	
	child(entity("player") {
		
		property("controlledDroidId", "droid1")
		property("color", utils.color(0.42f, 0.43f, 0.67f,1f))
		property("points", 0)
		
		component(utils.components.genericComponent(id:"changeControlledDroid", messageId:"changeControlledDroid"){ message ->
			entity.controlledDroidId = message.controlledDroid.id 
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("changeOwner"){ newMessage ->
				newMessage.controlledDroid = message.controlledDroid
				newMessage.ownerId = entity.id
			})
			
		})
		
		component(utils.components.genericComponent(id:"selectNextControlledDroid", messageId:"update"){ message ->
			
			def controlledDroidId = entity.controlledDroidId
			def controlledDroid = entity.root.getEntityById(controlledDroidId)
			
			if (controlledDroid)
				return
			
			def controlledDroids = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), //
					{ droid -> droid.ownerId == entity.id} as Predicate))
			
			if (controlledDroids.empty)
				return
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("changeControlledDroid"){ newMessage ->
				newMessage.controlledDroid = controlledDroids[0]
			})
			
		})
		
	})
	
	child(id:"playerController", template:"dassault.entities.keyboardcontroller") {
		ownerId = "player"
		camera = "camera"
		leftKey = Input.KEY_A
		rightKey = Input.KEY_D
		upKey = Input.KEY_W
		downKey = Input.KEY_S
		secondaryLeftKey = Input.KEY_LEFT
		secondaryRightKey = Input.KEY_RIGHT
		secondaryUpKey = Input.KEY_UP
		secondaryDownKey = Input.KEY_DOWN
	}
	
	child(id:"playerAiHelperController", template:"dassault.entities.aicontroller") {  ownerId = "player"  }
	
	child(entity("droid1") {
		parent("dassault.entities.floatingdroid",[ownerId:"player",position:utils.vector(400,300), 
		speed:0.2f, 
		energy:utils.container(10000f,10000f),
		regenerationSpeed:0.02f])
		
		child(id:"blasterWeapon1", template:"dassault.entities.blasterweapon") { 
			ownerId = "droid1"
			reloadTime = 200
			damage = 30f
			energy = 10f
			bulletTemplate = utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
		}
	} )
	
	child(entity("cpu1") {
		property("color", utils.color(0,0,1,1f))
	})
	child(id:"cpuController1", template:"dassault.entities.aicontroller") {  ownerId = "cpu1"  }
	
	child(entity("cpu2") {
		property("color", utils.color(1,0,0,1f))
	})
	child(id:"cpuController2", template:"dassault.entities.aicontroller") {  ownerId = "cpu2"  }
	
	child(entity("cpu3") {
		property("color", utils.color(0,1,0,1f))
	})
	child(id:"cpuController3", template:"dassault.entities.aicontroller") {  ownerId = "cpu3"  }
	
	child(id:"obstacle1", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,100)
		bounds = utils.rectangle(-50, -10, 100, 20)
	}
	
	// scene limits as obstacles
	
	child(id:"sceneLimit1", template:"dassault.entities.obstacle") { 
		position = utils.vector(-1000,300)
		bounds = utils.rectangle(-1010, -2000, 2020, 4000)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit2", template:"dassault.entities.obstacle") { 
		position = utils.vector(1800,300)
		bounds = utils.rectangle(-1010, -2000, 2020, 4000)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit3", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,-1000)
		bounds = utils.rectangle(-400, -1010, 800, 2020)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit4", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,1600)
		bounds = utils.rectangle(-400, -1010, 800, 2020)
		color = utils.color(0,0,0,1)
	}
	
	def blasterWeaponInstantiationTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dassault.entities.blasterweapon"), 
			utils.custom.genericprovider.provide{ data ->
				[
				ownerId:data.ownerId,
				reloadTime:250,
				damage:30f,
				energy:20f,
				bulletTemplate:utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
				]
			})
	
	def basicDroidInstantiationTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dassault.entities.basicdroid"), 
			utils.custom.genericprovider.provide{ data ->
				[
				ownerId:data.ownerId,
				position:data.position,
				speed:0.1f,
				energy:utils.container(50f,50f),
				regenerationSpeed:0.02f
				]
			})
	
	def floatingDroidInstantiationTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dassault.entities.floatingdroid"), 
			utils.custom.genericprovider.provide{ data ->
				[
				ownerId:data.ownerId,
				position:data.position,
				speed:0.2f,
				energy:utils.container(70f,70f),
				regenerationSpeed:0.02f
				]
			})
	
	def globalDroidFactory = [basicDroid:{ params -> basicDroidInstantiationTemplate.get(params) }, 
	floatingDroid:{ params -> floatingDroidInstantiationTemplate.get(params) }]
	
	def globalWeaponFactory = [blasterWeapon:{ params -> blasterWeaponInstantiationTemplate.get(params) }]
	
	child(id:"spawner1", template:"dassault.entities.droidspawner") { 
		position = utils.vector(100,100)
		minTime = 5000
		maxTime = 5000
		droidFactory = globalDroidFactory
		weaponFactory = globalWeaponFactory
		ownerId = "cpu1"
		droidTypes = ["basicDroid", "floatingDroid"]
	}
	
	child(id:"spawner2", template:"dassault.entities.droidspawner") { 
		position = utils.vector(700,100)
		minTime = 5000
		maxTime = 5000
		droidFactory = globalDroidFactory
		weaponFactory = globalWeaponFactory
		ownerId = "cpu2"
		droidTypes = ["basicDroid", "floatingDroid"]
	}
	
	child(id:"spawner3", template:"dassault.entities.droidspawner") { 
		position = utils.vector(100,500)
		minTime = 5000
		maxTime = 5000
		droidFactory = globalDroidFactory
		weaponFactory = globalWeaponFactory
		ownerId = "cpu3"
		droidTypes = ["basicDroid", "floatingDroid"]
	}
	
	child(id:"cursor", template:"dassault.entities.cursor") {
		camera = {entity.root.getEntityById("camera")}
	}
	
	component(new ExplosionComponent("explosions")) { }
	
	property("zoomIn", true)
	property("cameraId", "camera")
	
	component(utils.components.genericComponent(id:"toggleZoom", messageId:"toggleZoom"){ message ->
		
		def zoomTo = 1.0f
		
		if (entity.zoomIn) 
			zoomTo = 1.7f
		
		entity.zoomIn = !entity.zoomIn
		
		def time = 200
		utils.custom.messageQueue.enqueue(utils.genericMessage("zoom"){ newMessage ->
			newMessage.cameraId = entity.cameraId
			newMessage.end = zoomTo
			newMessage.time = time
		})		
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			press(button:"space",eventId:"pauseGame")
			press(button:"h",eventId:"helpscreen")
			press(button:"z",eventId:"toggleZoom")
		}
		mouse {
			press(button:"left", eventId:"leftmouse")
			press(button:"right", eventId:"rightmouse")
			move(eventId:"movemouse") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
	property("shouldGrabMouse",true)
	
	component(utils.components.genericComponent(id:"grabMouse", messageId:"update"){ message ->
		if(entity.shouldGrabMouse && !utils.custom.gameStateManager.gameProperties.runningInDebug)
			utils.custom.gameContainer.setMouseGrabbed(true)
	})
	
	component(utils.components.genericComponent(id:"grabMouse-enternodestate", messageId:"enterNodeState"){ message ->
		entity.shouldGrabMouse = true
		log.info("Entering playing state")
	})
	
	component(utils.components.genericComponent(id:"grabMouse-leavenodestate", messageId:"leaveNodeState"){ message ->
		utils.custom.gameContainer.setMouseGrabbed(false)
		entity.shouldGrabMouse = false
		log.info("Leaving playing state")
	})
	
	component(utils.components.genericComponent(id:"grabscreenshot-leavenodestate", messageId:"leaveNodeState"){ message ->
		def graphics = utils.custom.gameContainer.graphics
		graphics.copyArea(utils.custom.gameStateManager.gameProperties.screenshot, 0, 0); 
	})
	
	component(utils.components.genericComponent(id:"enterPauseWhenLostFocus", messageId:"update"){ message ->
		if(!utils.custom.gameContainer.hasFocus())
			messageQueue.enqueue(utils.genericMessage("paused"){})
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("paused"){})
	})
	
}
