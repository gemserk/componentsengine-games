
package towerofdefense.scenes;

builder.entity {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(name:"Shoulda Zagged",money:20.0f,lives:10){
		path(minX:0,minY:30){
			point(91.09998f,600.0f)
			point(91.09998f,346.5f)
			point(252.59999f,507.0f)
			point(419.19998f,355.5f)
			point(584.1f,508.5f)
			point(584.1f,213.0f)
			point(426.0f,205.5f)
			point(261.09998f,352.5f)
			point(92.79998f,211.5f)
		}
		critters(rewardFactor:[0.5f],healthFactor:[1.0f]){
			critter(type:"chomper",id:"chomper",speed:50.0f,health:50.0f)
			critter(type:"spinner",id:"spinner",speed:50.0f,health:50.0f)
			critter(type:"wiggle",id:"wiggle",speed:30.0f,health:50.0f)
			critter(type:"star",id:"star",speed:70.0f,health:25.0f)
		}
		waves(delayBetweenWaves:20000,delayBetweenSpawns:1000){
			wave(id:"chomper",quantity:4)
			wave(id:"spinner",quantity:8)
			wave(id:"wiggle",quantity:16)
			wave(id:"star",quantity:24)
			wave(id:"chomper",quantity:32)
			wave(id:"chomper",quantity:4)
			wave(id:"spinner",quantity:8)
			wave(id:"wiggle",quantity:16)
			wave(id:"star",quantity:24)
			wave(id:"chomper",quantity:32)
			wave(id:"chomper",quantity:4)
			wave(id:"spinner",quantity:8)
			wave(id:"wiggle",quantity:16)
			wave(id:"star",quantity:24)
			wave(id:"chomper",quantity:32)
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
