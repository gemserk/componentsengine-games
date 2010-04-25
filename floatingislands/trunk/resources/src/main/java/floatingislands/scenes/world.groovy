package floatingislands.scenes

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.messages.UpdateMessage 


builder.entity("world") {
	
	def scene = parameters.scene
	
	def startPosition = scene.startPosition
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	
	property("jumpCount", parameters.jumpCount)
	property("lives", parameters.lives)
	
	property("lifeImage", utils.resources.image("jumper"))
	
	property("currentIsland", null)
	property("lastIsland", null)
	property("islands", [])
	
	component(new ImageRenderableComponent("backgroundRenderer")) {
		property("image", utils.resources.image("background02"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(320,240))
		property("direction", utils.vector(1,0))
	}
	
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
	println "current island: $entity.currentIsland.id"
	
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
		// utils.custom.game.loadScene("floatingislands.scenes.game");
		entity.lives -= 1
		
		if (entity.lives <= 0)
		{
			utils.custom.messageQueue.enqueue(utils.genericMessage("jumperDead") { })
			return
		}
		
		
		def island = entity.currentIsland
		def newPosition = island.position.copy().sub(island.startPosition)
		
		def jumper = entity("jumper"){
			
			parent("floatingislands.entities.jumper", [
			position:newPosition
			])
			
		}
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(jumper, "world"))
	})
	
	component(new ComponentFromListOfClosures("livesRenderer",[{ SlickRenderMessage m->
		
		def image = entity.lifeImage
		def lives = entity.lives
		
		lives.times {
			image.draw((float)(100 + it*20), 5, 30, 30)
		}
		
	}]))
	
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
	
}
