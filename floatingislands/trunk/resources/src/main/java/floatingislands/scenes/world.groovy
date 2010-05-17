package floatingislands.scenes


import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.timers.CountDownTimer;

builder.entity {
	
	def scene = parameters.scene
	
	def startPosition = scene.startPosition
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	
	property("lifeImage", utils.resources.image("jumper"))
	
	property("player", parameters.player)	
	property("levelsCount", parameters.levelsCount)	
	
	property("currentIsland", null)
	property("lastIsland", null)
	property("islands", [])
	
	property("endSceneTimer", new CountDownTimer(1200))
	
	component(new TimerComponent("endSceneTimer")) {
		propertyRef("timer", "endSceneTimer")
		property("trigger", utils.custom.triggers.genericMessage("changeGameState") {
			// not used like the others ( message -> )
			
			if ((entity.player.currentLevel+1) == entity.levelsCount) 
				message.gameState = "gameFinished"
			else
				message.gameState = "sceneFinished"
			
		})
	}
	
	component(utils.components.genericComponent(id:"lastIslandReachedHandler", messageId:"lastIslandReached"){ message ->
		entity.endSceneTimer.reset()
	})
	
	def islands = entity.islands
	
	child(entity("flag") {
		parent("floatingislands.entities.flag", [island:{ islands[islands.size()-1] }])
	})
	
	def createIsland = { island, index ->
		def type = island.type
		def position = island.position
		
		def newIsland = entity("island-$index".toString()){
			
			parent(type, [
			position:position
			])
			
		}
		
		child(newIsland)
		
		entity.islands << newIsland
		entity.lastIsland = newIsland
	}
	
	createIsland.setResolveStrategy Closure.DELEGATE_FIRST
	
	scene.islands.eachWithIndex(createIsland)
	
	entity.currentIsland = entity.islands[0]
	
	child(entity("jumper"){
		
		parent("floatingislands.entities.jumper", [
		position:startPosition,
		minAngle:15f,
		maxAngle:165f
		])
		
	})
	
	component(utils.components.genericComponent(id:"jumpedHandler", messageId:"jumped"){ message ->
		entity.player.jumpCount++ 
	})
	
	component(utils.components.genericComponent(id:"islandReachedHandler", messageId:"islandReached"){ message ->
		entity.currentIsland = message.island
		
		println "currentIsland: $entity.currentIsland.id"
		
		if (entity.lastIsland == entity.currentIsland)
			utils.custom.messageQueue.enqueue(utils.genericMessage("lastIslandReached") { })
	})
	
	component(utils.components.genericComponent(id:"jumperOutsideScreenHandler", messageId:"jumperOutsideScreen"){ message ->
		
		def island = entity.currentIsland
		def newPosition = island.position.copy().sub(island.startPosition)
		
		def jumper = entity("jumper"){
			
			parent("floatingislands.entities.jumper", [
			position:newPosition,
			minAngle:15f,
			maxAngle:165f
			])
		}
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(jumper, "world"))
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		entity.parent.gamestate = "paused"
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"pauseGame")
		}
	}
	
}
