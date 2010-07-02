package dassault.entities

import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

import com.gemserk.commons.animation.PropertyAnimation;

builder.entity {
	
	def sizeAnimation = new PropertyAnimation("size");
	
	sizeAnimation.addKeyFrame 0, 1f
	sizeAnimation.addKeyFrame 200, 1.4f
	sizeAnimation.addKeyFrame 400, 1f
	
	def colorAnimation = new PropertyAnimation("color");
	
	colorAnimation.addKeyFrame 0, utils.color(0.9f, 0.9f, 0.9f, 1f)
	colorAnimation.addKeyFrame 200, utils.color(0.8f, 0.8f, 1f, 0.7f)
	colorAnimation.addKeyFrame 400, utils.color(0.9f, 0.9f, 0.9f, 1f)
	
	property("position", parameters.position)
	property("size", 1f)
	property("color", utils.color(1f,1f,1f,1f))
	property("shape", utils.rectangle(-50f, -10f, 100f, 20f))
	
	parent("dassault.entities.animation", [
			animations:[sizeAnimation, colorAnimation],
			target:entity
			])
			
	component(utils.components.genericComponent(id:"renderButtonHandler", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		def size = entity.size
		def layer = 10
		def color = entity.color
		def shape = entity.shape
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			g.translate((float) position.x, (float)position.y)
			g.scale(size, size)
			g.fill(shape)
			g.popTransform()
		}))
	})
	
	child(entity("$entity.id-areatrigger".toString()) { 
		
		parent("dassault.entities.areatrigger",[
		enterAreaTrigger:utils.custom.triggers.genericMessage("pointerEnterArea") { 
		},
		leaveAreaTrigger:utils.custom.triggers.genericMessage("pointerLeaveArea") {
		}
		])
		
		property("position", {entity.parent.position })
		property("area", {entity.parent.shape })
		
		component(utils.components.genericComponent(id:"pointerEnterAreaHandler", messageId:"pointerEnterArea"){ message ->
			if (!entity.id.equals(message.areaid)) 
				return
			log.info("pointer enter area $entity.id")
			utils.custom.messageQueue.enqueue(utils.genericMessage("restartAnimations"){ newMessage ->
				newMessage.animationId = entity.parent.id
			})
		})
		
		component(utils.components.genericComponent(id:"pointerLeaveAreaHandler", messageId:"pointerLeaveArea"){ message ->
			if (!entity.id.equals(message.areaid)) 
				return
			log.info("pointer leave area $entity.id")
		})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"space",eventId:"restartAnimations")
		}
		mouse {
			move(eventId:"movemouse") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
}
