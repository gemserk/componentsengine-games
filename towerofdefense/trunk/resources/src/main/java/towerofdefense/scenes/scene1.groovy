package towerofdefense.scenes;

import com.gemserk.games.towerofdefense.waves.CompositeWave 
import com.gemserk.games.towerofdefense.waves.SimpleWave;

import com.gemserk.games.towerofdefense.Path;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.Path;

builder.entity("world") {
	
	def utils = utils
	
	parameters.money = 15
	parameters.lives = 15
	parameters.wavePeriod = 15000
	
	def defx = 0
	def defy = 30	
	
	parameters.path=new Path([
			utils.vector(0, 450 + defy), 		      
			utils.vector(100, 450 + defy), 		      
			utils.vector(100, 300 + defy), 
			utils.vector(300, 150 + defy), 		      
			utils.vector(500, 150 + defy), 		      
			utils.vector(620, 300 + defy), 		      
			utils.vector(500, 500 + defy), 		      
			utils.vector(500, 500 + defy), 		      
			utils.vector(350, 450 + defy)
			])
	
	
	
	
	parameters.basePosition=utils.vector(350, 450 + defy)
	parameters.baseRadius=30f
	
	
	
	def critters = new CrittersDefinition(utils)
	
	parameters.waves=[new CompositeWave([new SimpleWave(1000,2,critters.critter("chomper")), new SimpleWave(1200,2,critters.critter("spinner"))]), 
			new CompositeWave([new SimpleWave(800,4,critters.critter("chomper")), new SimpleWave(1200,4,critters.critter("spinner"))])]
	
	
	def towerDefinitions = new TowersDefinitions(utils)
	
	def allTowers = ["blaster":towerDefinitions.tower("blaster"),"laser":towerDefinitions.tower("laser")]
	
	allTowers["blaster"].cost = 2
	
	parameters.towerDescriptions = allTowers
	
	
	parameters.sceneScript = "towerofdefense.scenes.scene1"
	
	
	parent("towerofdefense.scenes.game", parameters)
}