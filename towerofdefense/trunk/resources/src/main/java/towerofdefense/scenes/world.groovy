package towerofdefense.scenes;


import com.gemserk.componentsengine.commons.components.ExplosionComponent;
import com.gemserk.componentsengine.commons.components.DisablerComponent;


import com.gemserk.componentsengine.predicates.EntityPredicates;

import com.google.common.base.Predicates;

import com.gemserk.componentsengine.messages.GenericMessage;

import com.gemserk.games.towerofdefense.components.render.CrossRendererComponent;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures 
import com.gemserk.componentsengine.messages.*;

import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.components.Component 

import towerofdefense.components.TowerDeployer 

import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.timers.CountDownTimer 
import com.gemserk.componentsengine.timers.PeriodicTimer 

import towerofdefense.GroovyBootstrapper;
import com.gemserk.componentsengine.commons.components.*;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.towerofdefense.*;
import com.gemserk.games.towerofdefense.components.*;
import com.gemserk.games.towerofdefense.springmesh.MeshFactory 
import com.gemserk.games.towerofdefense.springmesh.SpringMesh;
import com.gemserk.games.towerofdefense.springmesh.SpringMeshComponent;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity {
	
	def utils = utils
	new GroovyBootstrapper();
	
	def gridDistance = 15f
	
	property("money", parameters.money)
	property("points",0)
	property("lives",parameters.lives)
	property("wavesTimer", new PeriodicTimer(parameters.wavePeriod))
	property("towerCount",0)
	
	def gameBoundsToRender = utils.rectangle(0, 120, 800, 480)
	def borderSize = 20
	def gameBounds = utils.rectangle((float)gameBoundsToRender.x+borderSize,(float)gameBoundsToRender.y+borderSize,(float)gameBoundsToRender.width-2*borderSize,(float)gameBoundsToRender.height-2*borderSize)
	
	property("gameBoundsToRender", gameBoundsToRender)
	property("gameBounds",gameBounds)
	
	component(new RectangleRendererComponent("gameBoundsToRenderRenderer")) {
		property("position", utils.vector(0, 0))
		propertyRef("rectangle", "gameBoundsToRender")
		property("fillColor", utils.color(0.0f, 0.0f, 0.0f, 1.0f))
		property("lineColor", utils.color(0f, 0f, 0f, 0f))
	}
	
	//	component(new GridRenderer("grid")){
	//		propertyRef("bounds","gameBoundsToRender")
	//		property("distance",gridDistance)
	//	}
	
	def meshVPoints = 64
	def meshHPoints = 64
	
	property("springMesh", new SpringMesh(MeshFactory.springMesh(800 + meshHPoints, 600 + meshVPoints, meshHPoints, meshVPoints)))
	
	component(new SpringMeshComponent()) {
		property("quadMesh2d", MeshFactory.quadMesh2d(meshVPoints, meshHPoints, null))
		propertyRef("springMesh", "springMesh")
	}
	
	component(utils.components.genericComponent(id:"explosionMeshEffect", messageId:"explosion"){ message ->
		def springMesh = entity.springMesh
		def position = message.position
		def range = (float) (message.range)
		def power = (float) message.power
		def time = 300
		springMesh.applyForce(time, position, range, power)
	})
	
	component(new RectangleRendererComponent("hudBackgroundRenderer")) {
		property("position", utils.vector(0, 0))
		property("rectangle", utils.rectangle(0, 0, 800, 120))
		property("fillColor", utils.color(0.1f, 0.1f, 0.1f, 1.0f))
		property("lineColor", utils.color(0f, 0f, 0f, 0f))
	}
	
	property("path",parameters.path)
	
	component(new PathRendererComponent("pathrenderer2")){
		property("lineColor", utils.color(0.3f, 0.3f, 1.0f, 1f))
		property("lineWidth", (float)(gridDistance + 6f))
		propertyRef("path", "path")		
	}                		
	
	component(new PathRendererComponent("pathrenderer")){
		property("lineColor", utils.color(0.2f, 0.2f, 0.8f, 1f))
		property("lineWidth", gridDistance)
		propertyRef("path", "path")		
	}                		
	
	child(template:"towerofdefense.entities.base", id:"base")	{
		position=parameters.path.points[-1]//last point in path
		direction=utils.vector(-1,0)
		radius=30f
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 0.0f)
		fillColor=utils.color(0.2f, 0.2f, 0.7f, 1.0f)
	}
	
	property("waves", new Waves().setWaves(parameters.waves))
	
	def spawnerDirection = parameters.path.points[0].copy().sub(parameters.path.points[1]).normalise()
	def spawnerPos = parameters.path.points[0].copy().sub(spawnerDirection.copy().scale(-20))
	
	child(template:"towerofdefense.entities.spawner", id:"spawner")	{
		position = spawnerPos
		direction = spawnerDirection
		//position={entity.parent.path.getPoint(0)}
		waves={entity.parent.waves }
		// waves=new Waves().setWaves(parameters.waves)
	}
	
	property("towerDescriptions", parameters.towerDescriptions )
	
	property("towerType","blaster")
	
	component(new TowerDeployer("towerdeployer")){
		property("instantiationTemplate",{
			entity.towerDescriptions[(entity.towerType)].instantiationTemplate
		})
		propertyRef("towerCount","towerCount")
	}
	
	component(utils.components.genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		def reward = message.critter.reward
		def points = message.critter.points
		entity.money=(float)(entity.money + reward)
		entity.points=(int)(entity.points + points)
	})
	
	
	
	def labelX = 300;
	def labelY = 20;
	
	component(new LabelComponent("moneylabel")){
		property("position",utils.vector(labelX,labelY + 0))
		property("message","Money: {0}")
		property("value",{entity.money })
	}
	
	component(new LabelComponent("pointslabel")){
		property("position",utils.vector(labelX,labelY + 20))
		property("message","Points: {0}")
		property("value",{entity.points })
	}
	
	if(utils.custom.gameStateManager.gameProperties.runningFromMain){
		component(new LabelComponent("towerCountlabel")){
			property("position",utils.vector(labelX,labelY + 60))
			property("message","Towers: {0}")
			propertyRef("value","towerCount")
		}	
	}
	
	component(new LabelComponent("wavesLabel")){
		property("position",utils.vector(labelX,labelY + 80))
		property("message", {"Waves: ${entity.waves.current}/${entity.waves.total}".toString() })
	}		
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		propertyRef("bounds", "gameBoundsToRender");
	}
	
	component(new TimerComponent("wavesTimerComponent")){
		property("trigger",utils.custom.triggers.genericMessage("nextWave") {
		})
		propertyRef("timer","wavesTimer")
	}
	
	component(utils.components.genericComponent(id:"nextWaveMessageHandler", messageId:"nextWave"){ message ->
		entity.wavesTimer.reset()
	})
	
	
	property("deployTower", "deployState")
	
	property("selectedTower",null)
	
	component(new GuiLogicComponent("guiComponent")){
		propertyRef("mousePosition", "mousePosition")
		propertyRef("deployCursorState", "deployCursorState")
		propertyRef("deployTowerEnabled", "deployTowerEnabled")
		propertyRef("path","path")
		property("distanceToPath",(float)(gridDistance*1.5))
		propertyRef("selectedTower","selectedTower")
		propertyRef("towerDescriptions", "towerDescriptions")
		propertyRef("gameBounds","gameBounds")
		propertyRef("towerType","towerType")
		propertyRef("money","money")
	}
	
	property("deployTowerEnabled", false)
	property("deployCursorState", "candeploy")
	property("mousePosition", utils.vector(0f, 0f))
	
	
	child(entity("deploy cursor") {
		
		def mapeo = [
		"candeploy":[color:utils.color(0.0f, 0.8f, 0.0f,0.25f),image:null],
		"cantdeploy":[color:utils.color(0.8f, 0.0f, 0.0f,0.25f),image:null],
		"cantdeploy-money":[color:utils.color(0.8f, 0.0f, 0.0f,0.25f),image:utils.resources.image("towerofdefense.images.sell_icon")]
		]
		
		
		property("enabled", {entity.parent.deployTowerEnabled })
		property("position", {entity.parent.mousePosition })
		property("cursorState", {entity.parent.deployCursorState })
		property("towers", {entity.parent.towers })
		property("towerType", {entity.parent.towerType })
		
		property("radius", {
			entity.towers[(entity.towerType)].radius
		})
		
		property("border", utils.color(0.5f, 0.5f, 0.5f, 0.1f))
		property("fill", {
			mapeo[(entity.cursorState)].color
		})
		
		property("image",{
			mapeo[(entity.cursorState)].image
		})
		
		component(new DisablerComponent(new ImageRenderableComponent("image"))) {
			propertyRef("image", "image")
			propertyRef("position", "position")
			property("direction", utils.vector(1f,0f))
			property("size",utils.vector(2f,2f))
			property("enabled",{
				entity.enabled && entity.image != null
			})
		}
		
		
		component(new DisablerComponent(new CircleRenderableComponent("circle"))){
			propertyRef("lineColor", "border")
			propertyRef("radius", "radius")
			propertyRef("fillColor", "fill")
			propertyRef("position", "position")
			propertyRef("enabled", "enabled")
		}
		
		component(new DisablerComponent(new CrossRendererComponent("cross"))) {
			property("width", 1.0f)
			propertyRef("color", "fill")
			propertyRef("radius", "radius")
			propertyRef("position", "position")
			propertyRef("enabled", "enabled")
		}
		
	})
	
	child(entity("tower highlighter"){
		
		property("deployState", {entity.parent.deployTowerEnabled })
		
		property("lineColor",{
			entity.deployState ? utils.color(1f,0.3f,0.3f,0.4f) : utils.color(0.3f,1f,0.3f,0.4f)
		})
		property("fillColor",{
			entity.deployState ? utils.color(1f,0f,0.0f,0.2f) : utils.color(0.5f,1f,0.5f,0.2f)
		})
		property("selectedTower", null)
		
		property("visible", false)
		
		property("cursorPosition", utils.vector(100f,100f))
		property("position", utils.vector(-20f,-20f))
		
		property("radius", {entity.deployState ? 25f : 20f })
		
		component(new DisablerComponent(new CircleRenderableComponent("circle"))){
			propertyRef("lineColor", "lineColor")
			propertyRef("fillColor", "fillColor")
			propertyRef("radius", "radius")
			propertyRef("position", "position")
			propertyRef("enabled", "visible")
		}
		
		component(utils.components.genericComponent([id:"mouse move", messageId:"move"]) { m ->
			entity.cursorPosition.x = m.x
			entity.cursorPosition.y = m.y
		})
		
		component(utils.components.genericComponent(id:"tower selected handler", messageId:"towerSelected"){ message ->
			entity.selectedTower = message.tower		
		})
		
		component(new ComponentFromListOfClosures("toggle", [ {UpdateMessage m ->
			
			entity.visible = false
			
			
			Entity world = entity.parent
			
			def position = entity.cursorPosition
			def distance = entity.radius
			
			def towers = world.getEntities(Predicates.and(EntityPredicates.withAllTags("tower"), EntityPredicates.isNear(position, distance)));
			
			if (towers.isEmpty()) 
				return
			
			def tower = towers[0]
			
			def selectedTower = entity.selectedTower
			
			if (selectedTower != null && selectedTower == tower)
				return
			
			entity.position = tower.position
			entity.visible = true
		}
		]))
	}) 
	
	
	component(new LabelComponent("towersLabel")){
		property("position",utils.vector(40,40))
		property("message", "Towers")
	}	
	
	def towerButtonsX = 40
	def towerButtonsY = 70
	
	def buttonRectangle = utils.rectangle(-25, -25, 50, 50)
	
	def towerGuiInstantiation = [:]
	parameters.towerDescriptions.each { key, value -> 
		towerGuiInstantiation[(key)]=value.instantiationTemplate.get()
	}
	
	property("towers",towerGuiInstantiation)
	
	parameters.towerDescriptions.each { key, value -> 
		
		child(template:"towerofdefense.entities.button", id:"button-${key}".toString())	{
			position=utils.vector(towerButtonsX, towerButtonsY)
			rectangle=buttonRectangle
			icon=utils.resources.image(value.icon)
			mouseNotOverFillColor={entity.canBuy ? utils.color(0.0f, 1.0f, 0.0f, 0.1f) : utils.color(1.0f, 1.0f, 1.0f, 0.4f)}
			mouseOverFillColor=utils.color(0.0f, 1.0f, 0.0f, 0.4f)
			trigger=utils.custom.triggers.genericMessage("deployTowerSelected") {
				message.towerType = "${key}".toString()
			}
			
		}
		
		component(new LabelComponent("towerCostLabel-${key}".toString())){
			property("position",utils.vector(towerButtonsX,towerButtonsY+35))
			property("message", "\${0,number,integer}".toString())
			property("value",value.cost)
		}	
		
		towerButtonsX+=60
	}
	
	def commandButtonX = 660
	def commandButtonY = towerButtonsY
	
	child(template:"towerofdefense.entities.timerbutton", id:"button-nextWave")	{
		position=utils.vector(750, towerButtonsY)
		rectangle=buttonRectangle
		icon=utils.resources.image("towerofdefense.images.nextwave_icon")
		mouseNotOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.4f)
		mouseOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.7f)
		timeLeft={
			entity.parent.wavesTimer.timeLeft/parameters.wavePeriod
		}
		trigger=utils.custom.triggers.genericMessage("nextWave") {
		}
	}
	
	
	
	component(new ComponentFromListOfClosures("cheats",[ {GenericMessage message ->
		switch(message.id){
			case "cheatMoney":
			entity.money=(float)(entity.money + 10)
			break;
			case "cheatLives":
			entity.lives+=10
			break;
		}
	}
	]))
	
	property("endSceneEnabled", false)
	property("endSceneMessage", "")
	property("endSceneTimer", new CountDownTimer(3000))
	
	component(utils.components.genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		if (entity.lives == 0)
			return
		
		entity.lives--;
		
	})
	
	component(new DisablerComponent(new LabelComponent("endSceneLabel"))) {
		propertyRef("enabled", "endSceneEnabled")
		property("font", utils.resources.fonts.font([italic:false, bold:false, size:48]))
		property("position", utils.vector(400, 300))
		propertyRef("message","endSceneMessage")
	}
	
	
	component(new EndSceneComponent("endSceneTrigger")){
		propertyRef("waves", "waves")
		propertyRef("lives", "lives")
		propertyRef("timer", "endSceneTimer")
		property("tags", ["critter"] as String[])
		
		propertyRef("endSceneEnabled", "endSceneEnabled")
		propertyRef("message", "endSceneMessage")
	}
	
	component(new TimerComponent("endSceneTimer")) {
		propertyRef("timer", "endSceneTimer")
		property("trigger", utils.custom.triggers.genericMessage("win") {
		});
	}
	
	component(utils.components.genericComponent(id:"winHandler", messageId:"win"){ message ->
		utils.custom.gameStateManager.gameProperties.inGame=false
		messageQueue.enqueue(utils.genericMessage("gotoMenu") {
			
		})
	})
	
	component(utils.components.genericComponent(id:"gotoMenuHandler", messageId:"gotoMenu"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager;
		stateBasedGame.enterState(0, new FadeOutTransition(), new FadeInTransition());
	})
	
	component(utils.components.genericComponent(id:"dumpDebugHandler", messageId:"dumpDebug"){ message ->
		Entity.times.entrySet().sort({it.count }).each { entry ->  println "$entry.element - $entry.count" }
	} )                                                                    
	
	component(utils.components.genericComponent(id:"reloadSceneHandler", messageId:"reloadScene"){ message ->
		if(utils.custom.gameStateManager.gameProperties.runningFromMain)
			utils.custom.game.loadScene(parameters.sceneScript);
	})
	
	input("inputmapping"){
		keyboard {
			press(button:"w", eventId:"nextWave")
			press(button:"r", eventId:"reloadScene")
			press(button:"m",eventId:"cheatMoney")
			press(button:"l",eventId:"cheatLives")
			press(button:"d",eventId:"dumpDebug")
			press(button:"u",eventId:"upgradeEvent")
			
			press(button:"escape", eventId:"gotoMenu")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			press(button:"right", eventId:"rightClick")
			
			press(button:"left", eventId:"mouse.leftpressed")
			release(button:"left", eventId:"mouse.leftreleased")
			
			move(eventId:"move") { message ->
				message.x = position.x
				message.y = position.y
			}
			
		}
	}
	
	component(new ExplosionComponent("explosions")) {
		
	}
	
	def selectedTowerParameters = [
			lineColor:utils.color(0.0f, 0.8f, 0.0f,0.5f),
			fillColor:utils.color(0.0f, 0.8f, 0.0f,0.25f)
			]
	child(entity("selectedTowerEntity"){
		property("selectedEntity", null)
		
		property("position", {entity.selectedEntity.position })
		property("radius", {entity.selectedEntity.radius })
		property("enabled", {entity.selectedEntity != null })
		
		
		component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
			property("lineColor", selectedTowerParameters.lineColor)
			property("fillColor", selectedTowerParameters.fillColor)
			propertyRef("position","position")
			propertyRef("radius","radius")
			propertyRef("enabled","enabled")
		}
		
		component(new DisablerComponent(new ImageRenderableComponent("selectedAuraRenderer"))){
			property("image", utils.resources.image("towerofdefense.images.blasterbullet"))
			property("color", utils.color(1f, 1f, 1f, 1.0f))
			property("direction", utils.vector(1f,0f))
			propertyRef("position", "position")
			propertyRef("enabled", "enabled")
		}
		
		component(utils.components.genericComponent(id:"towerSelectedHandler", messageId:"towerSelected"){ message ->
			entity.selectedEntity = message.tower
		})
	})
	
	child(entity("towerControl"){
		def towerControl = entity
		property("selectedEntity", null)
		
		property("position",utils.vector(400,10))
		
		component(utils.components.genericComponent(id:"towerSelectedHandler", messageId:"towerSelected"){ message ->
			entity.selectedEntity = message.tower		
		})
		
		component(utils.components.genericComponent(id:"upgradeHandler", messageId:"upgradeEvent") {
			
			def selectedEntity = towerControl.selectedEntity
			def world = towerControl.parent
			
			if(selectedEntity==null)
				return
			
			if(!selectedEntity.canUpgrade)
				return
			
			if(selectedEntity.upgradeCost > world.money)
				return
			
			world.money = (float)(world.money - selectedEntity.upgradeCost)
			
			messageQueue.enqueue(utils.genericMessage("upgrade") {upgrademessage ->
				upgrademessage.tower = selectedEntity
			})
		})
		
		component(new Component("enabler"){
					void handleMessage(Message message){
						if(entity.selectedEntity == null)
							message.suspendPropagation()
					}
				})
		
		child(entity("towerinfo"){
			
			property("position",{
				entity.parent.position.copy().add(utils.vector(10,10))
			})
			
			child(entity("levelLabel"){
				component(new LabelComponent("levellabelcomponent")){
					property("position",{
						entity.parent.position.copy().add(utils.vector(75,10))
					})
					property("message", "Level: {0}/7")
					property("value",{towerControl.selectedEntity?.level })
				}	
			})
			
		})
		
		child(entity("upgradecontrol"){
			
			component(new Component("enabler"){
				void handleMessage(Message message){
					if(entity.parent.selectedEntity.levels.isEmpty())
						message.suspendPropagation()
				}
			})
			
			child(entity("button-upgrade"){
				parent("towerofdefense.entities.button",[
				position:utils.vector(450, towerButtonsY),
				rectangle:buttonRectangle,
				icon:utils.resources.image("towerofdefense.images.upgrade_icon"),
				mouseNotOverFillColor:utils.color(0.0f, 0.0f, 1.0f, 0.4f),
				mouseOverFillColor:utils.color(0.0f, 0.0f, 1.0f, 0.7f),
				trigger:utils.custom.triggers.genericMessage("upgradeEvent") {
				},
				enabled:{
					def selectedEntity = towerControl.selectedEntity
					
					if(selectedEntity==null)
						return false
					
					if(!selectedEntity.canUpgrade)
						return false
					
					def world = towerControl.parent
					
					if(selectedEntity.upgradeCost > world.money)
						return false
					
					return true
				}
				])
			})
			
			child(entity("upgradeCostLabel"){
				component(new LabelComponent("upgradeCostLabelLabel")){
					property("position",utils.vector(450,towerButtonsY+35))
					property("message", "\${0,number,integer}".toString())
					property("value",{towerControl.selectedEntity?.upgradeCost ?: 0 })
				}	
			})
		})
		
		child(entity("sellcontrol") {
			
			child(entity("button-sell"){
				parent("towerofdefense.entities.button",[
				position:utils.vector(520, towerButtonsY),
				rectangle:buttonRectangle,
				icon:utils.resources.image("towerofdefense.images.sell_icon"),
				mouseNotOverFillColor:utils.color(0.0f, 0.0f, 1.0f, 0.4f),
				mouseOverFillColor:utils.color(0.0f, 0.0f, 1.0f, 0.7f),
				
				trigger:utils.custom.triggers.closureTrigger {
					def player = towerControl.parent
					def selectedEntity = towerControl.selectedEntity
					
					def sellCost = selectedEntity.sellCost
					
					player.money = (float) (player.money + sellCost)
					
					messageQueue.enqueue(utils.genericMessage("towerSelected") {upgrademessage ->
						upgrademessage.tower = null
					})
					
					messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(selectedEntity))
				},
				enabled:{
					def selectedEntity = towerControl.selectedEntity
					
					if(selectedEntity==null)
						return false
					
					if(selectedEntity.upgrading)
						return false
					
					if(!selectedEntity.sellCost)
						return false
					
					return true
				}
				])
			})
			
			child(entity("sellCostLabel"){
				component(new LabelComponent("label")){
					property("position",utils.vector(520,towerButtonsY+35))
					property("message", "\${0,number,integer}".toString())
					property("value",{towerControl.selectedEntity?.sellCost ?: 0 })
				}	
			})
		})
		
	})
	
}