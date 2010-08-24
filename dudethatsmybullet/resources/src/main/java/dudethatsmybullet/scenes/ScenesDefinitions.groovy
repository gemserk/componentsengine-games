package dudethatsmybullet.scenes

import org.newdawn.slick.geom.Vector2f 

class ScenesDefinitions {
	
	static def scenes(def utils){
		
		def geometricPositions = { Vector2f center, int quantity ->
			def angles = (0..(quantity-1)).collect { return (float)it * 360f/quantity	}
			angles.collect { angle -> center.copy().add(utils.vector(0,-200).add(angle))}
		}
		
		
		
		def calcShieldValues = { secondsToDischarge, secondsToRecharge ->
			def maxShield = 10000f
			def discharge = (float)maxShield/(secondsToDischarge * 1000f)
			def recharge = (float)maxShield/(secondsToRecharge * 1000f)
			return [maxShield: maxShield,
				shieldDischargeRate: discharge,
				shieldRechargeRate: recharge,]
		}
		
		
		
		def basicTurret = [
				damage: 25f,
				reloadTime: 300,
				fireRadius: 150f,
				hitpoints: 100f,
				]
		
		def basicHero = [
				hitpoints: 250f,
				maxShield: 150f,
				] + calcShieldValues(1.5f, 70f)
		
		
		def levelLine = [
				hero: basicHero, 
				turrets: [
				basicTurret + [position:utils.vector(200,300), reloadTime: 500],
				basicTurret + [position:utils.vector(600,300), reloadTime: 500],
				]
				]
		
		def levelTriangle = [
			hero: basicHero,
			turrets: [
			basicTurret + [position:utils.vector(150,500), reloadTime: 400],
			basicTurret + [position:utils.vector(650,500), reloadTime: 400],
			basicTurret + [position:utils.vector(400,100), reloadTime: 400],
			]
			]
		
		
		def levelCross = [
				hero: basicHero, 
				turrets: [
				basicTurret + [position:utils.vector(200,300)],
				basicTurret + [position:utils.vector(600,300)],
				basicTurret + [position:utils.vector(400,150)],
				basicTurret + [position:utils.vector(400,450)],
				]
				]
		
		def levelSquare = [
				hero: basicHero, 
				turrets: [
				basicTurret + [position:utils.vector(200,150)],
				basicTurret + [position:utils.vector(200,450)],
				basicTurret + [position:utils.vector(600,450)],
				basicTurret + [position:utils.vector(600,150)],
				]
				]
		
		
		def levelDinamic = [
			hero: basicHero ,
			turrets: geometricPositions(utils.vector(400,330),20).collect { basicTurret + [position: it ] }
			]
		
		
		//def levels = [levelLine, levelTriangle,levelSquare,levelCross,levelDinamic,]
		return (2..10).collect { quantity ->
			def generatedLevel = [
				hero: basicHero ,
				turrets: geometricPositions(utils.vector(400,330),quantity).collect { basicTurret + [position: it ] }
				]
				
			return generatedLevel
		}
		
		//return levels
	}
}
