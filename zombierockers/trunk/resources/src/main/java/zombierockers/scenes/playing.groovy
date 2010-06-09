package zombierockers.scenes


import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 

builder.entity {
	
	
	
	child(entity("world"){ parent("zombierockers.scenes.world",parameters) })
	
	child(entity("cursor"){
		
		property("color", {entity.parent.children["world"].children["cannon"].currentBall.color})
		
		property("position",utils.vector(400,300))
		property("bounds",utils.rectangle(20,20,760,520))
		
		component(new WorldBoundsComponent("bounds")){
			propertyRef("bounds","bounds")
			propertyRef("position","position")
		}
		
		component(utils.components.genericComponent(id:"mousemovehandler", messageId:["movemouse"]){ message ->
			entity.position =  utils.vector(message.x,message.y)
		})
		
		component(new ImageRenderableComponent("imagerenderer")) {
			property("image", utils.resources.image("cursor"))
			propertyRef("color", "color")
			property("position", {entity.position})
			property("direction", utils.vector(0,1))
		}
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"pauseGame")
			press(button:"p",eventId:"pauseGame")
			press(button:"g",eventId:"dumpEditorPositions")
		}
		mouse {
			press(button:"left", eventId:"leftmouse")
			press(button:"right", eventId:"rightmouse")
			move(eventId:"movemouse") { message ->
				message.x = position.x
				message.y = position.y
			}
		}
	}
	
	component(utils.components.genericComponent(id:"pauseGameHandler", messageId:"pauseGame"){ message ->
		if (messageQueue.messages.isEmpty())
			messageQueue.enqueue(utils.genericMessage("paused"){})
		else
			messageQueue.enqueue(utils.genericMessage("pauseGame"){})
	})
	
	child(entity("fpsLabel"){
		
		parent("gemserk.gui.label", [
		//font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
		position:utils.vector(60f, 30f),
		fontColor:utils.color(0f,0f,0f,1f),
		bounds:utils.rectangle(-50f, -20f, 100f, 40f),
		align:"left",
		valign:"top"
		])
		
		property("message", {"FPS: ${utils.custom.gameContainer.getFPS()}".toString() })
	})
	
	child(entity("explosionEntity") { 
		component(new ExplosionComponent("explosions")) {
		}
	})
	
}

