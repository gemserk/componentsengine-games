package zombierockers.scenes;

import com.gemserk.componentsengine.commons.components.states.NodeStateTransitionManagerComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage 
import com.gemserk.componentsengine.utils.EntityDumper 
import gemserk.utils.GroovyBootstrapper 
import net.sf.json.JSONArray 

builder.entity("game") { 
	
	new GroovyBootstrapper();
	
	property("gameState", "playing");
	property("playtime",0)
	
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
		parent("gemserk.states.stateBasedNode",[enabled:false,exclusions:[SlickRenderMessage.class]])
		parent("zombierockers.scenes.playing") 
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
		}
	}
	component(utils.components.genericComponent(id:"dumpEntitiesHandler", messageId:"dumpEntities"){ message ->
		println JSONArray.fromObject(new EntityDumper().dumpEntity(entity.root)).toString(4)
	} ) 
	
	utils.custom.messageQueue.enqueue(utils.genericMessage("resume"){})	
}