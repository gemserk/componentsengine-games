package dudethatsmybullet.scenes;


builder.entity("world") {
	
	child(entity("hero"){ parent("dudethatsmybullet.entities.ship",[
		position:utils.vector(400,300),
		bounds:utils.rectangle(0,0,800,600)
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
