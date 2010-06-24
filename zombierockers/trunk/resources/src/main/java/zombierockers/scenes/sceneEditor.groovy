package zombierockers.scenes

import org.newdawn.slick.Graphics;
import com.gemserk.componentsengine.render.ClosureRenderObject;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.games.zombierockers.PathTraversal;


builder.entity {
	
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("level", parameters.level)
	property("currentLevelIndex",parameters.levelIndex)
	
	
	
	property("path",new Path(utils.custom.svg.loadPoints(entity.level.path, "path")))	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image(entity.level.background))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", -1000)
	}
	
	component(utils.components.genericComponent(id:"placeablesRender", messageId:["render"]){ message ->
		
		def renderer = message.renderer
		
		def placeables = entity.level.placeables
		placeables.each { placeable ->
			def position = placeable.position
			def layer = placeable.layer
			def image = utils.resources.image(placeable.image)
			def input = utils.custom.gameContainer.input
			//position = utils.vector(input.mouseX, input.mouseY)
			//println position
			renderer.enqueue( new ClosureRenderObject(layer, { Graphics g ->
				g.pushTransform()
				g.translate((float) position.x + 5, (float)position.y + 5)
				g.drawImage(image, (float)-(image.getWidth() / 2), (float)-(image.getHeight() / 2))
				g.popTransform()
			}))
		}
	})
	
	
	
	
	input("inputmapping"){
		keyboard {
			press(button:"escape",eventId:"returnToGame")
			hold(button:"up",eventId:"advance")
			hold(button:"down",eventId:"retreat")
			press(button:"prior",eventId:"accelerate")
			press(button:"next",eventId:"deaccelerate")
			press(button:"space",eventId:"printDistance")
		}
	}
	
	def path = entity.path
	
	child(entity("pointerBall"){
		
		property("pathTraversal",new PathTraversal(path,0))
		property("velocity",(float)100/1000)
		property("delta",1)
		property("direction",0)
		
		component(new CircleRenderableComponent("circlerendererbig")) {
			property("position", {entity.pathTraversal.position})
			property("radius", 16f)
			property("lineColor", utils.color(0,0,0,1))
			property("fillColor", utils.color(0,0,0,0.1f))
			property("layer",1000)
		}
		component(new CircleRenderableComponent("circlerenderersmall")) {
			property("position", {entity.pathTraversal.position})
			property("radius", 1f)
			property("lineColor", utils.color(0,0,0,1))
			property("fillColor", utils.color(0,0,0,1))
			property("layer",1000)
		}
		
		child(entity("velocityLabel"){
			
			parent("gemserk.gui.label", [
			//font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
			position:utils.vector(60f, 20f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-50f, -20f, 100f, 40f),
			align:"left",
			valign:"top"
			])
			
			property("message", {"VEL: ${entity.parent.velocity}".toString() })
		})
		
		child(entity("positionLabel"){
			
			parent("gemserk.gui.label", [
			//font:utils.resources.fonts.font([italic:false, bold:false, size:16]),
			position:utils.vector(60f, 40f),
			fontColor:utils.color(0f,0f,0f,1f),
			bounds:utils.rectangle(-50f, -20f, 100f, 40f),
			align:"left",
			valign:"top"
			])
			
			property("message", {def pos =entity.parent.pathTraversal.position; return "POS: ($pos.x,$pos.y)".toString() })
		})
		
		component(utils.components.genericComponent(id:"captureDelta", messageId:"update"){ message ->
			entity.pathTraversal = entity.pathTraversal.add((float)entity.direction*entity.velocity * message.delta)
			entity.direction = 0
		})
		
		component(utils.components.genericComponent(id:"advance", messageId:["advance","retreat"]){ message ->
			if(message.id == "advance")
				entity.direction = 1
			else
				entity.direction = -1		
		})
		
		component(utils.components.genericComponent(id:"accelerator", messageId:["accelerate","deaccelerate"]){ message ->
			if(message.id == "accelerate")
				entity.velocity = (float)1.10f*entity.velocity
			else
				entity.velocity = (float)0.90f*entity.velocity
		})
		
		component(utils.components.genericComponent(id:"printerDistancer", messageId:["printDistance"]){ message ->
			println "${entity.pathTraversal.distanceFromOrigin}"
		})
	})
	
	property("sceneTemplate",utils.custom.templateProvider.getTemplate("zombierockers.scenes.scene"))
	
	component(utils.components.genericComponent(id:"returnToGameHandler", messageId:"returnToGame"){ message ->
		def levelIndex = entity.currentLevelIndex
		def scene = entity.sceneTemplate.instantiate(entity.id,[levelIndex:levelIndex])
		messageQueue.enqueueDelay(ChildrenManagementMessageFactory.addEntity(scene,entity.root))
	})
}
