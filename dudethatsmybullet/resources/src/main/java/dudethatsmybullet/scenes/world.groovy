package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 


builder.entity("world") {
	
	property("bounds",utils.rectangle(0,0,800,600))
	
	component(new OutOfBoundsRemover("outofboundsremover")) {//usa enqueueDelay
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "bounds");
	}
	
	
	
	def bulletTemplate = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dudethatsmybullet.entities.bullet"), 
			utils.custom.genericprovider.provide{ data ->
				[
				position:data.position.copy(),
				direction:data.direction.copy(),
				image:utils.resources.image("bullet"),
				radius:10.0f,
				maxVelocity:0.4f,
				color:utils.color(1.0f, 0.2f, 0.2f, 1.0f)
				]
			})
	
	
	
	
	def hero = entity("hero"){ parent("dudethatsmybullet.entities.ship",[
		position:utils.vector(400,300),
		bounds:utils.rectangle(0,0,800,600)
		])
	}
	
	child(hero)
	
	child(entity("turret1"){ parent("dudethatsmybullet.entities.turret",[
		position:utils.vector(200,300),
		bulletTemplate: bulletTemplate,
		target: hero,
		])
	})
	
	child(entity("turret2"){ parent("dudethatsmybullet.entities.turret",[
		position:utils.vector(600,300),
		bulletTemplate: bulletTemplate,
		target: hero,
		])
	})
	
	
	
	component(utils.components.genericComponent(id:"moveShipHandler", messageId:["move.left","move.right","move.up","move.down",]){ message ->
		
		def command = message.id
		def moveDirection
		switch(command){
			case "move.left":
				moveDirection = utils.vector(-1,0)
				break;
			case "move.right":
				moveDirection = utils.vector(1,0)
				break;
			case "move.up":
				moveDirection = utils.vector(0,-1)
				break;
			case "move.down":
				moveDirection = utils.vector(0,1)
				break;
			default:
				moveDirection = utils.vector(0,0)
		}
		
		messageQueue.enqueue(utils.genericMessage("move") { newMessage  ->
			newMessage.target = moveDirection.copy()
		})
	})
}
