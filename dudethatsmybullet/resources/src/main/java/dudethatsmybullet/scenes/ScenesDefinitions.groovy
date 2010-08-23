package dudethatsmybullet.scenes

class ScenesDefinitions {
	
	static def scenes(def utils){
		
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
		
		
		def levelIntro = [
				hero: basicHero, 
				turrets: [
				basicTurret + [position:utils.vector(200,300), reloadTime: 500],
				basicTurret + [position:utils.vector(600,300), reloadTime: 500],
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
		
		
		def levels = [levelIntro,levelCross,levelSquare]
		return levels
	}
}
