package floatingislands.scenes


import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.SlickRenderMessage;
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
	
	property("scroll", utils.vector(0.0f, 0.0f))
	property("scale", utils.vector(1.0f, 1.0f))
	
	component(utils.components.genericComponent(id:"scrollHandler", messageId:["scroll.up", "scroll.down", "scroll.left", "scroll.right"]){ message ->
		if (message.id == "scroll.up") {
			entity.scale.x += 0.01f
			entity.scale.y += 0.01f
						entity.scroll.y -= 1f
		}
		
		if (message.id == "scroll.down") {
			entity.scale.x -= 0.01f
			entity.scale.y -= 0.01f
						entity.scroll.y += 1f
		}
		
		if (message.id == "scroll.left")
			entity.scroll.x -= 1f
		
		if (message.id == "scroll.right")
			entity.scroll.x += 1f
	})
	
	component(new ComponentFromListOfClosures("scrollComponent", [{SlickRenderMessage message ->
		Graphics g = message.graphics
		// g.scale entity.scale.x, entity.scale.y
		g.translate entity.scroll.x, entity.scroll.y
	}]))
	
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
		
		property("speed", (float)(640.0f / 10000f))
		property("positions", [utils.vector(320, 240), utils.vector((float)(320 - 640), 240)])
		
		property("image", utils.resources.image("clouds"))
		
		component(new ComponentFromListOfClosures("scrollComponent", [{SlickRenderMessage message ->
			
			def image = entity.image
			
			SlickCallable.enterSafeBlock();
			
			image.bind()
			
			GL11.glPushMatrix();
			
			entity.positions.each { position ->
				GL11.glLoadIdentity();
				GL11.glTranslatef(position.x, position.y, 0.0f)
				GL11.glBegin(GL11.GL_QUADS) 
				
				GL11.glTexCoord2f 0f, 0f
				GL11.glVertex2f((float)(-image.width/2f), (float)(-image.height/2f))
				
				GL11.glTexCoord2f image.texture.width, 0f
				GL11.glVertex2f((float)(image.width/2f), (float)(-image.height/2f))
				
				GL11.glTexCoord2f image.texture.width, image.texture.height 
				GL11.glVertex2f((float)(image.width/2), (float)(image.height/2))
				
				GL11.glTexCoord2f 0f, image.texture.height 
				GL11.glVertex2f((float)(-image.width/2), (float)(image.height/2))
				GL11.glEnd()
			}
			
			GL11.glPopMatrix();
			
			SlickCallable.leaveSafeBlock();
			
		}]))
		
		component(new ComponentFromListOfClosures("cloudsMovement", [{ UpdateMessage m ->
			
			entity.positions.each { position ->
				position.x += (float) (entity.speed * m.getDelta())
				if (position.x > 640+320)
					position.x -= 1280f
			}
			
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
	
	component(utils.components.genericComponent(id:"mouseMovedHandler", messageId:"mouseMoved"){ message ->
		def x = (float)( message.x - entity.scroll.x)
		def y = (float)(message.y - entity.scroll.y)
		
		utils.custom.messageQueue.enqueue(utils.genericMessage("jumpDirectionChanged") { newMessage ->
			newMessage.x = x
			newMessage.y = y
		})
	})
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape", eventId:"pauseGame")
			hold(button:"up", eventId:"scroll.up")
			hold(button:"down", eventId:"scroll.down")
			hold(button:"left", eventId:"scroll.left")
			hold(button:"right", eventId:"scroll.right")
		}
		mouse {
			press(button:"left", eventId:"charge")
			release(button:"left", eventId:"jump")
			move(eventId:"mouseMoved") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
}
