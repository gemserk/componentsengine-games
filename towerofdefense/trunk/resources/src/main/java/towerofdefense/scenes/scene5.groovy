
package towerofdefense.scenes;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(name:"The Beat",money:70.0f,lives:10){
		path(minX:0,minY:30){
			point(341.0f,619.5f)
			point(341.0f,507.0f)
			point(509.3f,508.5f)
			point(509.3f,358.5f)
			point(91.09998f,358.5f)
			point(92.79998f,216.0f)
			point(594.3f,213.0f)
		}
		critters(rewardFactor:[1.5f],healthFactor:[1.0f]){
			critter(type:"chomper",id:"chomper",speed:50.0f,health:125.0f)
			critter(type:"spinner",id:"spinner",speed:50.0f,health:125.0f)
			critter(type:"wiggle",id:"wiggle",speed:30.0f,health:150.0f)
			critter(type:"star",id:"star",speed:70.0f,health:75.0f)
		}
		waves(delayBetweenWaves:25000,delayBetweenSpawns:1000){
			wave(id:"chomper",quantity:8)
			wave(id:"spinner",quantity:8)
			wave(id:"wiggle",quantity:8)
			wave(id:"star",quantity:8)
			wave(id:"chomper",quantity:8)
			wave(id:"star",quantity:8)
			wave(id:"wiggle",quantity:8)
			wave(id:"star",quantity:8)
			wave(id:"chomper",quantity:8)
			wave(id:"wiggle",quantity:5)
			wave {
				wave(id:"star",quantity:5)
				wave(id:"wiggle",quantity:1)
				wave(id:"star",quantity:5)
				wave(id:"wiggle",quantity:1)
				wave(id:"star",quantity:5)
				wave(id:"wiggle",quantity:1)
				wave(id:"star",quantity:5)
				wave(id:"wiggle",quantity:1)
				wave(id:"star",quantity:5)
			}
		}
		towers {
			tower(type:"blaster",cost:10.0f)
			tower(type:"laser",cost:15.0f)
			tower(type:"missile",cost:20.0f)
			tower(type:"shock",cost:15.0f)
		}
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	
	parent("towerofdefense.scenes.game", builtParameters)		
}
