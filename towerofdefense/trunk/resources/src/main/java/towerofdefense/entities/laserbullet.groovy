package towerofdefense.entities;
import org.newdawn.slick.geom.Vector2f;


import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.games.towerofdefense.GenericHitComponent;


builder.entity("laserbullet-${Math.random()}") {
	
	tags("bullet")
	
	property("position",{entity.parent.position})
	property("direction",{entity.parent.direction})
	property("damage",(Float)3f/1000f)
	
	property("line",{
		Vector2f position = entity.position
		Vector2f direction = entity.direction
		
		
		Vector2f start = position.copy().add(direction.normalise().scale(20))
		Vector2f segment = direction.copy().scale(1000)
		
		Line line = new Line(start.x,start.y,segment.x,segment.y,true)
	})
	
	def laserrenderer = new ReflectionComponent("laserrenderer"){
		void handleMessage(SlickRenderMessage message){
			Line line = entity.line
			Graphics g = message.graphics;
			
			g.draw(line)
		}
	}
	
	component(laserrenderer){
	}
	
	
	component(new GenericHitComponent("hitcomponent")){
		property("targetTag", "critter")
		property("predicate",{EntityPredicates.isNear(entity.line,5f)})
		property("messageBuilder", utils.custom.messageBuilderFactory.messageBuilder("hit") { 
			def source = message.source
			def damagePerTime = source.damage
			message.damage = (Float)(damagePerTime*message.delta);
		})
	}
	
	
	
}