package zombierockers.scenes;


import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
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
	
	def screenBounds = utils.slick.rectangle(0, 0, 800, 600);
	
	//	def backgroundMusic = utils.slick.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	parent("GameStateManager", [
			transitions:[
			gameover:"gameover",
			paused:"paused",
			resume:"playing",
			//	            			editor:"editor",
			],
			stateEntities:[
			playing:entity("playing"){ parent("zombierockers.scenes.playing", [level:currentLevel, screenBounds:screenBounds]) },
			paused:entity("paused"){ parent("zombierockers.scenes.paused", [screenBounds:screenBounds]) },
			gameover:entity("gameover"){  parent("zombierockers.scenes.gameover", [screenBounds:screenBounds]) },
			//	            			editor:entity("editor"){  parent("zombierockers.scenes.sceneEditor") },
			],
			])                          
	
	component(utils.components.genericComponent(id:"enterStateHandler", messageId:"enterState"){ message ->
		utils.messageQueue.enqueueDelay(utils.messages.genericMessage("resume"){})
	})
	
	component(utils.components.genericComponent(id:"makeScreenshotHandler", messageId:"makeScreenshot"){ message ->
		
		def screenshotGrabber = utils.screenshotGrabber
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
			utils.templateProvider.getTemplate("zombierockers.scenes.scene"), 
			utils.genericprovider.provide{ data ->[levelIndex:data.levelIndex]}))
	
	
	component(utils.components.genericComponent(id:"nextLevelHandler", messageId:"nextLevel"){ message ->
		def	levelIndex = (entity.currentLevelIndex + 1) % levels.size
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		utils.messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		utils.messageQueue.enqueueDelay(utils.messages.genericMessage("resume"){})
	})
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		utils.messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
		utils.messageQueue.enqueueDelay(utils.messages.genericMessage("resume"){})
	})
	
	property("sceneEditorTemplate",utils.templateProvider.getTemplate("zombierockers.scenes.sceneEditor"))
	
	component(utils.components.genericComponent(id:"goToEditorHandler", messageId:"goToEditor"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneEditorTemplate.instantiate(entity.id,[levelIndex:levelIndex, level:levels[(levelIndex)]])
		utils.messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
}