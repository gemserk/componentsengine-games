package floatingislands.entities;

import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;


import com.gemserk.componentsengine.messages.SlickRenderMessage;

builder.entity {
	
	tags("flag")
	
	property("island", parameters.island)
	property("position", {
		def island = entity.island
		return utils.vector((float)island.position.x, (float)(island.position.y + island.bounds.minY - island.bounds.height))
	})
	property("direction", utils.vector(1,0))
	property("animation", utils.resources.animation("flag"))
	
	component(new ComponentFromListOfClosures("animationRenderer",[{SlickRenderMessage message ->
		Graphics g = message.getGraphics()
		
		def animation = entity.animation
		g.drawAnimation(animation, (float)(entity.position.x - animation.width/2f), (float)(entity.position.y - animation.height/2f))
	}]))
	
	
}
