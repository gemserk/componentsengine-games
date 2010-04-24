package floatingislands.scenes
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 


import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.ProcessingDisablerComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.messages.UpdateMessage 
import floatingislands.GroovyBootstrapper 

builder.entity("game") {
	
	new GroovyBootstrapper();
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	
	def scene = [startPosition:utils.vector(100, 50), islands:[
			[type:"floatingislands.entities.island01", position:utils.vector(100,150)],
			[type:"floatingislands.entities.island02", position:utils.vector(230,300)],
			[type:"floatingislands.entities.island01", position:utils.vector(350,230)],
			[type:"floatingislands.entities.island01", position:utils.vector(550,280)]
			]]
	
	property("gamestate", "playing")
	
	child(entity("world") {
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "playing"})
			property("exclusions", [SlickRenderMessage.class])
		}
		
		def startPosition = scene.startPosition
		
		property("jumpCount", 0)
		property("lives", 1)
		
		property("lastIsland", null)
		
		component(new ImageRenderableComponent("backgroundRenderer")) {
			property("image", utils.resources.image("background02"))
			property("color", utils.color(1,1,1,1))
			property("position", utils.vector(320,240))
			property("direction", utils.vector(1,0))
		}
		
		def createIsland = { island, index ->
			def type = island.type
			def position = island.position
			
			child(entity("island-$index".toString()){
				
				parent(type, [
				position:position
				])
				
			})
		}
		
		createIsland.setResolveStrategy Closure.DELEGATE_FIRST
		
		scene.islands.eachWithIndex(createIsland)
		
		child(entity("jumper"){
			
			parent("floatingislands.entities.jumper", [
			position:startPosition
			])
			
		})
		
		child(entity("clouds"){
			
			property("windSound", utils.resources.sounds.sound("wind"))
			
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
					windSound.play()
				
			}]))
			
		})
		
		component(utils.components.genericComponent(id:"jumpedHandler", messageId:"jumped"){ message -> entity.jumpCount++ })
		
		component(utils.components.genericComponent(id:"islandReachedHandler", messageId:"islandReached"){ message ->
			entity.lastIsland = message.island
		})
		
		component(utils.components.genericComponent(id:"jumperOutsideScreenHandler", messageId:"jumperOutsideScreen"){ message ->
			// utils.custom.game.loadScene("floatingislands.scenes.game");
			entity.lives -= 1
		
			if (entity.lives == 0)
			{
				entity.parent.gamestate = "gameover"
				return
			}
			
			
			def island = entity.lastIsland
			def newPosition = island.position.copy().sub(island.startPosition)
			
			def jumper = entity("jumper"){
				
				parent("floatingislands.entities.jumper", [
				position:newPosition
				])
				
			}
			
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(jumper, "world"))
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
		
	})
	
	child(entity("gameover") {
		
		def font2 = utils.resources.fonts.font([italic:false, bold:false, size:36])
		
		component(new ProcessingDisablerComponent("disabler")) {
			property("enabled", {entity.parent.gamestate == "gameover"})
		}
		
		component(new RectangleRendererComponent("rectangle")) {
			property("position", utils.vector(0,0))
			property("rectangle", utils.rectangle(40,40, 560, 400))
			property("lineColor", utils.color(0,0,0,0))
			property("fillColor", utils.color(0.5f,0.5f,1f,0.4f))
		}
		
		child(entity("gameOverLabel"){
			
			parent("gemserk.gui.label", [
			font:font2,
			position:utils.vector(320, 240),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-100, -20, 200, 40),
			align:"center",
			valign:"center"
			])
			
			property("message", "Game Over")
		})
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"up", eventId:"jump")
		}
		mouse {
			press(button:"left", eventId:"startJump")
			release(button:"left", eventId:"jump")
			move(eventId:"jumpDirectionChanged") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
}
