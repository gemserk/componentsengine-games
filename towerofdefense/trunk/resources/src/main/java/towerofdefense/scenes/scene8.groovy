
package towerofdefense.scenes;

builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(name:"Soppy Joe",money:45.0f,lives:10){
		path(minX:0,minY:30){
			point(164.19998f,612.0f)
			point(121.69998f,553.5f)
			point(113.19998f,520.5f)
			point(119.999985f,429.0f)
			point(150.59998f,390.0f)
			point(181.19998f,295.5f)
			point(262.8f,304.5f)
			point(307.0f,408.0f)
			point(337.59998f,390.0f)
			point(414.09998f,442.5f)
			point(432.8f,339.0f)
			point(403.9f,252.0f)
			point(361.4f,214.5f)
			point(230.49998f,196.5f)
			point(148.89998f,207.0f)
			point(94.49998f,246.0f)
			point(75.79998f,282.0f)
			point(85.99998f,357.0f)
			point(174.39998f,451.5f)
			point(259.4f,483.0f)
			point(320.59998f,531.0f)
			point(395.4f,526.5f)
			point(473.59998f,474.0f)
			point(524.6f,366.0f)
			point(606.2f,250.5f)
		}
		critters(rewardFactor:[1.0f],healthFactor:[1.25f]){
			critter(type:"chomper",id:"chomper",speed:40.0f,health:100.0f)
			critter(type:"spinner",id:"spinner",speed:40.0f,health:100.0f)
			critter(type:"wiggle",id:"wiggle",speed:20.0f,health:150.0f)
			critter(type:"star",id:"star",speed:60.0f,health:50.0f)
			critter(type:"cubic",id:"cubic",speed:30.0f,health:70.0f)
		}
		waves(delayBetweenWaves:20000,delayBetweenSpawns:1000){
			wave(id:"chomper",quantity:8)
			wave(id:"spinner",quantity:8)
			wave(id:"wiggle",quantity:8)
			wave(id:"star",quantity:8)
			wave(id:"chomper",quantity:8)
			wave(id:"star",quantity:8)
			wave(id:"wiggle",quantity:8)
			wave(id:"star",quantity:10)
			wave(id:"chomper",quantity:15)
			wave(id:"wiggle",quantity:20)
		}
		towers {
			tower(type:"blaster",cost:10.0f)
			tower(type:"laser",cost:15.0f)
			tower(type:"missile",cost:20.0f)
		}
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	
	parent("towerofdefense.scenes.game", builtParameters)		
}
