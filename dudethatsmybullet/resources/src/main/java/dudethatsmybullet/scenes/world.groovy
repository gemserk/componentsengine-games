package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.commons.components.TimerComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import com.gemserk.componentsengine.timers.CountDownTimer 


builder.entity("world") {
	
	property("bounds",utils.rectangle(0,0,800,600))
	
	property("gameOver",false)
	
	property("level",parameters.level)
	
	component(new OutOfBoundsRemover("outofboundsremover")) {//usa enqueueDelay
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	
	
	def bulletTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dudethatsmybullet.entities.bullet"), 
			utils.custom.genericprovider.provide{ data ->
				[
				position:data.position.copy(),
				direction:data.direction.copy(),
				image:utils.resources.image("bullet"),
				radius:10.0f,
				maxVelocity:0.2f,
				color:utils.color(1.0f, 0.2f, 0.2f, 1.0f),
				damage: data.damage
				]
			})
	
	
	
	
	def hero = entity("hero"){ parent("dudethatsmybullet.entities.ship",[
		position:utils.vector(400,300),
		bounds:utils.rectangle(0,0,800,600),
		hitpoints: 250f,
		maxShield: 150f,
		shieldDischargeRate: (float)200f/1000f,
		shieldRechargeRate: (float)1000f/(80f*1000f),
		])
	}
	
	child(hero)
	
	def turretPositions = [
			utils.vector(200,300),
			utils.vector(600,300),		
			utils.vector(400,150),
			utils.vector(400,450),
		]
	
	parameters.level.turrets.eachWithIndex { turretDefinition, index ->
		child(entity("turret$index"){ parent("dudethatsmybullet.entities.turret",[
			position:turretDefinition.position,
			bulletTemplate: bulletTemplate,
			target: hero,
			damage: 25f,
			])
		})
	}
	
	
//	child(entity("turret1"){ parent("dudethatsmybullet.entities.turret",[
//		position:utils.vector(200,300),
//		bulletTemplate: bulletTemplate,
//		target: hero,
//		damage: 25f,
//		])
//	})
//	
//	child(entity("turret2"){ parent("dudethatsmybullet.entities.turret",[
//		position:utils.vector(600,300),
//		bulletTemplate: bulletTemplate,
//		target: hero,
//		damage: 25f,
//		])
//	})
	
	
	property("win",null)
	component(utils.components.genericComponent(id:"endConditionChecker", messageId:["update",]){ message ->
		if(entity.gameOver)
			return
			
		def resultMessage = ""			
		def heroEntity = entity.getEntityById("hero")
		if(!heroEntity || heroEntity.isDead){
			entity.gameOver = true
			entity.win = false
		}
		
		
		def turrets = entity.root.getEntities(EntityPredicates.withAllTags("turret"))
		if(!entity.gameOver && turrets.size == 1){
			entity.gameOver = true
			entity.win = true
		}
		
		if(entity.gameOver){
			entity.gameOverTimer.reset()
			messageQueue.enqueue(utils.genericMessage("playStopped"){})
		}
	})
	
	
	property("gameOverTimer",new CountDownTimer(1000))
	component(new TimerComponent("gameOverTimerComponent")){
		property("trigger",utils.custom.triggers.genericMessage("gameover") { message.win = entity.win	})
		propertyRef("timer","gameOverTimer")
	}
		
	
	
	component(utils.components.genericComponent(id:"moveShipHandler", messageId:["move.left","move.right","move.up","move.down",]){ message ->
		
		def command = message.id
		def moveDirection
		switch(command){
			case "move.left":
				moveDirection = utils.vector(-1,0)
				break;
			case "move.right":
				moveDirection = utils.vector(1,0)
				break;
			case "move.up":
				moveDirection = utils.vector(0,-1)
				break;
			case "move.down":
				moveDirection = utils.vector(0,1)
				break;
			default:
				moveDirection = utils.vector(0,0)
		}
		
		messageQueue.enqueue(utils.genericMessage("move") { newMessage  ->
			newMessage.target = moveDirection.copy()
		})
	})
	
	component(new ExplosionComponent("explosions")) { }
}
