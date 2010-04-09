package towerofdefense.scenes;

builder.entity {
		
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(money:30f, lives:15){
		path(minX=0,minY=30) {
			point(100, 570)		      
			point(100, 450) 		      
			point(100, 300)
			point(300, 150)		      
			point(500, 150)		      
			point(620, 300) 		      
			point(500, 500)  		      
			point(500, 500) 		      
			point(350, 450)
		}
		
		critters(rewardFactor:[1f], healthFactor:[1.7f]){
			critter(type:"chomper", health:70f, speed:20f)
			critter(type:"spinner",health:70f, speed:20f)
			critter(type:"wiggle", health:70f, speed:20f)
			critter(type:"star", health:70f, speed:20f)
		}
		waves(delayBetweenWaves:20000, delayBetweenSpawns:1000){
			wave(quantity:6, id:"chomper")
			wave {
				wave(quantity:2, id:"spinner")
				wave(quantity:2, id:"chomper")
				wave(quantity:2, id:"spinner")
			}
			wave(quantity:6, id:"wiggle")
			wave {
				wave(quantity:2, id:"spinner")
				wave(quantity:2, id:"star")
				wave(quantity:2, id:"spinner")
			}
			wave {
				wave(quantity:1, id:"chomper")
				wave(quantity:1, id:"wiggle")
				wave(quantity:1, id:"chomper")
				wave(quantity:1, id:"wiggle")
				wave(quantity:1, id:"chomper")
				wave(quantity:1, id:"wiggle")
			}
			wave(quantity:6, id:"star")
		}
		
		towers{
			tower(type:"blaster",cost:10f)
		}
		
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	
	parent("towerofdefense.scenes.game", builtParameters)		
}