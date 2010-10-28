package zombierockers.scenes;


import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.utils.EntityDumper 
import gemserk.utils.GroovyBootstrapper 
import net.sf.json.JSONArray 

builder.entity("game") { 
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
	property("currentLevelIndex", parameters.levelIndex ?: 0)
	
	def levels = ScenesDefinitions.scenes(utils)
	
	def currentLevel = levels[entity.currentLevelIndex]
	
	//	def backgroundMusic = utils.slick.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	property("transitions",[
			gameover:"gameover",
			paused:"paused",
			resume:"playing",
			])
	
	property("stateEntities",[
			entity("playing"){
				parent("zombierockers.scenes.playing", [level:currentLevel])
			},
			entity("paused"){ parent("zombierockers.scenes.paused") },entity("gameover"){  parent("zombierockers.scenes.gameover") }
			])
	
	property("currentNodeState", null)
	
	component(utils.components.genericComponent(id:"transitionHandler", messageId:["gameover","paused","resume"]){ message ->
		
		String messageId = message.getId();
		String transition = entity.transitions.get(messageId);
		
		def newEntity = entity.stateEntities.find{it.id == transition}
		
		messageQueue.enqueueDelay(new Message("leaveNodeState", new PropertiesMapBuilder().property("message", message).build()));
		messageQueue.enqueueDelay(utils.messages.genericMessage("changeNodeState"){ newMessage -> newMessage.state = newEntity})
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
		messageQueue.enqueueDelay(utils.messages.genericMessage("resume"){})
	})
	
	component(utils.components.genericComponent(id:"makeScreenshotHandler", messageId:"makeScreenshot"){ message ->
		
		def screenshotGrabber = utils.custom.screenshotGrabber
		screenshotGrabber.saveScreenshot("zombierockers-", "png")
		
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"x",eventId:"dumpEntities")
			press(button:"n",eventId:"nextLevel")
			press(button:"k",eventId:"makeScreenshot")
		}
	}
	component(utils.components.genericComponent(id:"dumpEntitiesHandler", messageId:"dumpEntities"){ message ->
		println JSONArray.fromObject(new EntityDumper().dumpEntity(entity.root)).toString(4)
	} )
	
	property("sceneTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("zombierockers.scenes.scene"), 
			utils.custom.genericprovider.provide{ data ->[levelIndex:data.levelIndex]}))
	
	
	component(utils.components.genericComponent(id:"nextLevelHandler", messageId:"nextLevel"){ message ->
		def	levelIndex = (entity.currentLevelIndex + 1) % levels.size
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.messages.genericMessage("resume"){})
	})
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		messageQueue.enqueueDelay(utils.messages.genericMessage("resume"){})
	})
	
	property("sceneEditorTemplate",utils.custom.templateProvider.getTemplate("zombierockers.scenes.sceneEditor"))
	
	component(utils.components.genericComponent(id:"goToEditorHandler", messageId:"goToEditor"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneEditorTemplate.instantiate(entity.id,[levelIndex:levelIndex, level:levels[(levelIndex)]])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
}