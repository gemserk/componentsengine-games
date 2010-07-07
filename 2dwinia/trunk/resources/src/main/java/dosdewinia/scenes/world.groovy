
package dosdewinia.scenes
import com.gemserk.componentsengine.predicates.EntityPredicates;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.games.dosdewinia.Target;
import com.google.common.base.Predicates 

builder.entity {
	
	
	def traversableColor = utils.color(1,1,1,1)
	def traversable = { terrainMap, position ->
		def x = (int)position.x
		def y = (int)position.y
		if(x < 0 || y < 0 || x > terrainMap.width || y > terrainMap.height)
			return false
		
		def terrainColor = terrainMap.getColor((int)position.x, (int)position.y)
		return terrainColor == traversableColor
	}
	
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("level", parameters.level)
	
	property("started",false)
	property("terrainMap",utils.resources.image("terrainMap"))
	
	component(new ImageRenderableComponent("imagerenderer")) {
		property("image", utils.resources.image("background"))
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", -1000)
	}
	
	
	
	
	
	component(utils.components.genericComponent(id:"enterStateHandler", messageId:"enterNodeState"){ message ->
		if(entity.started)
			return
		
		entity.started = true
		messageQueue.enqueue(utils.genericMessage("spawnDarwinians"){newMessage -> newMessage.quantity = 100})
	})
	
	child(entity("darwinianSpawner"){
		
		property("darwinianTemplate",new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("dosdewinia.entities.darwinian"), 
		utils.custom.genericprovider.provide{ data ->
			[
			position:data.position, 
			speed:0.04f,
			target: data.target
			]
		}))
		
		
		component(utils.components.genericComponent(id:"spawnDarwinians", messageId:"spawnDarwinians"){ message ->
			log.info("Adding first darwinian")
			def iniTime = System.currentTimeMillis()
			message.quantity.times {
				def darwinian = entity.darwinianTemplate.get([position:utils.vector(500,300), target:new Target(utils.vector(600,400),100,10)])
				messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(darwinian,entity))
			}
			
			log.info("Time to create darwinians: ${System.currentTimeMillis() - iniTime}")
		})
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
	
	property("selectedEntityCandidate", null)
	property("selectedEntity", null)
	component(utils.components.genericComponent(id:"selectcandidateforselection", messageId:["update"]){ message ->
		def input = utils.custom.gameContainer.input
		def cursorPosition = utils.vector(input.getMouseX(),input.getMouseY())
		
		def selectables = entity.root.getEntities(Predicates.and(EntityPredicates.withAllTags("selectable"),EntityPredicates.isNear(cursorPosition, (float)10)))
		
		if(selectables.isEmpty()){
			entity.selectedEntityCandidate = null
			return
		}
		def candidate = selectables[0]		
		entity.selectedEntityCandidate = candidate
	})
	
	component(utils.components.genericComponent(id:"selector", messageId:["mouse.left.press"]){ message ->
		def candidate = entity.selectedEntityCandidate
		if(candidate){
			entity.selectedEntity = candidate
			log.info("Selected entity - $candidate.id")
		}
	
		
	})
	
	property("selectedOfficerCursor",utils.resources.image("selectedOfficerCursor"))
	component(utils.components.genericComponent(id:"selectedEntityCandidateRenderer", messageId:["render"]){ message ->		
		def renderer = message.renderer
		def selectedEntity = entity.selectedEntity
		def selectedEntityCandidate  = entity.selectedEntityCandidate
		
		
		def toRenderList =[]
		if(selectedEntity)
			toRenderList << [entity:selectedEntity, color:utils.color(1,0,1,1)]
			                 
		if(selectedEntityCandidate && selectedEntity != selectedEntityCandidate)
			toRenderList <<[entity:selectedEntityCandidate, color:utils.color(1,1,1,1)]
		
		toRenderList.each { toRender ->
			def theEntity = toRender.entity
			def renderColor = toRender.color
			if(!theEntity.tags.contains("officer"))
				return
			
			def center = theEntity.position
			def radius = theEntity.radius
			def displacement = (float)radius / 2f
			
			renderer.enqueue( new ClosureRenderObject(1000, { Graphics g ->
				def oldColor = g.getColor()
				g.setColor(renderColor)
				g.drawOval((float)center.x - displacement,(float)center.y -displacement, radius, radius)
				g.setColor(oldColor)
			}))
			
			def targetPosition = theEntity.destinationPoint
			
			if(!targetPosition)
				return
			
			def selectedOfficerCursor = entity.selectedOfficerCursor
			
			def cursorDisplacement = (float)selectedOfficerCursor.width /2f
			
			
			renderer.enqueue( new ClosureRenderObject(1000, { Graphics g ->
				def oldColor = g.getColor()
				g.drawImage(selectedOfficerCursor, (float)targetPosition.x - cursorDisplacement,(float)targetPosition.y - cursorDisplacement,renderColor)
				g.setColor(oldColor)
			}))
			
		}
	})
	
	component(utils.components.genericComponent(id:"unselector", messageId:["space"]){ message ->
		entity.selectedEntity = null
	})
	
	
	child(entity("officialPlacer"){
		property("placementPoint", null)
		
		
		property("officerTemplate",new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("dosdewinia.entities.officer"), 
				utils.custom.genericprovider.provide{ data ->
					[
					position:data.position,
					direction:data.direction,
					destinationPoint:data.destinationPoint
					]
				}))
		
		
		component(utils.components.genericComponent(id:"startOfficial", messageId:["mouse.left.press"]){ message ->
			
			if(entity.parent.selectedEntityCandidate)
				return
				
		
			def input = utils.custom.gameContainer.input
			def position = utils.vector(input.getMouseX(),input.getMouseY())
			
			def officer = entity.officerTemplate.get([position:position])
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(officer,entity.parent))
			entity.parent.selectedEntity = officer
		})
		
		
		component(utils.components.genericComponent(id:"selectDestination", messageId:["mouse.right.press"]){ message ->
		
			def selectedEntity = entity.parent.selectedEntity
		
			if(!selectedEntity)
				return
			
			if(!selectedEntity.tags.contains("officer"))
				return
		
			def input = utils.custom.gameContainer.input
			def destination = utils.vector(input.getMouseX(),input.getMouseY())
			
			if(traversable(entity.parent.terrainMap,destination))
				selectedEntity.destinationPoint = destination
			
			
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
			
			def direction = destinationPoint.copy().sub(placementPoint).normalise()
			
			def officer = entity.officerTemplate.get([position:placementPoint, destinationPoint:destinationPoint ,direction:direction])
			messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(officer,entity))
		})
		
		
	})
	
	component(new ExplosionComponent("explosions")) {
	}
	
	
	
	
	component(utils.components.genericComponent(id:"debug-directions", messageId:["render"]){ message ->
		
		if(!utils.custom.gameContainer.input.isKeyDown(Input.KEY_1))
			return
		
		def renderer = message.renderer
		
		def darwinians = entity.getEntities(EntityPredicates.withAllTags("darwinian"))
		
		darwinians.each { darwinian ->
			def start = darwinian.position.copy()
			def end = darwinian.position.copy().add(darwinian.direction.copy().normalise().scale(50))
			renderer.enqueue( new ClosureRenderObject(5, { Graphics g ->
				g.drawLine( start.x, start.y, end.x, end.y)
			}))
		}
		
		
	})
	
	component(utils.components.genericComponent(id:"debug-targetPositions", messageId:["render"]){ message ->
		
		if(!utils.custom.gameContainer.input.isKeyDown(Input.KEY_2))
			return
		
		def renderer = message.renderer
		
		def darwinians = entity.getEntities(EntityPredicates.withAllTags("darwinian"))
		
		darwinians.each { darwinian ->
			def start = darwinian.position.copy()
			def end = darwinian.targetPosition?.copy()
			if(end != null){
				renderer.enqueue( new ClosureRenderObject(5, { Graphics g ->
					g.drawLine( start.x, start.y, end.x, end.y)
				}))
			}
		}
		
		
	})
	
	component(utils.components.genericComponent(id:"debug-officers", messageId:["render"]){ message ->
		
		if(!utils.custom.gameContainer.input.isKeyDown(Input.KEY_3))
			return
		
		def renderer = message.renderer
		
		def officers = entity.getEntities(EntityPredicates.withAllTags("officer"))
		
		officers.each { officer ->
			def start = officer.position.copy()
			def end = officer.destinationPoint.copy()
			
			renderer.enqueue( new ClosureRenderObject(5, { Graphics g ->
				g.drawLine( start.x, start.y, end.x, end.y)
			}))
		}
		
		
	})
	
	component(utils.components.genericComponent(id:"debug-terrainMap", messageId:["render"]){ message ->
		
		if(!utils.custom.gameContainer.input.isKeyDown(Input.KEY_0))
			return
		
		def renderer = message.renderer
		def terrainMap = entity.terrainMap
		renderer.enqueue( new ClosureRenderObject(1000, { Graphics g ->
			def oldColor = g.getColor()
			g.setColor(utils.color(1,1,1,0.0f))
			g.drawImage(terrainMap, 0,0)
			g.setColor(oldColor)
		}))
		
		
	})
	
}