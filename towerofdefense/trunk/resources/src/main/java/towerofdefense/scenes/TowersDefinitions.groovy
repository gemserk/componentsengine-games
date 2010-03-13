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
	
	def blaster(){
		def blastTower = new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.blastertower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(-1,0),
			radius:50f,
			lineColor:utils.color(0.0f, 0.8f, 0.0f,0.5f),
			fillColor:utils.color(0.0f, 0.8f, 0.0f,0.25f),
			color:utils.color(1f, 1f, 1f, 1.0f),
			template:"towerofdefense.entities.bullet",
			reloadTime:200,
			turnRate:0.3f,
			instanceParameters: utils.custom.genericprovider.provide{ tower ->
				[
				position:tower.position.copy(),
				// direction:(entity.targetEntity.position.copy().sub(newPosition).normalise()),
				direction:tower.direction.copy(),
				image:utils.resources.image("towerofdefense.images.blasterbullet"),
				damage:10.0f,
				radius:10.0f,
				maxVelocity:0.6f,
				color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
				]
			}	
			]
		})
		
		return [icon:"towerofdefense.images.blastertower_icon", cost:5, instantiationTemplate:blastTower]
	}
	
	def laser(){
		def laserTower = new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.lasertower"),
		utils.custom.genericprovider.provide{ position ->
			[
			position:position,
			direction:utils.vector(-1,0),
			lineColor:utils.color(0.0f, 0.0f, 0.8f,0.5f),
			fillColor:utils.color(0.0f, 0.0f, 0.8f,0.25f),
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			radius:90f,
			reloadTime:250,
			turnRate:0.1f,
			damage:0.03f
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
			radius:72f,
			turnRate:(float)(360/14000),
			lineColor:utils.color(1.0f, 0.0f, 0.0f,0.5f),
			fillColor:utils.color(1.0f, 0.0f, 0.0f,0.20f),
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			template:"towerofdefense.entities.missilebullet",
			reloadTime:3500,
			instanceParameters: utils.custom.genericprovider.provide{ entity ->
				def newPosition = entity.position.copy()
				def newDirection = entity.direction.copy()
				[
						position:newPosition,
						direction: newDirection,
						targetEntity:entity.targetEntity,
						image:utils.resources.image("towerofdefense.images.blasterbullet"),
						damage:150.0f,
						radius:5.0f,
						blastRadius: 40f,
						maxVelocity:0.09f,
						turnRatio:0.18f,
						color:utils.color(1f, 0.1f, 0.1f, 1.0f)
						]
			}	
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
			lineColor:utils.color(1.0f, 1.0f, 0.0f,0.5f),
			fillColor:utils.color(1.0f, 1.0f, 0.0f,0.2f),
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			radius:50f,
			reloadTime:1500,
			fireDuration:500,
			shockFactor:0.5f/500
			]
		})
		return [icon:"towerofdefense.images.shocktower_icon", cost:7, instantiationTemplate:laserTower]
		
	}
}