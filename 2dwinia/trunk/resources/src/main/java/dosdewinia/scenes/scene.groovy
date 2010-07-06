package dosdewinia.scenes;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.Message 
import com.gemserk.componentsengine.properties.PropertiesMapBuilder 
import com.gemserk.componentsengine.utils.EntityDumper 
import gemserk.utils.GroovyBootstrapper 
import net.sf.json.JSONArray 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	Vector2f.metaClass.distanceSquared = { Vector2f otherVector ->
			return delegate.copy().sub(otherVector).lengthSquared()
	}
	
	
	
	property("gameState", "playing");
	
	
	property("transitions",[
			gameover:"gameover",
			paused:"paused",
			resume:"playing",
			])
	
	property("stateEntities",[
			entity("playing"){
				parent("dosdewinia.scenes.playing", [:])
			},
			entity("paused"){ parent("dosdewinia.scenes.paused") },entity("gameover"){  parent("dosdewinia.scenes.gameover") }
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
	
	input("inputmapping"){
		keyboard {
			press(button:"x",eventId:"dumpEntities")
		}
	}
	component(utils.components.genericComponent(id:"dumpEntitiesHandler", messageId:"dumpEntities"){ message ->
		println JSONArray.fromObject(new EntityDumper().dumpEntity(entity.root)).toString(4)
	} )
	
	property("sceneTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dosdewinia.scenes.scene"), 
			utils.custom.genericprovider.provide{ data ->[:]}))
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([:])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.genericMessage("resume"){})
	})
}
