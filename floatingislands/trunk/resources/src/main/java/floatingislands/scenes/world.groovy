package floatingislands.scenes


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.UpdateMessage 
import com.gemserk.componentsengine.timers.CountDownTimer;


builder.entity("world") {
	
	def scene = parameters.scene
	
	def startPosition = scene.startPosition
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	
	property("jumpCount", parameters.jumpCount)
	
	property("lifeImage", utils.resources.image("jumper"))
	
	property("currentIsland", null)
	property("lastIsland", null)
	property("islands", [])
	
	property("currentLevel", parameters.currentLevel)			
	property("levelsCount", parameters.levelsCount)	
	
	property("endSceneTimer", new CountDownTimer(1200))
	
	component(new TimerComponent("endSceneTimer")) {
		propertyRef("timer", "endSceneTimer")
		property("trigger", utils.custom.triggers.genericMessage("changeGameState") {
			// not used like the others ( message -> )
			
			if (entity.currentLevel == entity.levelsCount) 
				message.gameState = "gameFinished"
			else
				message.gameState = "sceneFinished"
			
		})
	}
	
	component(utils.components.genericComponent(id:"lastIslandReachedHandler", messageId:"lastIslandReached"){ message ->
		entity.endSceneTimer.reset()
	})
	
	component(new ImageRenderableComponent("backgroundRenderer")) {
		property("image", utils.resources.image("background02"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(320,240))
		property("direction", utils.vector(1,0))
	}
	
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
		position:startPosition
		])
		
	})
	
	child(entity("clouds"){
		
		property("windSound", {entity.parent.windSound})
		
		property("position1", utils.vector(320, 240))
		property("position2", utils.vector((float)(320 - 640), 240))
		
		component(new ImageRenderableComponent("cloudsRenderer1")) {
			propertyRef("position", "position1")
			property("image", utils.resources.image("clouds"))
			property("color", utils.color(1,1,1,1))
			property("direction", utils.vector(1,0))
		}
		
		component(new ImageRenderableComponent("cloudsRenderer2")) {
			propertyRef("position", "position2")
			property("image", utils.resources.image("clouds"))
			property("color", utils.color(1,1,1,1))
			property("direction", utils.vector(1,0))
		}
		
		component(new ComponentFromListOfClosures("cloudsMovement", [{ UpdateMessage m ->
			
			entity.position1.x += (float) (0.05f * m.getDelta())
			entity.position2.x = (float) (entity.position1.x - 640.0f)
			
			if (entity.position1.x > 640+320)
				entity.position1.x = (float)(320.0f)
			
			def windSound = entity.windSound
			if (!windSound.isPlaying())
				windSound.loop()
			
		}]))
		
	})
	
	component(utils.components.genericComponent(id:"jumpedHandler", messageId:"jumped"){ message -> entity.jumpCount++ })
	
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
			position:newPosition
			])
			
		}
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(jumper, "world"))
	})
	
	child(entity("levelCountLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(320, 40),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-320, -20, 320, 40),
		align:"center",
		valign:"top"
		])
		
		property("currentLevel", { entity.parent.currentLevel })			
		property("levelsCount", { entity.parent.levelsCount })
		
		property("message", {"Level $entity.currentLevel / $entity.levelsCount".toString()})
	})
	
	child(entity("jumpCountLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(600, 40),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-100, -20, 200, 40),
		align:"left",
		valign:"top"
		])
		
		property("jumpCount", { entity.parent.jumpCount })			
		
		property("message", {"Jumps: $entity.jumpCount".toString() })
	})
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		entity.parent.gamestate = "paused"
	})
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"pauseGame")
			press(button:"up", eventId:"jump")
		}
		mouse {
			press(button:"left", eventId:"charge")
			release(button:"left", eventId:"jump")
			move(eventId:"jumpDirectionChanged") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
}
