package towerofdefense.scenes;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	
	def builtParameters = sceneBuilder.scene(money:45f, lives:10){
		path(minX=0,minY=30) {

			def invertpoint = { x, y ->
				point((float)(800f - (1.7f*y)), (float)(600f - (1.5f*x)))
			}

			invertpoint(200,490)
			invertpoint(200,145)
			invertpoint(48,145)
			invertpoint(48,316)
			invertpoint(266,316)
			invertpoint(266,226)
			invertpoint(90,226)
			invertpoint(90,100)
		}
		
		critters(rewardFactor:[1f], healthFactor:[1.25f]){
			critter(type:"chomper", health:100f, speed:40f)
			critter(type:"spinner",health:100f, speed:40f)
			critter(type:"wiggle", health:150f, speed:20f)
			critter(type:"star", health:50f, speed:60f)
		}
		
		waves(delayBetweenWaves:30000, delayBetweenSpawns:1000){
			wave(id:"chomper", quantity:8)
			wave(id:"spinner", quantity:8)
			wave(id:"wiggle", quantity:8)
			wave(id:"star", quantity:8)
			wave(id:"chomper", quantity:8)
			wave(id:"star", quantity:8)
			wave(id:"wiggle", quantity:8)
			wave(id:"star", quantity:10)
			wave(id:"chomper", quantity:15)
			wave(id:"wiggle", quantity:20)
		}
		
		towers{
			tower(type:"blaster",cost:10f)
			tower(type:"laser",cost:15f)
		}
		
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	parent("towerofdefense.scenes.game", builtParameters)		
	
}