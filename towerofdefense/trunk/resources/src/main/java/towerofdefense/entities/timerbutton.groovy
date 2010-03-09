package towerofdefense.entities;

import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.games.towerofdefense.components.render.RectangleRendererComponent;
import org.newdawn.slick.Graphics 

builder.entity {
	parent("towerofdefense.entities.button",parameters)
	property("timeLeft",parameters.timeLeft)
	
	component(new ComponentFromListOfClosures("background",[ {SlickRenderMessage message -> 
		Graphics g = message.graphics
		
		def rectangle = entity.bounding
		def position = entity.position
		def width = rectangle.width
		def height = rectangle.height
		
		def backupColor = g.color
		
		def worldClip = g.worldClip
		g.setWorldClip((float)(position.x + rectangle.x),(float)(position.y+rectangle.y),width,height)
		g.pushTransform()
		
		//VA A LA CLOSURE TIMELEFT(entity.wavesTimer.timeLeft/parameters.wavePeriod)
		g.translate(position.x,position.y)
		def timeLeftPercent = entity.timeLeft
		def angleLeft =360-  360*timeLeftPercent
		
		//g.fillArc(100,100, 100,100,-90,(float)( 90+(float)angleLeft))
		g.color = entity.enabled ? entity.fillColor : entity.disabledFillColor
		g.fillArc((float)-width,(float)-height,(float)2*width, (float)2*height,-90,(float)( -90+(float)angleLeft))
		g.popTransform()
		g.setWorldClip(worldClip)
		g.color = backupColor
	}
	]))
}
