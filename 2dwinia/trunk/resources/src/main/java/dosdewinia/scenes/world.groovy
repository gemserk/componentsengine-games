package dosdewinia.scenes
import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 

builder.entity {
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("level", parameters.level)
	
	property("started",false)
	
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("background"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", -1000)
	}
	
	
	property("darwinianTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dosdewinia.entities.darwinian"), 
			utils.custom.genericprovider.provide{ data ->
				[position:data.position, speed:0.04f]
			}))
	
	
	
	component(utils.components.genericComponent(id:"enterStateHandler", messageId:"enterNodeState"){ message ->
		if(entity.started)
			return
		
		entity.started = true
		log.info("Adding first darwinian")
		def iniTime = System.currentTimeMillis()
		500.times {
			def darwinian = entity.darwinianTemplate.get([position:utils.vector(500,300)])
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(darwinian,entity))
		}
		
		log.info("Time to create darwinians: ${System.currentTimeMillis() - iniTime}")
	})
	
	
	//	child(entity("camera"){
	//		
	//		component(utils.components.genericComponent(id:"changePerspective", messageId:["render"]){ message ->
	//			def renderer = message.renderer
	//			
	//			
	//			renderer.enqueue( new ClosureRenderObject(0, { Graphics g ->
	//				g.pushTransform()
	//				g.translate(100,100)
	//			}))
	//			
	//			renderer.enqueue( new ClosureRenderObject(10, { Graphics g ->
	//				g.popTransform()
	//			}))
	//			
	//			renderer.enqueue( new ClosureRenderObject(20, { Graphics g ->
	//				g.drawRect 0, 0, 50, 50
	//			}))
	//			
	//		})
	//	})
	
	
	
	child(entity("officialPlacer"){
		property("placementPoint", null)
		
		
		property("officerTemplate",new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("dosdewinia.entities.officer"), 
			utils.custom.genericprovider.provide{ data ->
				[position:data.position, direction:data.direction]
			}))
		
		
		component(utils.components.genericComponent(id:"startOfficial", messageId:["mouse.left.press"]){ message ->
			def input = utils.custom.gameContainer.input
			entity.placementPoint = utils.vector(input.getMouseX(),input.getMouseY())
		})
		
		
		component(utils.components.genericComponent(id:"endOfficial", messageId:["mouse.left.release"]){ message ->
			def placementPoint = entity.placementPoint
			
			if(!placementPoint)
				return
			
			entity.placementPoint = null
				
			def input = utils.custom.gameContainer.input
			def destinationPoint = utils.vector(input.getMouseX(),input.getMouseY())
			log.info("POS: $entity.placementPoint - DIR: $destinationPoint")
			if(destinationPoint == placementPoint)
				return 
				
			def direction = destinationPoint.sub(placementPoint).normalise()
				
			def officer = entity.officerTemplate.get([position:placementPoint, direction:direction])
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(officer,entity))
		})
		
		component(utils.components.genericComponent(id:"renderArrow", messageId:["render"]){ message ->
			def placementPoint = entity.placementPoint
			if(!placementPoint)
				return 
			
			def renderer = message.renderer
			def input = utils.custom.gameContainer.input
			def destinationPoint = utils.vector(input.getMouseX(),input.getMouseY())
			renderer.enqueue( new ClosureRenderObject(0, { Graphics g ->
				def start = entity.placementPoint
				def end = destinationPoint
				g.drawLine( start.x, start.y, end.x, end.y)
			}))
		})
		
	})
	
	component(new ExplosionComponent("explosions")) {
	}
	
}