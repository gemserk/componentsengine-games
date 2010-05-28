package zombierockers.entities



import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.WeaponComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 


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
	
	
	property("bounds",parameters.bounds)
	
	property("balls",[])
	property("currentBallIndex",0)
	property("currentBall",{entity.balls[(entity.currentBallIndex)]})
	property("nextBall",{entity.balls[((entity.currentBallIndex+1) % 2)]})
	
	property("ballTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.entities.ball"), 
			utils.custom.genericprovider.provide{ data ->
				[
				radius:10.0f,
				color:data.color
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
	
	
	component(new CircleRenderableComponent("currentBallRenderer")) {
		propertyRef("position", "bulletPosition")
		property("radius", {entity.currentBall.radius})
		property("lineColor", utils.color(0,0,0,0))
		property("fillColor", {entity.currentBall.color})
	}
	
	component(new CircleRenderableComponent("nextBallRenderer")) {
		propertyRef("position", "nextBulletPosition")
		property("radius", {entity.nextBall.radius})
		property("lineColor", utils.color(0,0,0,0))
		property("fillColor", {entity.nextBall.color})
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
		entity.fireTriggered = true
	})
	
	component(utils.components.genericComponent(id:"rightmousehandler", messageId:["rightmouse"]){ message ->
		entity.currentBallIndex = (entity.currentBallIndex + 1) % 2
	})
	
	component(new WeaponComponent("shooter")) {
		property("reloadTime", 250)
		propertyRef("position", "position")
		propertyRef("shouldFire", "fireTriggered")
		
		property("trigger", utils.custom.triggers.closureTrigger { cannon -> 
			entity.fireTriggered = false
			
			def bulletTemplate = entity.bulletTemplate
			def parameters = [position:cannon.bulletPosition.copy(),direction:cannon.direction.copy(),ball:entity.currentBall]
			def bullet = bulletTemplate.get(parameters)
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entity.parent))
			messageQueue.enqueue(utils.genericMessage("generateBall"){})
		})
	}
	def generateBallHandlerMethod = {entity -> 
		def colors = [utils.color(1,0,0,1), utils.color(0,1,0,1), utils.color(0,0,1,1)]
		def color = getRandomItem(colors)
		def ball = entity.ballTemplate.get([color:color])
		entity.balls[(entity.currentBallIndex)]=ball
		entity.currentBallIndex = (entity.currentBallIndex + 1) % 2
	}
	component(utils.components.genericComponent(id:"generateBallHandler", messageId:["generateBall"]){ message -> generateBallHandlerMethod(entity) })
	
	generateBallHandlerMethod(entity) 
	generateBallHandlerMethod(entity) 
	
	//	property("notInitialized",true)
	//	
	//	component(new DisablerComponent(utils.custom.components.closureComponent("initHandler"){ UpdateMessage message ->
	//		entity.notInitialized = false;
	//		utils.custom.messageQueue.enqueue(utils.genericMessage("generateBall"){})
	//		utils.custom.messageQueue.enqueue(utils.genericMessage("generateBall"){})
	//		println "Initialized - $entity.id"
	//	})){ propertyRef("enabled","notInitialized") }
	//	
	
	
	component(new WorldBoundsComponent("bounds")){
		propertyRef("bounds","bounds")
		propertyRef("position","position")
	}
}
