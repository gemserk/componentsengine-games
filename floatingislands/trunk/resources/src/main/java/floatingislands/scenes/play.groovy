package floatingislands.scenes

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.messages.UpdateMessage 
import org.lwjgl.opengl.GL11 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.opengl.SlickCallable 


builder.entity {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:24])
	
	child(entity("background") {
		
		component(new ImageRenderableComponent("backgroundRenderer")) {
			property("image", utils.resources.image("background02"))
			property("color", utils.color(1,1,1,1))
			property("position", utils.vector(320,240))
			property("direction", utils.vector(1,0))
		}
		
	})
	
	child(entity("foreground") {
		
		property("controllerEnabled", true)
		
		property("scroll", utils.vector(0.0f, 0.0f))
		
		component(utils.components.genericComponent(id:"scrollHandler", messageId:["scroll.up", "scroll.down", "scroll.left", "scroll.right"]){ message ->
			if (message.id == "scroll.up") 
				entity.scroll.y -= 1f
			
			if (message.id == "scroll.down") 
				entity.scroll.y += 1f
			
			if (message.id == "scroll.left")
				entity.scroll.x -= 1f
			
			if (message.id == "scroll.right")
				entity.scroll.x += 1f
		})
		
		component(utils.components.genericComponent(id:"mouseMovedHandler", messageId:"mouseMoved"){ message ->
			
			if (!entity.controllerEnabled)
				return
			
			def x = (float)( message.x - entity.scroll.x)
			def y = (float)(message.y - entity.scroll.y)
			
			utils.custom.messageQueue.enqueue(utils.genericMessage("jumpDirectionChanged") { newMessage ->
				newMessage.x = x
				newMessage.y = y
			})
		})
		
		component(utils.components.genericComponent(id:"mouseLeftPressedHandler", messageId:"mouseLeftPressed"){ message ->
			if (!entity.controllerEnabled)
				return
			utils.custom.messageQueue.enqueue(utils.genericMessage("charge") { })
		})
		
		component(utils.components.genericComponent(id:"mouseLeftReleasedHandler", messageId:"mouseLeftReleased"){ message ->
			if (!entity.controllerEnabled)
				return
			utils.custom.messageQueue.enqueue(utils.genericMessage("jump") { })
		})
		
		component(utils.components.genericComponent(id:"lastIslandReachedHandler", messageId:"lastIslandReached"){ message ->
			entity.controllerEnabled = false
		})
		
		child(entity("preTranslate") {
			
			component(new ComponentFromListOfClosures("scrollComponent", [{SlickRenderMessage message ->
				Graphics g = message.graphics
				g.pushTransform()
				g.translate entity.parent.scroll.x, entity.parent.scroll.y
			}]))
			
		})
		
		child(entity("world") { parent("floatingislands.scenes.world", parameters) })
		
		child(entity("postTranslate") {
			
			component(new ComponentFromListOfClosures("scrollComponent", [{SlickRenderMessage message ->
				Graphics g = message.graphics
				g.popTransform()
			}]))
			
		})
		
		input("inputmapping"){
			keyboard {
				hold(button:"up", eventId:"scroll.up")
				hold(button:"down", eventId:"scroll.down")
				hold(button:"left", eventId:"scroll.left")
				hold(button:"right", eventId:"scroll.right")
			}
			mouse {
				press(button:"left", eventId:"mouseLeftPressed")
				release(button:"left", eventId:"mouseLeftReleased")
				move(eventId:"mouseMoved") { message ->
					message.x = position.x
					message.y = position.y
				}
			}
		}
		
	})
	
	child(entity("clouds") {
		
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
			
			//				def windSound = entity.windSound
			//				if (!windSound.isPlaying())
			//					windSound.loop()
			
		}]))
		
	})
	
	child(entity("hud") {
		
		property("player", parameters.player)	
		property("levelsCount", parameters.levelsCount)	
		
		child(entity("levelCountLabel"){
			
			parent("gemserk.gui.label", [
			font:font,
			position:utils.vector(320, 40),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-320, -20, 320, 40),
			align:"center",
			valign:"top"
			])
			
			property("currentLevel", { entity.parent.player.currentLevel })			
			property("levelsCount", { entity.parent.levelsCount })
			
			property("message", {"Level ${entity.currentLevel+1} / $entity.levelsCount".toString()})
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
			
			property("jumpCount", { entity.parent.player.jumpCount })			
			
			property("message", {"Jumps: $entity.jumpCount".toString() })
		})
		
	})
	
}
