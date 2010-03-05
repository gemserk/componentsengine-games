package towerofdefense.entities;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.messages.SlickRenderMessage;

import com.gemserk.componentsengine.messages.UpdateMessage;





import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import org.newdawn.slick.geom.Line 
import org.newdawn.slick.geom.Vector2f 


builder.entity("missilebullet-${Math.random()}") {
	
	parent("towerofdefense.entities.bullet",parameters)
	tags("missile")
	
	property("targetEntity",parameters.targetEntity)
	
	component(new ComponentFromListOfClosures("steering",[ {UpdateMessage message ->
		def debugVectors = []
		
		                       
		Vector2f position = entity.position
		Vector2f velocity = entity.direction
		
		debugVectors << [vector:velocity.copy().normalise().scale(50f), color:Color.red]
		
		Vector2f targetPosition = entity.targetEntity.position
		Vector2f directionToTarget = targetPosition.copy().sub(position)
		debugVectors << [vector:directionToTarget, color:Color.yellow]
		
		def steerDirection = new Vector2f(-velocity.y, velocity.x).normalise()
		
		debugVectors << [vector:steerDirection.copy().scale(50f), color:Color.green]
		
		
		def steerForceLength = steerDirection.dot(directionToTarget)
		def steerForceSign = steerForceLength > 0 ? 1 : -1	
		steerForceLength = Math.abs(steerForceLength)
		def steerMaxForce = Math.min(0.0007f, steerForceLength)
		
		
		
		
		def steerForceResult = steerDirection.normalise().scale((float)(steerMaxForce*steerForceSign*message.delta))
		
		debugVectors << [vector:steerForceResult.copy().normalise().scale(50f), color:Color.blue]
		
		entity."movement.force".add(steerForceResult)
		
		//entity.debugVectors = debugVectors
		
	}, {SlickRenderMessage message ->
		def debugVectors = entity.debugVectors
		if( debugVectors == null)
			return
		
		
		Graphics g = message.graphics
		Vector2f start = entity.position.copy()
		
		debugVectors.each { vectorContainer ->
			
			def vector = vectorContainer.vector
			def color = vectorContainer.color ?: Color.white
			
			Vector2f segment = vector.copy()
			
			Line line = new Line(start.x,start.y,segment.x,segment.y,true)
			def origColor = g.getColor()
			g.setColor(color)
			g.draw(line)
			g.setColor(origColor)
		}
		
	}
	
	]))
	

//	component(new GenericHitComponent("bullethit")){
//		property("targetTag", "critter")
//		property("predicate",{EntityPredicates.isNear(entity.position, entity.radius)})
//		property("trigger", utils.custom.messageBuilderFactory.super() { data -> 
//			def source = data.source
//			def targets = data.targets
//			
//			def lista = []
//			             
//			targets.each { target ->
//				def distancia = ...addShutdownHook { }
//				lista << new GenericMessage("hit").with{
//					damage = entity.damage/distancia
//					target = target
//					source = source
//				}
//			}
//			
//		return lista
//		})
//	}
	
	
	
}
