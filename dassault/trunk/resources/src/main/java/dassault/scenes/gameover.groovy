package dassault.scenes;

import com.gemserk.commons.animation.PropertyAnimation 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 

builder.entity("paused") {
	
	def font = utils.resources.fonts.font([italic:false, bold:false, size:28])
	
	def labelRectangle = utils.rectangle(-220,-50,440,100)
	
	def imagerRenderableObject = { layer, image, x, y, size, color -> 
		return new ClosureRenderObject(layer-1, { Graphics g ->
			g.pushTransform()
			g.translate(x, y)
			g.scale(size, size)
			g.drawImage(image, (float)-(image.getWidth() / 2), (float)-(image.getHeight() / 2), color)
			g.popTransform()
		})
	}
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", {utils.custom.gameStateManager.gameProperties.screenshot})
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", 900)
	}
	
	component(new RectangleRendererComponent("background")) {
		property("position",utils.vector(0,0))
		property("rectangle", utils.rectangle(0,0, 800, 600))
		property("lineColor", utils.color(0.2f,0.2f,0.2f,0.0f))
		property("fillColor", utils.color(0.5f,0.5f,0.5f,0.5f))
		property("layer",1000)
	}
	
	property("gameOverImage", utils.resources.image("gameover"))
	property("size", 0.5f)
	
	def sizeAnimation = new PropertyAnimation("size");
	
	sizeAnimation.addKeyFrame 0, 0.5f
	sizeAnimation.addKeyFrame 500, 2.0f
	
	property("animation", sizeAnimation)
	
	component(utils.components.genericComponent(id:"updateAnimationsHandler", messageId:"update"){ message ->
		def animation = entity.animation
		if (animation.paused)
			animation.play()
		animation.animate(entity, message.delta)
	})
	
	component(utils.components.genericComponent(id:"gameOverLabelRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		def position = utils.vector(400.0f, 300.0f);
		def size = entity.size
		
		renderer.enqueue(imagerRenderableObject(1001, entity.gameOverImage, position.x, //
				position.y, size, Color.white))
		
	})
	
	child(entity("restartLabel"){
		
		parent("gemserk.gui.label", [
		font:font,
		position:utils.vector(400f, 420f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:labelRectangle,
		align:"center",
		valign:"center",
		layer:1010
		])
		
		property("message", "Press \"r\" to restart")
	})
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"restart"){ message ->
		messageQueue.enqueue(utils.genericMessage("restartLevel"){})
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"restart")
			press(button:"r",eventId:"restart")
			press(button:"space",eventId:"restart")
			press(button:"p",eventId:"restart")
			press(button:"escape",eventId:"restart")
		}
		mouse {
			press(button:"left", eventId:"restart")
			press(button:"right", eventId:"restart")
		}
	}
	
}
