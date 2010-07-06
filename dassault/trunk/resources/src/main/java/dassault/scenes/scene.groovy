package dassault.scenes;

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	child(id:"camera", template:"dassault.entities.camera") { 
		position = utils.vector(0,0)
		ownerId = "player"
		screen = utils.rectangle(0,0, 800, 600)
		followMouse = false
	}
	
	child(entity("player") {
		property("controlledDroidId", "droid1")
		
		component(utils.components.genericComponent(id:"changeControlledDroid", messageId:"changeControlledDroid"){ message ->
			entity.controlledDroidId = message.controlledDroid.id 
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("changeOwner"){ newMessage ->
				newMessage.controlledDroid = message.controlledDroid
				newMessage.ownerId = entity.id
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
	}
	
	child(id:"playerAiHelperController", template:"dassault.entities.aicontroller") {  ownerId = "player"  }
	
	child(entity("droid1") {
		parent("dassault.entities.droid",[ownerId:"player",position:utils.vector(300,400), 
		speed:0.2f, 
		energy:utils.container(10000f,10000f),
		regenerationSpeed:0.02f])
		
		child(id:"blasterWeapon1", template:"dassault.entities.blasterweapon") { 
			ownerId = "droid1"
			reloadTime = 100
			damage = 30f
			energy = 20f
			bulletTemplate = utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
		}
	} )
	
	child(entity("cpu") {
		
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
				reloadTime:100,
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
	
	//	weapon1 = {
	//		template = "blasterweapon"
	//		speed = 0.02f
	//		reloadTime = 1000
	//	}
	
	//	def weapon1 = [type:"blasterWeapon", reloadTime:100, damage:30f, energy:20f]
	//	def droid1 = [type:"basicDroid", speed:0.1f, energy:utils.container(50f,50f), regenerationSpeed:0.02f]
	//	def droid2 = [type:"basicDroid", speed:0.1f, energy:utils.container(100f,100f), regenerationSpeed:0.03f, weapon:"weapon1"]
	
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
	
	component(new ExplosionComponent("explosions")) { }
	
	input("inputmapping"){
		keyboard {
		}
		mouse {
			
		}
	}
	
	
	
}
