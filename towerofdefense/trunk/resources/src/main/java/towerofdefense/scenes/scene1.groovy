package towerofdefense.scenes;
import com.gemserk.games.towerofdefense.Path;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.Path;
import com.gemserk.games.towerofdefense.waves.Wave;

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
	
	parameters.waves=[new Wave(1000,10,new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
			utils.custom.genericprovider.provide{ entity ->
				[
				position:entity.position.copy(),
				maxVelocity:0.05f,
				path:{entity.parent.path},
				color:utils.color(1.0f, 0.5f, 0.5f, 0.95f),
				health:utils.container(8,8),
				points: 5,
				reward:1			
				]	
			}	
			)), new Wave(1200,5,new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
			utils.custom.genericprovider.provide{ entity ->
				[
				position:entity.position.copy(),
				maxVelocity:0.07f,
				path:{entity.parent.path},
				color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
				health:utils.container(12,12),
				points: 10,
				reward:2
				]	
			}	
			)), new Wave(2500,5,new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
			utils.custom.genericprovider.provide{ entity ->
				[
				position:entity.position.copy(),
				maxVelocity:0.02f,
				path:{entity.parent.path},
				color:utils.color(0.0f, 1.0f, 0.0f, 1.0f),
				health:utils.container(20,20),
				points: 15,
				reward:3
				]	
			}	
			)), new Wave(200,1000,new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
			utils.custom.genericprovider.provide{ entity ->
				[
				position:entity.position.copy(),
				maxVelocity:0.09f,
				path:{entity.parent.path},
				color:utils.color(0.0f, 0.0f, 1.0f, 1.0f),
				health:utils.container(15,15),
				points: 20,
				reward:4
				]	
			}	
			))]
	
	
	def blastTower = new InstantiationTemplateImpl(
			
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.blastertower"),
			utils.custom.genericprovider.provide{ position ->
				[
				position:position,
				direction:utils.vector(-1,0),
				radius:52f,
				lineColor:utils.color(0.0f, 0.8f, 0.0f,0.5f),
				fillColor:utils.color(0.0f, 0.8f, 0.0f,0.25f),
				color:utils.color(0.2f, 1.0f, 0.2f, 1.0f),
				template:"towerofdefense.entities.bullet",
				reloadTime:250,
				cost:5,
				instanceParameters: utils.custom.genericprovider.provide{
					[
					damage:1.0f,
					radius:10.0f,
					maxVelocity:0.6f,
					color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
					]
				}	
				]
			})
	
	def laserTower = new InstantiationTemplateImpl(
			utils.custom.templateProvider.getTemplate("towerofdefense.entities.lasertower"),
			utils.custom.genericprovider.provide{ position ->
				[
				position:position,
				direction:utils.vector(-1,0),
				radius:200f,
				lineColor:utils.color(0.0f, 0.0f, 0.8f,0.5f),
				fillColor:utils.color(0.0f, 0.0f, 0.8f,0.25f),
				color:utils.color(0.2f, 0.2f, 1.0f, 1.0f),
				reloadTime:250,
				cost:7
				]
			})
	
	
	parameters.towerDescriptions = [blaster:[icon:"towerofdefense.images.blastertower_icon", cost:5, instantiationTemplate:blastTower], 
			laser:[icon:"towerofdefense.images.lasertower_icon", cost:7, instantiationTemplate:laserTower]]
	
	//como mierda hago esto
	//	genericComponent(id:"reloadSceneHandler", messageId:"reloadScene"){ message ->
	//		utils.custom.game.loadScene("towerofdefense.scenes.game");
	//	}
	parent("towerofdefense.scenes.game", parameters)
}