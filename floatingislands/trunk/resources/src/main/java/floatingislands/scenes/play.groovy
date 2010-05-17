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

		def vmin = utils.vector(10000f,10000f)
		def vmax = utils.vector(-10000f, -10000f)
		
		parameters.scene.islands.each { island -> 
			
			def position = island.position
		
			vmin.x = Math.min(vmin.x, position.x)
			vmin.y = Math.min(vmin.y, position.y)
			
			vmax.x = Math.max(vmax.x, position.x)
			vmax.y = Math.max(vmax.y, position.y)

		}
		
		vmin.x -= 500f
		vmin.y -= 400f
		
		property("scrollLimits", utils.rectangle(vmin.x, vmin.y, (float)(vmax.x-vmin.x), (float)(vmax.y - vmin.y)))
		
		println entity.scrollLimits.minX
		println entity.scrollLimits.maxX
		
		property("dragging", false)
		property("lastMousePosition", utils.vector(0,0))
		
		component(utils.components.genericComponent(id:"mouseRightPressedHandler", messageId:"mouseRightPressed"){ message ->
			entity.dragging = true
		})
		
		component(utils.components.genericComponent(id:"mouseRightReleasedHandler", messageId:"mouseRightReleased"){ message ->
			entity.dragging = false
		})
		
		component(utils.components.genericComponent(id:"dragScrollWhenMouseMoveHandler", messageId:"mouseMoved"){ message ->
			def mouseposition = utils.vector(message.x, message.y)
			
			if (!entity.dragging) {
				entity.lastMousePosition = mouseposition
				return
			}
			
			def scroll = entity.scroll
			
			scroll.add(mouseposition.copy().sub(entity.lastMousePosition))
			
			entity.lastMousePosition = mouseposition
			
			def scrollLimits = entity.scrollLimits
			
			if (scroll.x > -scrollLimits.minX)
				scroll.x = -scrollLimits.minX
			if (scroll.x < -scrollLimits.maxX)
				scroll.x = -scrollLimits.maxX
				
			if (scroll.y > -scrollLimits.minY)
				scroll.y = -scrollLimits.minY
			if (scroll.y < -scrollLimits.maxY)
				scroll.y = -scrollLimits.maxY
				
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
			mouse {
				press(button:"left", eventId:"mouseLeftPressed")
				release(button:"left", eventId:"mouseLeftReleased")
				
				press(button:"right", eventId:"mouseRightPressed")
				release(button:"right", eventId:"mouseRightReleased")
				
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
			
			entity.positions.each { position ->
				GL11.glPushMatrix();
				// GL11.glLoadIdentity();
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
				GL11.glPopMatrix();
			}
			
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
