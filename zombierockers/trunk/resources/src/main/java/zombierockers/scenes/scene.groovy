package zombierockers.scenes;

import com.gemserk.componentsengine.commons.components.states.NodeStateTransitionManagerComponent;
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
	
	//	def backgroundMusic = utils.resources.sounds.sound("backgroundmusic")
	//	backgroundMusic.play();
	
	component(new NodeStateTransitionManagerComponent("stateChanger")){
		property("transitions",[
		gameover:["gameover"],
		paused:["paused"],
		resume:["playing"],
		enterscore:["enterscore"]
		])
	}
	
	child(entity("playing"){ 
		parent("gemserk.states.stateBasedNode",[enabled:true,exclusions:["render"]])
		parent("zombierockers.scenes.playing", [level:currentLevel]) 
	})
	
	child(entity("gameover"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("zombierockers.scenes.gameover")
	})
	
	child(entity("paused"){ 
		parent("gemserk.states.stateBasedNode",[enabled:false])
		parent("zombierockers.scenes.paused")
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"x",eventId:"dumpEntities")
			press(button:"n",eventId:"nextLevel")
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
	})
	
	component(utils.components.genericComponent(id:"restartLevelHandler", messageId:"restartLevel"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.get([levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
	
	property("sceneEditorTemplate",utils.custom.templateProvider.getTemplate("zombierockers.scenes.sceneEditor"))
	
	component(utils.components.genericComponent(id:"goToEditorHandler", messageId:"goToEditor"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneEditorTemplate.instantiate(entity.id,[levelIndex:levelIndex, level:levels[(levelIndex)]])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
}