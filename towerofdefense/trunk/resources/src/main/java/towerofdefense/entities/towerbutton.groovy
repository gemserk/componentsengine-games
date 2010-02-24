package towerofdefense.entities;

import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;

builder.entity {
	
	tags("button")
	
	property("position", parameters.position)
	property("direction", utils.vector(1f,0f))
	property("fillColor", utils.color(0.0f, 1.0f, 0.0f, 0.2f))
	property("mouseNotOverFillColor", utils.color(0.0f, 1.0f, 0.0f, 0.4f))
	property("mouseOverFillColor", utils.color(0.0f, 1.0f, 0.0f, 0.7f))
	property("bounding", utils.rectangle(-25, -25, 50, 50))
	
	property("enabled", true)
	property("disabledFillColor", utils.color(1.0f, 1.0f, 1.0f, 0.4f))
	
	property("mouseOver", false)
	
	property("messageBuilder", parameters.messageBuilder)
	
	genericComponent(id:"mouseOverHandler", messageId:"move"){ message ->
		def x = (float)(message.x - entity.position.x)
		def y = (float)(message.y - entity.position.y)
		
		if (entity.bounding.contains(x, y)) {
			entity.fillColor = entity.mouseOverFillColor
			entity.mouseOver = true
		}
		else {
			entity.fillColor = entity.mouseNotOverFillColor
			entity.mouseOver = false
		}
	}
	
	genericComponent(id:"mouseClickHandler", messageId:"click"){ message ->
		if (!entity.enabled)
			return
	
		if (! entity.mouseOver )
			return
		
		def clickedMessage = entity.messageBuilder.build([:])
		messageQueue.enqueue(clickedMessage)
	}
	
	component(new ComponentFromListOfClosures("background", [{ SlickRenderMessage m ->
		Graphics g = m.getGraphics()
		g.pushTransform();

		g.setColor(entity.enabled ? entity.fillColor : entity.disabledFillColor)
		def width = entity.bounding.width
		def height = entity.bounding.height
		g.translate((float)(entity.position.x -width/2), (float)(entity.position.y -height/2))
		g.fillRoundRect(0, 0, width, height, 5)
		
		g.popTransform();
	}]))
	
	component(new ImageRenderableComponent("towerRenderer")) {
		property("image", parameters.towerImage)
		property("color", utils.color(1f,1f,1f,1f))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
	
	component(new ImageRenderableComponent("cannonRenderer")) {
		property("image", parameters.cannonImage)
		property("color", utils.color(1f,1f,1f,1f))
		propertyRef("position", "position")
		propertyRef("direction", "direction")
	}
}
