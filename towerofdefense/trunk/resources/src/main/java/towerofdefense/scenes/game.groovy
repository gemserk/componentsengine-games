package towerofdefense.scenes;
import com.gemserk.games.towerofdefense.Path;

import com.gemserk.games.towerofdefense.PathRendererComponent;

import com.gemserk.componentsengine.messages.SlickRenderMessage;

import com.gemserk.componentsengine.messages.UpdateMessage;


import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gemserk.componentsengine.messages.GenericMessage;


import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import towerofdefense.GroovyBootstrapper;
import towerofdefense.components.TowerDeployer;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.DisablerComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.towerofdefense.ComponentFromListOfClosures;
import com.gemserk.games.towerofdefense.LabelComponent;
import com.gemserk.games.towerofdefense.components.GuiLogicComponent;
import com.gemserk.games.towerofdefense.components.OutOfBoundsRemover;
import com.gemserk.games.towerofdefense.components.render.RectangleRendererComponent;
import com.gemserk.games.towerofdefense.components.TimerComponent;
import com.gemserk.games.towerofdefense.timers.PeriodicTimer;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity("world") {
	def utils = utils
	new GroovyBootstrapper();
	
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
	
	property("path",parameters.path)
	                		
	component(new PathRendererComponent("pathrenderer")){
		property("lineColor", utils.color(0.2f, 0.2f, 0.7f, 1.0f))
		property("lineWidth", 20.0f)
		propertyRef("path", "path")		
	}                		
	
	child(template:"towerofdefense.entities.base", id:"base")	{
		position=parameters.basePosition
		direction=utils.vector(-1,0)
		radius=parameters.baseRadius
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 0.0f)
		fillColor=utils.color(0.2f, 0.2f, 0.7f, 1.0f)
	}
	
	child(template:"towerofdefense.entities.spawner", id:"spawner")	{
		position={entity.parent.path.getPoint(0)}
		waves=new Waves().setWaves(parameters.waves)
	}
	
	property("towerDescriptions", parameters.towerDescriptions )
	
	

	
	property("towerType","blaster")
	
	component(new TowerDeployer("towerdeployer")){
		property("instantiationTemplate",{entity.towerDescriptions[(entity.towerType)].instantiationTemplate})
		propertyRef("towerCount","towerCount")
	}
	
	genericComponent(id:"critterdeadHandler", messageId:"critterdead"){ message ->
		def reward = message.critter.reward
		def points = message.critter.points
		entity.money+=reward
		entity.points+=points
		
		if(entity.particlesEnabled){
			def particleSystem = entity.particleSystem
			ConfigurableEmitter explosion = ParticleIO.loadEmitter(this.getClass().getClassLoader().getResourceAsStream("assets/particles/creepexplosionemitter.xml"));
			particleSystem.addEmitter(explosion);	
			def critterPos = message.critter.position
			explosion.setPosition(critterPos.x, critterPos.y);
		}
	}
	
	genericComponent(id:"critterReachBaseHandler", messageId:"critterReachBase"){ message ->
		entity.lives--;
		
		if(entity.lives <= 0)
			messageQueue.enqueue(utils.genericMessage("reloadScene",{
			}))
	}
	
	
	genericComponent(id:"reloadSceneHandler", messageId:"reloadScene"){ message ->
		utils.custom.game.loadScene("towerofdefense.scenes.scene1");
	}
	
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
	
	component(new LabelComponent("timerlabel")){
		property("position",utils.vector(labelX,labelY + 40))
		property("message","Timer: {0}")
		property("value",{entity.wavesTimer.timeLeft })
	}
	
	component(new LabelComponent("towerCountlabel")){
		property("position",utils.vector(labelX,labelY + 60))
		property("message","Towers: {0}")
		propertyRef("value","towerCount")
	}	
	
	component(new LabelComponent("towertypelabel")){
		property("position",utils.vector(labelX,labelY + 80))
		property("message","TowerType: {0}")
		propertyRef("value", "towerType")
	}	
	
	component(new OutOfBoundsRemover("outofboundsremover")) {
		property("tags", ["bullet"] as String[] );
		property("bounds", utils.rectangle(0,0, 800, 600));
	}
	
	component(new TimerComponent("wavesTimerComponent")){
		property("messageBuilder",utils.custom.messageBuilderFactory.messageBuilder("nextWave") {
		})
		propertyRef("timer","wavesTimer")
	}
	
	genericComponent(id:"nextWaveMessageHandler", messageId:"nextWave"){ message ->
		entity.wavesTimer.reset()
	}
	
	
	property("deployTower", "deployState")
	
	
	component(new GuiLogicComponent("guiComponent")){
		// propertyRef("state", "guiState")
		propertyRef("mousePosition", "mousePosition")
		propertyRef("deployCursorState", "deployCursorState")
		propertyRef("deployTowerEnabled", "deployTowerEnabled")
		propertyRef("path","path")
		
		propertyRef("towerDescriptions", "towerDescriptions")
		propertyRef("gameBounds","gameBounds")
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
		property("radius", 52.0f)
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
	
	parameters.towerDescriptions.each { key, value -> 
		
		child(template:"towerofdefense.entities.button", id:"button-${key}".toString())	{
			position=utils.vector(towerButtonsX, towerButtonsY)
			rectangle=buttonRectangle
			icon=utils.resources.image(value.icon)
			mouseNotOverFillColor=utils.color(0.0f, 1.0f, 0.0f, 0.4f)
			mouseOverFillColor=utils.color(0.0f, 1.0f, 0.0f, 0.7f)
			messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("deployTowerSelected") {
				message.towerType = "${key}".toString()
			}
		}
		
		component(new LabelComponent("towerCostLabel-${key}".toString())){
			property("position",utils.vector(towerButtonsX,towerButtonsY+35))
			property("message", "\$${value.cost}".toString())
		}	
		
		towerButtonsX+=60
	}
	
	def commandButtonX = 660
	def commandButtonY = towerButtonsY
	
	child(template:"towerofdefense.entities.button", id:"button-nextWave")	{
		position=utils.vector(commandButtonX, commandButtonY)
		rectangle=buttonRectangle
		icon=utils.resources.image("towerofdefense.images.nextwave_icon")
		mouseNotOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.4f)
		mouseOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.7f)
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("nextWave") {
		}
	}
	
	child(template:"towerofdefense.entities.button", id:"button-restart")	{
		position=utils.vector(commandButtonX + 60, commandButtonY)
		rectangle=buttonRectangle
		icon=utils.resources.image("towerofdefense.images.restart_icon")
		mouseNotOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.4f)
		mouseOverFillColor=utils.color(0.0f, 0.0f, 1.0f, 0.7f)
		messageBuilder=utils.custom.messageBuilderFactory.messageBuilder("reloadScene") {
		}
	}
	
	component(new ComponentFromListOfClosures("cheats",[ {GenericMessage message ->
		switch(message.id){
			case "cheatMoney":
			entity.money+=10
			break;
			case "cheatLives":
			entity.lives+=10
			break;
		}
	}
	]))
	
	genericComponent(id:"gotoMenuHandler", messageId:"gotoMenu"){ message ->
		StateBasedGame stateBasedGame = utils.custom.gameStateManager;
		stateBasedGame.enterState(0, new FadeOutTransition(), new FadeInTransition());
	}
	
	property("particlesEnabled",false)
	property("particleSystem", new ParticleSystem("org/newdawn/slick/data/particle.tga", 2000))
	component(new ComponentFromListOfClosures("particleManagerComponent",[ {UpdateMessage message -> entity.particleSystem.update(message.delta)}, {SlickRenderMessage message ->
		entity.particleSystem.render()
	}
	]))
	
	
	
	
	genericComponent(id:"dumpDebugHandler", messageId:"dumpDebug"){ message ->
		Entity.times.entrySet().sort({it.count }).each { entry -> 
			println "$entry.element - $entry.count"
		}
	}                                                                     
	
	input("inputmapping"){
		keyboard {
			press(button:"w", eventId:"nextWave")
			press(button:"r", eventId:"reloadScene")
			press(button:"m",eventId:"cheatMoney")
			press(button:"l",eventId:"cheatLives")
			press(button:"d",eventId:"dumpDebug")
			
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
	
}