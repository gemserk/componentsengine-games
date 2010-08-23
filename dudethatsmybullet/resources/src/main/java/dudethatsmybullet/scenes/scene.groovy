package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.Message 
import com.gemserk.componentsengine.properties.PropertiesMapBuilder 
import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	//	child(entity("playing"){ parent("dudethatsmybullet.scenes.playing",parameters) })
	
	
	property("gameState", "playing");
	
	
	property("transitions",[
			gameover:"gameover",
			paused:"paused",
			resume:"playing",
			editor:"editor"
			])
	
	property("stateEntities",[
			entity("playing"){
				parent("dudethatsmybullet.scenes.playing", [:])
			},
			entity("paused"){ parent("dudethatsmybullet.scenes.paused") },
			entity("gameover"){  parent("dudethatsmybullet.scenes.gameover") },
			])
	
	property("currentNodeState", null)
	
	component(utils.components.genericComponent(id:"transitionHandler", messageId:["gameover","paused","resume"]){ message ->
		
		String messageId = message.getId();
		String transition = entity.transitions.get(messageId);
		
		def newEntity = entity.stateEntities.find{it.id == transition}
		
		messageQueue.enqueueDelay(new Message("leaveNodeState", new PropertiesMapBuilder().property("message", message).build()));
		messageQueue.enqueueDelay(utils.genericMessage("changeNodeState"){ newMessage -> newMessage.state = newEntity})
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
		messageQueue.enqueueDelay(utils.genericMessage("resume"){})
	})
	
	property("sceneTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dudethatsmybullet.scenes.scene"),
			utils.custom.genericprovider.provide{ data ->[:]}))
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([:])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.genericMessage("resume"){})
	})
}
