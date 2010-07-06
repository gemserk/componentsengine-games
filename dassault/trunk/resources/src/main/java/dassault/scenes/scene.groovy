package dassault.scenes;


import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.Message 
import com.gemserk.componentsengine.properties.PropertiesMapBuilder 
import gemserk.utils.GroovyBootstrapper 

builder.entity("game") {
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	
	property("transitions",[
			paused:"paused",
			resume:"playing",
			])
	
	property("stateEntities",[
			entity("playing"){ parent("dassault.scenes.playing") },
			entity("paused"){ parent("dassault.scenes.paused") }])
	
	property("currentNodeState", null)
	
	component(utils.components.genericComponent(id:"transitionHandler", messageId:["paused","resume"]){ message ->
		
		String messageId = message.getId();
		String transition = entity.transitions.get(messageId);
		
		def newEntity = entity.stateEntities.find{it.id == transition }
		
		messageQueue.enqueueDelay(new Message("leaveNodeState", new PropertiesMapBuilder().property("message", message).build()));
		messageQueue.enqueueDelay(utils.genericMessage("changeNodeState"){ newMessage -> newMessage.state = newEntity })
		messageQueue.enqueueDelay(new Message("enterNodeState", new PropertiesMapBuilder().property("message", message).build()));
	})
	
	component(utils.components.genericComponent(id:"changeNodeStateHandler", messageId:"changeNodeState"){ message ->
		def newEntity = message.state
		
		if (entity.currentNodeState == newEntity)
			return
		
		if (entity.currentNodeState != null)
			messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entity.currentNodeState));
		
		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(newEntity,entity));
		entity.currentNodeState = newEntity
	})
	
	component(utils.components.genericComponent(id:"enterStateHandler", messageId:"enterState"){ message ->
		messageQueue.enqueueDelay(utils.genericMessage("resume"){
		})
	})
	
	property("gameTemplate", utils.custom.templateProvider.getTemplate("dassault.scenes.scene"))
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def scene = entity.gameTemplate.instantiate("playing")
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.genericMessage("resume"){
		})
	})
	
}
