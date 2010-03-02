package towerofdefense.scenes;



builder.entity("world") {
	
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder.scene(money:30, lives:15, wavePeriod:20000){
		path(minX=0,minY=30) {
			point(0, 450)		      
			point(100, 450) 		      
			point(100, 300)
			point(300, 150)		      
			point(500, 150)		      
			point(620, 300) 		      
			point(500, 500)  		      
			point(500, 500) 		      
			point(350, 450)
		}
		
		critters{
			critter(type:"chomper", health:70f, speed:20f)
			critter(type:"spinner",health:70f, speed:20f)
			critter(type:"wiggle", health:70f, speed:20f)
			critter(type:"star", health:70f, speed:20f)
		}
		
		waves{
			wave(rate:1000, quantity:6, id:"chomper")
			wave {
				wave(rate:1000, quantity:2, id:"spinner")
				wave(rate:1000, quantity:2, id:"chomper")
				wave(rate:1000, quantity:2, id:"spinner")
			}
			wave(rate:1000, quantity:6, id:"wiggle")
			wave {
				wave(rate:1000, quantity:2, id:"spinner")
				wave(rate:1000, quantity:2, id:"star")
				wave(rate:1000, quantity:2, id:"spinner")
			}
			wave {
				wave(rate:1000, quantity:1, id:"chomper")
				wave(rate:1000, quantity:1, id:"wiggle")
				wave(rate:1000, quantity:1, id:"chomper")
				wave(rate:1000, quantity:1, id:"wiggle")
				wave(rate:1000, quantity:1, id:"chomper")
				wave(rate:1000, quantity:1, id:"wiggle")
			}
			wave(rate:1000, quantity:6, id:"star")
		}
		
		towers{
			tower(type:"blaster",cost:10)
			tower(type:"laser",cost:15)
		}
		
	}
	
	builtParameters.sceneScript = this.getClass().getName()
	
	
	parent("towerofdefense.scenes.game", builtParameters)		
}