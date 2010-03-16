package towerofdefense.scenes;


import com.gemserk.componentsengine.commons.components.DisablerComponent;

import towerofdefense.components.GridRenderer;
import towerofdefense.components.TowerDeployer 
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.timers.CountDownTimer 
import com.gemserk.componentsengine.timers.PeriodicTimer 
import towerofdefense.GroovyBootstrapper;
import com.gemserk.componentsengine.commons.components.*;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.towerofdefense.*;
import com.gemserk.games.towerofdefense.components.*;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity("world") {
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
	
	component(new GridRenderer("grid")){
		propertyRef("bounds","gameBoundsToRender")
		property("distance",gridDistance)
	}
	
	property("path",parameters.path)
	
	component(new PathRendererComponent("pathrenderer")){
		property("lineColor", utils.color(0.2f, 0.2f, 0.7f, 1f))
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
	
	def mapeo = [
			"candeploy":utils.color(0.0f, 0.8f, 0.0f,0.25f),
			"cantdeploy":utils.color(0.8f, 0.0f, 0.0f,0.25f)
			]
	
	component(new DisablerComponent(new CircleRenderableComponent("circlerenderer"))){
		property("lineColor", utils.color(0.5f, 0.5f, 0.5f, 0.1f))
		property("radius", {
			entity.towers[(entity.towerType)].radius
		})
		property("fillColor", {
			mapeo[(entity.deployCursorState)]
		})
		propertyRef("position", "mousePosition")
		propertyRef("enabled", "deployTowerEnabled")
	}
	
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
			mouseNotOverFillColor=utils.color(0.0f, 1.0f, 0.0f, 0.1f)
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
		utils.custom.game.loadScene(parameters.sceneScript);
	})
	
	
	//	component(utils.components.genericComponent(id:"upgradeGUIHandler", messageId:"upgradeGUI"){ message ->
	//		def selectedTower = entity.selectedTower
	//		if(selectedTower == null)
	//			return
	//		
	//		if(selectedTower.levels.isEmpty()){
	//			println "Cant upgrade tower"
	//			return
	//		}
	//		
	//		messageQueue.enqueue(utils.genericMessage("upgrade") {upgrademessage ->
	//			upgrademessage.tower = selectedTower
	//		})
	//	})
	
	input("inputmapping"){
		keyboard {
			press(button:"w", eventId:"nextWave")
			press(button:"r", eventId:"reloadScene")
			press(button:"m",eventId:"cheatMoney")
			press(button:"l",eventId:"cheatLives")
			press(button:"d",eventId:"dumpDebug")
			//			press(button:"u",eventId:"upgradeGUI")
			
			press(button:"escape", eventId:"gotoMenu")
		}
		mouse {
			
			press(button:"left", eventId:"click")
			press(button:"right", eventId:"rightClick")
			
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
		property("selectedEntity", null)
		
		
		
		
		component(utils.components.genericComponent(id:"towerSelectedHandler", messageId:"towerSelected"){ message ->
			def hadSelected = entity.selectedEntity != null
			entity.selectedEntity = message.tower
			if(message.tower != null){
				if(!hadSelected){
					entity.childrenEntities.each { entityToAdd ->
						messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(entityToAdd,entity))
					}
				}
			} else {
				if(hadSelected){
					entity.childrenEntities.each { entityToRemove ->
						messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(entityToRemove))
					}
				}
			}
			
		})
		
		property("childrenEntities",[entity("button-upgrade"){
			parent("towerofdefense.entities.button",[
			position:utils.vector(450, towerButtonsY),
			rectangle:buttonRectangle,
			icon:utils.resources.image("towerofdefense.images.upgrade_icon"),
			mouseNotOverFillColor:utils.color(0.0f, 0.0f, 1.0f, 0.4f),
			mouseOverFillColor:utils.color(0.0f, 0.0f, 1.0f, 0.7f),
			trigger:utils.custom.triggers.closureTrigger {
				def entity = entity
				def selectedTower = entity.parent.selectedEntity
				if(selectedTower == null)
					return
				
				if(selectedTower.levels.isEmpty()){
					println "Cant upgrade tower"
					return
				}
				
				messageQueue.enqueue(utils.genericMessage("upgrade") {upgrademessage ->
					upgrademessage.tower = selectedTower
				})
			},
			enabled:{!entity.parent.selectedEntity.levels.isEmpty()}
			])
		}
		])
		
		
	})
	
	
}