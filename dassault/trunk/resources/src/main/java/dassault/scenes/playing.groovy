package dassault.scenes;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;

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
	
	child(entity("restartLabel"){
		
		parent("gemserk.gui.label", [
		font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.vector(300f, 520f),
		fontColor:utils.color(1f,1f,1f,0f),
		bounds:utils.rectangle(-220,-50,440,100),
		align:"center",
		valign:"center",
		layer:1010
		])
		
		property("message", "GAME OVER: press ESCAPE to restart")
	})
	
	property("gameOver", false)
	
	child(entity("gameLogic") {
		
		property("playerId", "player")
		property("pointsForKillingADroid", 100)
		
		component(utils.components.genericComponent(id:"detectGameOver", messageId:"update"){ message ->
			
			def controlledDroids = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), //
			{ droid -> droid.ownerId == entity.playerId} as Predicate))
			
			if (!controlledDroids.empty)
				return
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("gameover"){ newMessage ->
				// newMessage.points = ...
			})
			
		})
		
		component(utils.components.genericComponent(id:"gameOverHandler", messageId:"gameover"){ message ->
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
			
			// TODO: "game over message, press ... key to restart"
			// show label!
			
			def label = entity.root.getEntityById("restartLabel")
			label.color.a = 1f
			
			entity.parent.gameOver = true
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
		parent("dassault.entities.basicdroid",[ownerId:"player",position:utils.vector(400,300), 
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
	
	def obstacleForm = new Polygon([1f, 1f, 4f, 1f, 4f, 4f, 2.5f, 5f, 1f, 4f] as float[])
	def obstacleForm2 = new Polygon([6.49f, -22.29f, 25.25f, 1.13f, 1.84f, 19.88f, -15.35f, 14.43f, -16.92f, -3.53f, -1.31f, -9.63f] as float[])
	
	// def obstacleForm3 = new Polygon([-13.18f, -19.1f, 16.82f, -18.87f, 45f, -26f, 101f, -66f, 169f, -75f, 175f, -12f, 77f, -17f, 47f, -3f, 16.58f, 11.13f, 38f, 18f, 135f, 65f, 52f, 41f, 33f, 57f, 29f, 32f, 7f, 41f, 1.5f, 21.01f, -13.42f, 10.89f, -8.29f, -5.07f] as float[])
	def obstacleForm3 = new Polygon([58.05f, 46.16f, 30.43f, 34.46f, 0.74f, 29.88f, -19.3f, 12.92f, -45.41f, 36.74f, -36.27f, 64.34f, -23.31f, 82.7f, -5.43f, 63.05f, 15.03f, 60.7f, 23.15f, 80.29f, 5.15f, 79.33f, -2.35f, 100.04f, -28.48f, 110.86f, -44.37f, 89.13f, -50.19f, 63.99f, -85.88f, 66.52f, -108.5f, 63.65f, -117.48f, 38.28f, -117.38f, 14.51f, -123.29f, -18.25f, -110.82f, -50.97f, -108.96f, -81.59f, -73.41f, -73.35f, -54.89f, -84.08f, -31.15f, -96.98f, 1.19f, -83.59f, 19.89f, -79.09f, 46.05f, -76.91f, 71.44f, -72.89f, 96.07f, -67.01f, 120.48f, -57.99f, 146.91f, -32.97f, 152.51f, -4.67f, 160.25f, 15.85f, 135.51f, 33.75f, 112.19f, 27.34f, 97.92f, 9.52f, 120.68f, 1.63f, 113.48f, -17.59f, 93.45f, -34.55f, 69.74f, -40.04f, 42.89f, -45.75f, 18.1f, -53.85f, -2.77f, -63.58f, -27.49f, -77.06f, -49.23f, -61.17f, -69.37f, -54.36f, -89.82f, -52.01f, -92.38f, -24.93f, -102.56f, 2.25f, -100.43f, 25.86f, -94.61f, 51f, -65.55f, 43.56f, -63.59f, 20.55f, -59.06f, -16.54f, -9.89f, -12.41f, 7.08f, 11.94f, 24.58f, -6.79f, 42.13f, 6.83f, 59.84f, 3.47f, 69.75f, 18.53f, 58.91f, 31.31f] as float[])

	// def obstacleForm =
	
	//  
	
	obstacleForm.centerX = 0f
	obstacleForm.centerY = 0f
	
	obstacleForm2.centerX = 0f
	obstacleForm2.centerY = 0f

	Transform rotationMatrix = Transform.createRotateTransform(30);

//	child(id:"obstacle1", template:"dassault.entities.obstacle") { 
//		position = utils.vector(200,200)
//		// bounds = utils.rectangle(-50, -10, 100, 20)
//		bounds = obstacleForm.transform(Transform.createScaleTransform(30f, 30f))
//		color = utils.color(0.1f,0f,0f,1f)
//		layer = 19
//	}
//	
//	child(id:"obstacle2", template:"dassault.entities.obstacle") { 
//		position = utils.vector(400,400)
//		// bounds = utils.rectangle(-50, -10, 100, 20)
//		bounds = obstacleForm2.transform(rotationMatrix).transform(Transform.createScaleTransform(2f, 2f))
//		color = utils.color(0.1f,0f,0f,1f)
//		layer = 19
//	}
	
	child(id:"obstacleForm3", template:"dassault.entities.obstacle") { 
		position = utils.vector(200,100)
		// bounds = utils.rectangle(-50, -10, 100, 20)
		bounds = obstacleForm3.transform(Transform.createScaleTransform(3f, 3f)).transform(Transform.createRotateTransform(1f))
		color = utils.color(0.1f,0.1f,0f,1f)
		layer = 19
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
	
	def globalDroidFactory = [basicDroid:basicDroidInstantiationTemplate, 
			floatingDroid:floatingDroidInstantiationTemplate]
	
	def globalWeaponFactory = [blasterWeapon:blasterWeaponInstantiationTemplate]
	
	def globalDroidTypes = globalDroidFactory.keySet().collect { it }
	def globalWeaponTypes = globalWeaponFactory.keySet().collect { it }
	
	child(id:"spawner1", template:"dassault.entities.droidspawner") { 
		position = utils.vector(100,100)
		minTime = 5000
		maxTime = 5000
		droidFactory = globalDroidFactory
		weaponFactory = globalWeaponFactory
		ownerId = "cpu1"
		droidTypes = globalDroidTypes
		weaponTypes = globalWeaponTypes
	}
	
	child(id:"spawner2", template:"dassault.entities.droidspawner") { 
		position = utils.vector(700,100)
		minTime = 5000
		maxTime = 5000
		droidFactory = globalDroidFactory
		weaponFactory = globalWeaponFactory
		ownerId = "cpu2"
		droidTypes = globalDroidTypes
		weaponTypes = globalWeaponTypes
	}
	
	child(id:"spawner3", template:"dassault.entities.droidspawner") { 
		position = utils.vector(100,500)
		minTime = 5000
		maxTime = 5000
		droidFactory = globalDroidFactory
		weaponFactory = globalWeaponFactory
		ownerId = "cpu3"
		droidTypes = globalDroidTypes
		weaponTypes = globalWeaponTypes
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
		if(!entity.gameOver)
			messageQueue.enqueue(utils.genericMessage("paused"){})
		else
			messageQueue.enqueue(utils.genericMessage("restartLevel"){})
	})
	
}
