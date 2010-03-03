package towerofdefense.scenes;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	
	def builtParameters = sceneBuilder.scene(money:50, lives:10){
		path(minX=0,minY=30) {

			def invertpoint = { x, y ->
				point((float)(800f - (1.7f*y)), (float)(600f - (1.5f*x)))
			}

			invertpoint(0,401)
			invertpoint(170,401)
			invertpoint(179,306)
			invertpoint(215,290)
			invertpoint(252,354)
			invertpoint(285,313)
			invertpoint(285,212)
			invertpoint(243,116)
			invertpoint(149,203)
			invertpoint(87,118)
			invertpoint(37,204)
			invertpoint(78,286)
		}
		
		critters(rewardFactor:[1.5f], healthFactor:[1f]){
			critter(type:"chomper", health:100f, speed:40f)
			critter(type:"spinner",health:100f, speed:40f)
			critter(type:"wiggle", health:150f, speed:20f)
			critter(type:"star", health:50f, speed:60f)
		}
		
		waves(delayBetweenWaves:30000, delayBetweenSpawns:1000){
			wave(id:"chomper" , quantity:3)
			wave(id:"spinner" , quantity:4)
			wave(id:"wiggle" , quantity:5)
			wave(id:"star" , quantity:6)
			wave(id:"chomper" , quantity:7)
			wave(id:"star" , quantity:8)
			wave(id:"wiggle" , quantity:9)
			wave(id:"star" , quantity:10)
		}
		
		towers{
			tower(type:"blaster",cost:10)
			tower(type:"laser",cost:15)
		}
		
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	parent("towerofdefense.scenes.game", builtParameters)		
	
}