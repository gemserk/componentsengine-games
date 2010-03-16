package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.commons.components.DisablerComponent 
import com.gemserk.componentsengine.commons.components.GenericHitComponent 
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.predicates.EntityPredicates 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.geom.Line 
import org.newdawn.slick.geom.Vector2f 


builder.entity() {
	
	tags("bullet")
	
	property("position",{entity.parent.position})
	property("direction",{entity.parent.direction})
	property("damage", {entity.parent.damage})
	
	property("enabled",false)
	
	property("line",{
		def entity = entity
		Vector2f position = entity.position
		Vector2f direction = entity.direction
		
		
		Vector2f start = position.copy().add(direction.normalise().scale(20))
		Vector2f segment = direction.copy().scale(1000)
		
		Line line = new Line(start.x,start.y,segment.x,segment.y,true)
	})
	
	Random random = new Random();
	
	def laserrenderer = new ComponentFromListOfClosures("laserrenderer",[
	     {SlickRenderMessage message -> 
			Line line = entity.line
			Graphics g = message.graphics;
			
			def backupColor = g.getColor()
			
			def randomColor = (float)((random.nextFloat() * 0.5f) + 0.3f)
			
			g.setColor(utils.color(randomColor,randomColor,1f,1))
			g.draw(line)
			g.setColor(backupColor)
		}])
	
	
	component(new DisablerComponent(laserrenderer)){
		propertyRef("enabled","enabled")
	}
	
	
	component(new DisablerComponent(new GenericHitComponent("hitcomponent"))){
		property("targetTag", "critter")
		property("predicate",{EntityPredicates.isNear(entity.line,5f)})
		property("trigger", utils.custom.triggers.genericMessage("hit") { 
			def source = message.source
			def damagePerTime = source.damage
			message.damage = (Float)(damagePerTime*message.delta);
		})
		propertyRef("enabled","enabled")
	}
	
	
	
}