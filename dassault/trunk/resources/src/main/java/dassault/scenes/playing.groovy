package dassault.scenes;

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.google.common.base.Predicate 
import com.google.common.base.Predicates 
import gemserk.utils.GroovyBootstrapper 

builder.entity("playing") {
	
	new GroovyBootstrapper();
	
	child(id:"camera", template:"dassault.entities.camera") { 
		position = utils.vector(0,0)
		ownerId = "player"
		screen = utils.rectangle(0,0, 800, 600)
		followMouse = true
	}
	
	child(entity("gameLogic") {
		
		property("playerId", "player")
		
		component(utils.components.genericComponent(id:"detectGameOver", messageId:"update"){ message ->
			
			def controlledDroids = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("droid"), //
			{ droid -> droid.ownerId == entity.playerId} as Predicate))
			
			if (!controlledDroids.empty)
				return
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("gameover"){ newMessage ->
				// newMessage.points = ...
			})
			
		})
		
	})
	
	child(entity("player") {
		
		property("controlledDroidId", "droid1")
		property("color", utils.color(0.62f, 0.83f, 0.87f,1f))
		
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
			
			// send changeControlledDroid!
			//				entity.controlledDroidId = controlledDroids[0].id
			
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
		parent("dassault.entities.droid",[ownerId:"player",position:utils.vector(400,300), 
		speed:0.2f, 
		energy:utils.container(10000f,10000f),
		regenerationSpeed:0.02f])
		
		child(id:"blasterWeapon1", template:"dassault.entities.blasterweapon") { 
			ownerId = "droid1"
			reloadTime = 400
			damage = 30f
			energy = 20f
			bulletTemplate = utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
		}
	} )
	
	child(entity("cpu") {
		property("color", utils.color(1,0,0,1f))
	})
	
	child(id:"cpuController", template:"dassault.entities.aicontroller") {  ownerId = "cpu"  }
	
	child(id:"obstacle1", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,100)
		bounds = utils.rectangle(-50, -10, 100, 20)
	}
	
	// scene limits as obstacles
	
	child(id:"sceneLimit1", template:"dassault.entities.obstacle") { 
		position = utils.vector(0,300)
		bounds = utils.rectangle(-10, -300, 20, 600)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit2", template:"dassault.entities.obstacle") { 
		position = utils.vector(800,300)
		bounds = utils.rectangle(-10, -300, 20, 600)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit3", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,0)
		bounds = utils.rectangle(-400, -10, 800, 20)
		color = utils.color(0,0,0,1)
	}
	
	child(id:"sceneLimit4", template:"dassault.entities.obstacle") { 
		position = utils.vector(400,600)
		bounds = utils.rectangle(-400, -10, 800, 20)
		color = utils.color(0,0,0,1)
	}
	
	def blasterWeaponInstantiationTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dassault.entities.blasterweapon"), 
			utils.custom.genericprovider.provide{ data ->
				[
				ownerId:data.ownerId,
				reloadTime:500,
				damage:30f,
				energy:20f,
				bulletTemplate:utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
				]
			})
	
	def droidInstantiationTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dassault.entities.droid"), 
			utils.custom.genericprovider.provide{ data ->
				[
				ownerId:data.ownerId,
				position:data.position,
				speed:0.1f,
				energy:utils.container(50f,50f),
				regenerationSpeed:0.02f
				]
			})
	
	def enemyDroidFactory = [basicDroid:{ params -> droidInstantiationTemplate.get(params) }, 
	blasterWeapon:{ params -> blasterWeaponInstantiationTemplate.get(params) }]
	
	child(id:"spawner1", template:"dassault.entities.droidspawner") { 
		position = utils.vector(100,100)
		minTime = 5000
		maxTime = 5000
		droidFactory = enemyDroidFactory
		ownerId = "cpu"
	}
	
	child(id:"spawner2", template:"dassault.entities.droidspawner") { 
		position = utils.vector(700,100)
		minTime = 8000
		maxTime = 1000
		droidFactory = enemyDroidFactory
		ownerId = "cpu"
	}
	
	child(id:"cursor", template:"dassault.entities.cursor") {
		camera = {entity.root.getEntityById("camera")}
	}
	
	component(new ExplosionComponent("explosions")) { }
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			press(button:"space",eventId:"pauseGame")
			press(button:"h",eventId:"helpscreen")
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
