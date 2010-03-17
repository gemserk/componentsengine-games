package towerofdefense.scenes;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 




public class TowersDefinitions {
	
	BuilderUtils utils;
	
	def TowersDefinitions(BuilderUtils utils){
		this.utils = utils;
	}
	
	def tower(def type){
		switch (type) {
			case "blaster":
			return blaster()
			case "laser":
			return laser()
			case "missile":
			return missile()
			case "shock":
			return shock()
			default:
			return null;
		}
	}
	
	def completeTurn  = { seconds ->
		return (float) (360 / (seconds*1000))			
	}
	
	def blaster(){
		def blastTower = new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.blastertower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(-1,0),
			color:utils.color(1f, 1f, 1f, 1.0f),
			template:"towerofdefense.entities.bullet",
			turnRate:completeTurn(1),
			instanceParameters: utils.custom.genericprovider.provide{ tower ->
				[
				position:tower.position.copy(),
				// direction:(entity.targetEntity.position.copy().sub(newPosition).normalise()),
				direction:tower.direction.copy(),
				image:utils.resources.image("towerofdefense.images.blasterbullet"),
				damage:tower.damage,
				radius:10.0f,
				maxVelocity:0.6f,
				color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
				]
			},
			levels:[
			[level:1, radius:50f, damage:10f, upgradeCost:5, sellCost:5, reloadTime:200],
			[level:2, radius:55f, damage:15f, upgradeCost:10, sellCost:7], 
			[level:3, radius:60f, damage:20f, upgradeCost:15, sellCost:12],
			[level:4, radius:65f, damage:25f, upgradeCost:20, sellCost:20],
			[level:5, radius:75f, damage:30f, upgradeCost:25, sellCost:30],
			[level:6, radius:80f, damage:35f, upgradeCost:30, sellCost:42],
			[level:7, radius:150f, damage:350f, sellCost:57, reloadTime:2000]]
			]
			
		})
		
		return [icon:"towerofdefense.images.blastertower_icon", cost:5, instantiationTemplate:blastTower]
	}
	
	def damageOverTime = { damage, time ->
		return (float) damage/time
	}
	
	def laser(){
		def laserTower = new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.lasertower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(-1,0),
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			turnRate:completeTurn(2),
			levels:[
			[level:1, radius:105f, fireDuration:300, reloadTime:1800, damage:damageOverTime(45,300), upgradeCost:7, sellCost:7],
			[level:2, radius:110f, damage:damageOverTime(75,300), upgradeCost:15, sellCost:11],
			[level:3, radius:115f, damage:damageOverTime(95,300), upgradeCost:22, sellCost:18],
			[level:4, radius:120f, damage:damageOverTime(115,300), upgradeCost:30, sellCost:29],
			[level:5, radius:125f, damage:damageOverTime(145,300), upgradeCost:37, sellCost:44],
			[level:6, radius:130f, damage:damageOverTime(185,300), upgradeCost:45, sellCost:63],
			[level:7, radius:200f, damage:damageOverTime(200,300), sellCost:85]
			]
			
			]
		})
		return [icon:"towerofdefense.images.lasertower_icon", cost:7, instantiationTemplate:laserTower]
	}
	
	def missile(){
		def missileTower = new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.missiletower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(1,0),
			turnRate:completeTurn(14),
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			template:"towerofdefense.entities.missilebullet",
			instanceParameters: utils.custom.genericprovider.provide{ tower ->
				def newPosition = tower.position.copy()
				def newDirection = tower.direction.copy()
				[
						position:newPosition,
						direction: newDirection,
						targetEntity:tower.targetEntity,
						image:utils.resources.image("towerofdefense.images.blasterbullet"),
						damage:tower.damage,
						radius:5.0f,
						blastRadius: 40f,
						maxVelocity:0.09f,
						turnRatio:0.18f,
						color:utils.color(1f, 0.1f, 0.1f, 1.0f)
						]
			},
			levels:[[level:1, radius:72f, damage:150f, upgradeCost:5, sellCost:5, reloadTime:3500]]
			]
		})
		return [icon:"towerofdefense.images.missiletower_icon", cost:20, instantiationTemplate:missileTower]
	}
	
	def shock(){
		
		def laserTower = new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.shocktower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(-1,0),
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			levels:[
			[level:1, radius:50f, fireDuration:500, reloadTime:1500, shockFactor:0.5f/500, upgradeCost:7, sellCost:5],
			[level:2, radius:55f, shockFactor:0.6f/500, upgradeCost:15, sellCost:5],
			[level:3, radius:60f, fireDuration:600, shockFactor:0.6f/600, upgradeCost:22, sellCost:5],
			[level:4, radius:65f, shockFactor:0.7f/500, upgradeCost:30, sellCost:5],
			[level:5, radius:70f, fireDuration:700, shockFactor:0.7f/700, upgradeCost:37, sellCost:5],
			[level:6, radius:75f, shockFactor:0.8f/500, upgradeCost:45, sellCost:5],
			[level:7, radius:100f, fireDuration:800, shockFactor:0.8f/800, sellCost:85]]					
			]
		})
		return [icon:"towerofdefense.images.shocktower_icon", cost:7, instantiationTemplate:laserTower]
		
	}
}