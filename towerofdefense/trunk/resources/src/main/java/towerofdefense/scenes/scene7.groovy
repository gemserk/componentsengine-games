
package towerofdefense.scenes;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(name:"Ascension",money:60.0f,lives:10){
		path(minX:0,minY:30){
			point(426.0f,627.0f)
			point(427.69998f,354.0f)
			point(259.4f,355.5f)
			point(257.69998f,505.5f)
			point(546.7f,508.5f)
			point(545.0f,282.0f)
			point(341.0f,285.0f)
			point(339.3f,433.5f)
			point(169.29999f,432.0f)
			point(172.69998f,211.5f)
			point(543.3f,211.5f)
		}
		critters(rewardFactor:[1.0f],healthFactor:[1.0f]){
			critter(type:"chomper",id:"chomper",speed:70.0f,health:80.0f)
			critter(type:"spinner",id:"spinner",speed:70.0f,health:80.0f)
			critter(type:"wiggle",id:"wiggle",speed:50.0f,health:100.0f)
			critter(type:"star",id:"star",speed:100.0f,health:35.0f)
		}
		waves(delayBetweenWaves:10000,delayBetweenSpawns:1000){
			wave(id:"chomper",quantity:1)
			wave(id:"spinner",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"spinner",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"spinner",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"spinner",quantity:1)
			wave(id:"wiggle",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"chomper",quantity:1)
			wave(id:"star",quantity:1)
			wave(id:"wiggle",quantity:2)
			wave(id:"star",quantity:3)
			wave(id:"chomper",quantity:4)
			wave(id:"wiggle",quantity:5)
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
