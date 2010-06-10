package zombierockers.entities



import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;


builder.entity("cannon") {
	Random random = new Random()
	
	def getRandomItem = {def items ->
		return  items[random.nextInt(items.size())]
	}
	
	tags("cannon","nofriction")
	
	property("yaxisConstraint", 570f)
	property("position", utils.vector(400f,570f))
	property("bulletPosition",{entity.position.copy().add(utils.vector(0f,-10f))})
	property("nextBulletPosition",{entity.position.copy().add(utils.vector(0f,30f))})
	property("direction",utils.vector(0,-1))
	
	property("fireTriggered",false)
	property("canFire",true)
	
	property("bounds",parameters.bounds)
	
	property("balls",[])
	property("currentBallIndex",0)
	property("currentBall",{entity.balls[(entity.currentBallIndex)]})
	property("nextBall",{entity.balls[((entity.currentBallIndex+1) % 2)]})
	
	property("levelBallTypes", parameters.levelBallTypes)
	
	property("fireRate", 300)
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ data ->
				[
				radius:0.1f,
				finalRadius:16.0f,
				color:data.color,
				state:"inWorld",
				fired:true
				]
			}))
	
	property("bulletTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.bullet"), 
			utils.custom.genericprovider.provide{ cannon ->
				[
				ball:cannon.ball,
				position:cannon.position.copy(),
				direction:cannon.direction.copy(),
				maxVelocity:0.7f,
				]
			}))
	
	component(new ImageRenderableComponent("currentBallRenderer")) {
		property("image", {entity.currentBall.animation.currentFrame})
		property("color",{entity.currentBall.color})
		propertyRef("position", "bulletPosition")
		property("direction", utils.vector(0,-1))
	}
	
	component(new ImageRenderableComponent("nextBallRenderer")) {
		property("image", {entity.nextBall.animation.currentFrame})
		property("color",{entity.nextBall.color})
		propertyRef("position", "nextBulletPosition")
		property("direction", utils.vector(0,-1))
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("ship"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	
	component(utils.components.genericComponent(id:"mousemovehandler", messageId:["movemouse"]){ message ->
		entity.position =  utils.vector(message.x,entity.yaxisConstraint)
	})
	
	component(utils.components.genericComponent(id:"leftmousehandler", messageId:["leftmouse"]){ message ->
		if (entity.canFire)
			entity.fireTriggered = true
	})
	
	component(utils.components.genericComponent(id:"rightmousehandler", messageId:["rightmouse"]){ message ->
		entity.currentBallIndex = (entity.currentBallIndex + 1) % 2
	})
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
	
	component(new WeaponComponent("shooter")) {
		propertyRef("reloadTime", "fireRate")
		propertyRef("position", "position")
		propertyRef("shouldFire", "fireTriggered")
		
		property("trigger", utils.custom.triggers.closureTrigger { cannon -> 
			entity.fireTriggered = false
			
			def bulletTemplate = entity.bulletTemplate
			def parameters = [position:cannon.bulletPosition.copy(),direction:cannon.direction.copy(),ball:entity.currentBall]
			def bullet = bulletTemplate.get(parameters)
			log.info("Fired bullet from Cannon - cannon.id: $entity.id - bullet.id: $bullet.id - bullet.ball.color:$bullet.ball.color" )
			messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
			messageQueue.enqueue(utils.genericMessage("generateBall"){})
		})
	}
	def generateBallHandlerMethod = {entity ->
		// maybe it should be obtained from a LevelProperties or something like that, shared with spawner, etc.
		def levelBallTypes = entity.levelBallTypes
		
		def availableBallTypes = []
		
		def limbosNotEmpty =  entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("limbo"), { limbo -> !limbo.isEmpty} as Predicate))
		
		if (limbosNotEmpty.size == 0) {
			// use only ball types of the balls left in the world
			log.info("Limbos are empty, generating balls using only balls left in the wolrd - cannon.id : $entity.id")
			def segments =  entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), { segment -> !segment.isEmpty} as Predicate))
			segments.each { segment ->
				segment.balls.each { ball ->
					if (!availableBallTypes.contains(ball.color))
						availableBallTypes << ball.color
				}
			}
		}
		
		if (availableBallTypes.isEmpty()) 
			availableBallTypes = levelBallTypes
		
		log.info("Colors available to generate - ballTypes: $availableBallTypes")
		
		def color = getRandomItem(availableBallTypes)
		def ball = entity.ballTemplate.get([color:color])
		entity.balls[(entity.currentBallIndex)]=ball
		entity.currentBallIndex = (entity.currentBallIndex + 1) % 2
	}
	component(utils.components.genericComponent(id:"generateBallHandler", messageId:["generateBall"]){ message -> generateBallHandlerMethod(entity) })
	
	generateBallHandlerMethod(entity) 
	generateBallHandlerMethod(entity) 
	
	component(utils.components.genericComponent(id:"baseReachedHandler", messageId:["baseReached"]){ message ->
		entity.canFire = false
	})
}
