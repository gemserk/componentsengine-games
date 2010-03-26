package towerofdefense.scenes;

builder.entity("world") {
		
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(money:30f, lives:15){
		path(minX:0,minY:30) {
			point(0, 150)		 
			point(100, 150)		      
			point(500, 150) 		      
			point(500, 350)
			point(350, 350)		      
			point(350, 450)		      
		}
		
		critters(rewardFactor:[1f], healthFactor:[1f]){
			critter(type:"chomper", health:60f, speed:30f)
		}
		waves(delayBetweenWaves:30000, delayBetweenSpawns:4000){
			wave(quantity:6, id:"chomper")
			wave(quantity:6, id:"chomper")
			wave(quantity:6, id:"chomper")
		}
		
		towers{
			tower(type:"laser",cost:15f)
		}
		
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	
	parent("towerofdefense.scenes.game", builtParameters)		
}