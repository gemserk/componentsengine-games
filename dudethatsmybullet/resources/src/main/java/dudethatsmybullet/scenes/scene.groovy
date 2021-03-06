package dudethatsmybullet.scenes;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.Message 
import com.gemserk.componentsengine.properties.PropertiesMapBuilder 
import gemserk.utils.GroovyBootstrapper 

builder.entity("scene") {
	
	new GroovyBootstrapper();
	
	
	
	
	property("gameState", "playing");
	property("currentLevelIndex", parameters.levelIndex ?: 0)
	
	def levels = ScenesDefinitions.scenes(utils)
	
	def currentLevel = levels[entity.currentLevelIndex]
	
	property("transitions",[
			gameover:"gameover",
			paused:"paused",
			resume:"playing",
			helpscreen:"helpscreen",
			title:"title"
			])
	
	property("stateEntities",[
			entity("playing"){
				parent("dudethatsmybullet.scenes.playing", [level:currentLevel])
			},
			entity("paused"){ parent("dudethatsmybullet.scenes.paused") },
			entity("gameover"){  parent("dudethatsmybullet.scenes.gameover") },
			entity("helpscreen"){parent("dudethatsmybullet.scenes.helpscreen") },
			entity("title"){parent("dudethatsmybullet.scenes.title") },
			])
	
	property("currentNodeState", null)
	
	component(utils.components.genericComponent(id:"transitionHandler", messageId:["gameover","paused","resume","helpscreen","title"]){ message ->
		
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
		messageQueue.enqueueDelay(utils.genericMessage("title"){})
	})
	
	property("sceneTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dudethatsmybullet.scenes.scene"),
			utils.custom.genericprovider.provide{ data ->[levelIndex:data.levelIndex]}))
	
	component(utils.components.genericComponent(id:"nextLevelHandler", messageId:"nextLevel"){ message ->
		def	levelIndex = (entity.currentLevelIndex + 1) % levels.size
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.genericMessage("resume"){})
	})
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.genericMessage("resume"){})
	})
	
	
	input("inputmapping"){
		keyboard {
			press(button:"k",eventId:"makeScreenshot")
			press(button:"h",eventId:"helpscreen")
			press(button:"f",eventId:"toggleShowFps")
		}
	}
	component(utils.components.genericComponent(id:"makeScreenshotHandler", messageId:"makeScreenshot"){ message ->
		
		def screenshotGrabber = utils.custom.screenshotGrabber
		screenshotGrabber.saveScreenshot("dudethatsmybullet-", "png")
	})
	
	component(utils.components.genericComponent(id:"toggleShowFps", messageId:"toggleShowFps"){ message ->
		
		def gameContainer = utils.custom.gameContainer
		boolean isShowingFps = gameContainer.isShowingFPS()
		gameContainer.setShowFPS(!isShowingFps)
		
	})

}
