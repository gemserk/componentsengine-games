package dassault.entities

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.render.ClosureRenderObject 

import org.newdawn.slick.Graphics 

builder.entity {
	
	tags("droid", "nofriction")
	
	property("position", parameters.position)
	property("direction",utils.vector(1,0))
	property("size", 1.0f)
	
	// render type
	
	// weapon type
	
	component(utils.components.genericComponent(id:"droidRenderer", messageId:"render"){ message ->
		
		def renderer = message.renderer
		
		def position = entity.position
		def size = entity.size
		def layer = 0
		def color = utils.color(1f,1f,1f,1f)
		def shape = utils.rectangle(-5, -5, 10, 10)
		
		renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
			g.setColor(color)
			g.pushTransform()
			g.translate((float) position.x, (float)position.y)
			g.scale(size, size)
			g.fill(shape)
			g.popTransform()
		}))
	})
	
	component(new SuperMovementComponent("movementComponent")) {
		propertyRef("position", "position")
		property("maxVelocity", 0.1f)
	}
	
	component(utils.components.genericComponent(id:"updateMoveDirection", messageId:"update"){ message ->
		
		Input input = utils.custom.gameContainer.input
		
		def moveDirection = utils.vector(0,0)
		
		if (input.isKeyDown(Input.KEY_LEFT)) {
			moveDirection.x = -1
		}
		
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			moveDirection.x = 1
		}
		
		if (input.isKeyDown(Input.KEY_UP)) {
			moveDirection.y = -1
		}
		
		if (input.isKeyDown(Input.KEY_DOWN)) {
			moveDirection.y = 1
		}
	
		if (moveDirection.lengthSquared() > 0f) {
			def desiredDirection = moveDirection.normalise().scale(0.01f)
			entity."movementComponent.force".add(desiredDirection)
		} else {
			entity."movementComponent.force".add(entity."movementComponent.velocity".copy().negate().scale(0.01f))
		}
		
	})
	
}
