
package towerofdefense.scenes;

builder.entity("world") {
		
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(name:"Time will tell...",money:30.0f,lives:15){
  path(minX:0,minY:30){
    point(312.09998f,612.0f)
    point(313.8f,358.5f)
    point(221.99998f,436.5f)
    point(148.89998f,435.0f)
    point(62.199978f,360.0f)
    point(62.199978f,285.0f)
    point(143.79999f,211.5f)
    point(227.09999f,211.5f)
    point(312.09998f,285.0f)
    point(422.59998f,285.0f)
    point(507.6f,210.0f)
    point(592.6f,211.5f)
    point(672.5f,285.0f)
    point(674.2f,357.0f)
    point(596.0f,432.0f)
    point(509.3f,435.0f)
    point(427.69998f,358.5f)
    point(426.0f,507.0f)
  }
  critters(speedFactor:[0.0f],rewardFactor:[4.0f],healthFactor:[2.0f]){
    critter(type:"chomper",id:"chomper",speed:50.0f,health:50.0f)
    critter(type:"spinner",id:"spinner",speed:50.0f,health:50.0f)
    critter(type:"wiggle",id:"wiggle",speed:50.0f,health:50.0f)
    critter(type:"star",id:"star",speed:75.0f,health:50.0f)
  }
  waves(delayBetweenWaves:10000,delayBetweenSpawns:1000){
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"spinner",quantity:1)
    }
    wave {
      wave(id:"chomper",quantity:2)
      wave(id:"star",quantity:1)
    }
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
