package zombierockers.scenes


import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.WorldBoundsComponent 

builder.entity {
	
	
	
	child(entity("world"){ parent("zombierockers.scenes.world",parameters) })
	
	child(entity("cursor"){
		
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
			property("image", utils.resources.image("ship"))
			property("color", utils.color(1,1,1,1))
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
	
//	TEMPORAL PATH EDITOR
//	property("newPositions", [])
//	
//	property("path", new Path([]))
//	
//	child(entity("editor.newPathRenderer"){ 
//
//		component(new PathRendererComponent("editor.pathRendererFill")){
//			property("lineColor", utils.color(0.5f, 0.5f, 1f, 1.0f))
//			property("lineWidth", 10.0f)
//			property("path", {entity.parent.path})		
//		}
//		
//	})
//	
//	
//	component(utils.components.genericComponent(id:"editor.putPositionHandler", messageId:"rightmouse"){ message ->
//		entity.newPositions << entity.mousePosition
//		log.info("Editor new position : $entity.mousePosition")
//		
//		entity.path = new Path(entity.newPositions)
//	})
//	
//	component(utils.components.genericComponent(id:"editor.mouseMoveHandler", messageId:"movemouse"){ message ->
//		entity.mousePosition = new Vector2f(message.x, message.y)
//	})
//	
//	component(utils.components.genericComponent(id:"editor.dumpPositions", messageId:"dumpEditorPositions"){ message ->
//		log.info("newPositions dump ")
//		entity.newPositions.each {   println "utils.vector(${it.x}f, ${it.y}f)," }
//	})
	
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

