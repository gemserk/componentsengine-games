package dosdewinia.scenes
import com.gemserk.games.dosdewinia.ZonesMap;
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
import com.gemserk.games.dosdewinia.TraversalMap 
import com.google.common.base.Predicates 

builder.entity {
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("level", parameters.level)
	
	property("started",false)
	property("mapData",[traversableMap:new TraversalMap(Thread.currentThread().getContextClassLoader().getResourceAsStream("levels/passage/terrainMap.png")),
			zoneMap:new ZonesMap(Thread.currentThread().getContextClassLoader().getResourceAsStream("levels/passage/zoneMap.png"))
			])
	
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
		messageQueue.enqueue(utils.genericMessage("spawnDarwinians"){newMessage -> newMessage.quantity = 1})
	})
	
	child(entity("darwinianSpawner"){
		property("mapData",{entity.parent.mapData})
		
		property("darwinianTemplate",new InstantiationTemplateImpl(
				utils.custom.templateProvider.getTemplate("dosdewinia.entities.darwinian"), 
				utils.custom.genericprovider.provide{ data ->
					[
					position:data.position, 
					speed:0.04f,
					target: data.target,
					mapData: data.mapData
					]
				}))
		
		
		component(utils.components.genericComponent(id:"spawnDarwinians", messageId:"spawnDarwinians"){ message ->
			log.info("Adding first darwinian")
			def iniTime = System.currentTimeMillis()
			def mapData = entity.mapData
			def zoneMap = mapData.zoneMap
			message.quantity.times {
				def darwinian = entity.darwinianTemplate.get([position:utils.vector(500,300), target:new Target(utils.vector(600,400),100,10,zoneMap.getZoneValue(utils.vector(600,400))),mapData:mapData])
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
		property("traversableMap",{entity.parent.mapData.traversableMap})
		property("zoneMap",{entity.parent.mapData.zoneMap})
		
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
			
			if(entity.traversableMap.getTraversable(destination)){
				selectedEntity.destinationPoint = destination
				selectedEntity.destinationPointZoneId = entity.zoneMap.getZoneValue(destination)
			}
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
	
	component(utils.components.genericComponent(id:"debug-zoneMap", messageId:["render"]){ message ->
		
		if(!utils.custom.gameContainer.input.isKeyDown(Input.KEY_3))
			return
		
		def renderer = message.renderer
		
		def image = utils.resources.image("zoneMap")
		
		renderer.enqueue( new ClosureRenderObject(5, { Graphics g ->
			g.drawImage(image,0,0,utils.color(1,1,1,0.5f))
		}))
		
	})
	
	component(utils.components.genericComponent(id:"debug-terrainMap", messageId:["render"]){ message ->
		
		if(!utils.custom.gameContainer.input.isKeyDown(Input.KEY_4))
			return
		
		def renderer = message.renderer
		
		def image = utils.resources.image("terrainMap")
		
		renderer.enqueue( new ClosureRenderObject(5, { Graphics g ->
			g.drawImage(image,0,0,utils.color(1,1,1,0.5f))
		}))
		
	})
}