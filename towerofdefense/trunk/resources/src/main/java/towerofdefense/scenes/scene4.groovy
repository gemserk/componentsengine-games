package towerofdefense.scenes;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	
	def builtParameters = sceneBuilder.scene(money:45f, lives:10){
		path(minX=0,minY=30) {
			
			def invertpoint = { x, y ->
				point((float)(800f - (1.7f*y)), (float)(600f - (1.5f*x)))
			}
			
			invertpoint(330,476)
			invertpoint(224,367)
			invertpoint(60,367)
			invertpoint(60,172)
			invertpoint(101,110)
			invertpoint(143,110)
			invertpoint(161,220)
			invertpoint(161,415)
		}
		
		critters(rewardFactor:[1f], healthFactor:[1.25f]){
			critter(type:"chomper", speed:40f, health:125f)
			critter(type:"spinner", speed:40f, health:125f)
			critter(type:"wiggle", speed:20f, health:150f)
			critter(type:"star", speed:65f, health:75f)
		}
		
		waves(delayBetweenWaves:35000, delayBetweenSpawns:1000){
			wave(id:"chomper", quantity:8)
			wave(id:"spinner", quantity:8)
			wave(id:"wiggle", quantity:8)
			wave(id:"star", quantity:8)
			wave(id:"chomper", quantity:8)
			wave(id:"star", quantity:8)
			wave(id:"wiggle", quantity:8)
			wave(id:"star", quantity:8)
			wave(id:"chomper", quantity:8)
			wave(id:"wiggle", quantity:5)
			wave {
				wave(id:"star", quantity:5)
				wave(id:"wiggle", quantity:1)
				wave(id:"star", quantity:5)
				wave(id:"wiggle", quantity:1)
				wave(id:"star", quantity:5)
				wave(id:"wiggle", quantity:1)
				wave(id:"star", quantity:5)
				wave(id:"wiggle", quantity:1)
				wave(id:"star", quantity:5)
			}
		}
		
		towers{
			tower(type:"blaster",cost:10f)
			tower(type:"laser",cost:15f)
			tower(type:"missile",cost:20f)
			tower(type:"shock",cost:15f)
		}
		
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	parent("towerofdefense.scenes.game", builtParameters)		
	
}