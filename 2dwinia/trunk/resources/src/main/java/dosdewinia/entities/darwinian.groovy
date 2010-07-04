package dosdewinia.entities

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.SuperMovementComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

builder.entity("darwinian-${Math.random()}") {
	
	
	tags("darwinian","nofriction")
	
	property("position", parameters.position)
	property("nextPosition",parameters.position)
	propertyRef("direction", "movement.velocity")
	property("terrainMap",utils.resources.image("terrainMap"))
	
	property("speed", parameters.speed)
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("darwinian"))
		property("color", utils.color(1,1,1,1))
		propertyRef("position", "position")
		property("direction", utils.vector(1,0))
		property("size",utils.vector(0.5f,0.5f))
		property("layer", 1)
	}
	
	component(new SuperMovementComponent("movement")){
		property("velocity",utils.vector(entity.speed,0))
		propertyRef("maxVelocity", "speed")
		propertyRef("position", "nextPosition")
	}
	
	component(new WorldBoundsComponent("bounds")){
		property("bounds",utils.rectangle(0,0,800,600))
		propertyRef("position","position")
	}
	
	
	component(utils.components.genericComponent(id:"steeringbehaviour", messageId:"update"){ message ->
		
		def currentDirection = entity.direction.copy()
		//		def estimatedNextPosition = entity.position.copy().add(currentDirection.copy().scale(message.delta))
		//		log.info(estimatedNextPosition.toString())
		//		def terrainColor = entity.terrainMap.getColor((int)estimatedNextPosition.x, (int)estimatedNextPosition.y)
		//		log.info(terrainColor.toString())
		//		if( terrainColor== utils.color(0,0,0,1))
		//			currentDirection.negate()
		
		//log.info(currentDirection.toString())
		def newForce = currentDirection.add((float)(Math.random()-0.5f)*20)
		//log.info(newForce.toString())
		entity."movement.force".add(newForce)
	})
	
	component(utils.components.genericComponent(id:"islandboundchecker", messageId:"update"){ message ->
		def nextPosition = entity.nextPosition
		def terrainColor = entity.terrainMap.getColor((int)nextPosition.x, (int)nextPosition.y)
		if( terrainColor== utils.color(1,1,1,1)){
			entity.position = nextPosition
		}
		else{
			entity.nextPosition = entity.position
			entity."movement.velocity".negateLocal()			
		}
		
	})
	
//	component(utils.components.genericComponent(id:"debug", messageId:["render"]){ message ->
//			
//		def renderer = message.renderer
//		def start = entity.position
//		def end = entity.position.copy().add(entity.direction.copy().normalise().scale(50))
//		renderer.enqueue( new ClosureRenderObject(5, { Graphics g ->
//			g.drawLine( start.x, start.y, end.x, end.y)
//		}))
//	})
	
}