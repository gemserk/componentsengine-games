package towerofdefense.scenes;

import com.gemserk.games.towerofdefense.waves.CompositeWave 
import com.gemserk.games.towerofdefense.waves.SimpleWave;

import com.gemserk.games.towerofdefense.Path;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.Path;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(money:15, lives:15, wavePeriod:15000){
		path(minX=0,minY=30) {
			point(0, 450)		      
			point(100, 450) 		      
			point(100, 300)
			point(300, 150)		      
			point(500, 150)		      
			point(620, 300) 		      
			point(500, 500)  		      
			point(500, 500) 		      
			point(350, 450)
		}
		critters{
			critter(type:"chomper",health:120f, speed:80f)
			critter(type:"spinner",health:12f, speed:10f)
			critter(type:"chomper",id:"chomper2",health:12f, speed:200f)
		}
		
		
		waves{
			wave(rate:1000, quantity:2, id:"chomper")
			wave {
				wave(rate:1000, quantity:2, id:"chomper")
				wave(rate:1000, quantity:2, id:"spinner")
				wave(rate:1000, quantity:2, id:"chomper2")
			}
		}
		
		towers{
			tower(type:"blaster",cost:5)
			tower(type:"laser",cost:7)
		}
		
	}

	builtParameters.sceneScript = this.getClass().getName()

	
	parent("towerofdefense.scenes.game", builtParameters)		
}