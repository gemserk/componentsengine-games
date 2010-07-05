package dassault.scenes;

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	child(id:"camera", template:"dassault.entities.camera") { 
		position = utils.vector(0,0)
		owner = "droid1"
		screen = utils.rectangle(0,0, 800, 600)
		followMouse = true
	}
	
	child(id:"playerController", template:"dassault.entities.keyboardcontroller") {
		player = "player1"
		camera = "camera"
		leftKey = Input.KEY_A
		rightKey = Input.KEY_D
		upKey = Input.KEY_W
		downKey = Input.KEY_S
	}
	
	child(entity("droid1") {
		tags("player1", "blasterweapon")
		parent("dassault.entities.droid",[position:utils.vector(300,400), speed:0.2f])
	} )
	
	child(id:"blasterWeapon1", template:"dassault.entities.blasterweapon") { 
		owner = "droid1"
		reloadTime = 100
		damage = 30f
		bulletTemplate = utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
	}
	
	child(id:"cpuController", template:"dassault.entities.aicontroller") { player = "cpu" }
	
	child(entity("droid2") {
		tags("cpu")
		parent("dassault.entities.droid",[position:utils.vector(200,200)])
	} )
	
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
	
	child(id:"spawner1", template:"dassault.entities.droidspawner") { 
		position = utils.vector(100,100)
		minTime = 5000
		maxTime = 5000
		droidTemplate = utils.custom.templateProvider.getTemplate("dassault.entities.droid")
	}
	
	child(id:"spawner2", template:"dassault.entities.droidspawner") { 
		position = utils.vector(700,100)
		minTime = 8000
		maxTime = 1000
		droidTemplate = utils.custom.templateProvider.getTemplate("dassault.entities.droid")
	}
	
//	component(utils.components.genericComponent(id:"shootBulletHandler", messageId:"shootBullet"){ message ->
//		def bulletTemplate  = utils.custom.templateProvider.getTemplate("dassault.entities.blasterbullet")
//		def bulletId = "bullet_${utils.random.nextInt()}"
//		
//		println bulletId 
//		
//		def bullet = bulletTemplate.instantiate(bulletId, [position:utils.vector(20,20), moveDirection:utils.vector(1,0), speed:0.3f])
//		utils.custom.messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity))
//	})
	
	component(new ExplosionComponent("explosions")) { }
	
	input("inputmapping"){
		keyboard {
//			press(button:"space",eventId:"shootBullet")
		}
		mouse {
			
		}
	}
	
	
	
}
