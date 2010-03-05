package towerofdefense.scenes;
import com.gemserk.componentsengine.builders.BuilderUtils;

import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;



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
			color:utils.color(0.2f, 1.0f, 0.2f, 1.0f),
			template:"towerofdefense.entities.bullet",
			reloadTime:200,
			turnRate:0.1f,
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
			color:utils.color(0.2f, 0.2f, 1.0f, 1.0f),
			radius:90f,
			reloadTime:250,
			turnRate:0.05f,
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
			radius:250f,
			turnRate:0.01f,
			lineColor:utils.color(0.0f, 0.8f, 0.0f,0.5f),
			fillColor:utils.color(0.0f, 0.8f, 0.0f,0.25f),
			color:utils.color(0.2f, 1.0f, 0.2f, 1.0f),
			template:"towerofdefense.entities.missilebullet",
			reloadTime:1000,
			instanceParameters: utils.custom.genericprovider.provide{ entity ->
				def newPosition = entity.position.copy()
				def newDirection = entity.direction.copy()
				[
						position:newPosition,
						direction: newDirection,
						targetEntity:entity.targetEntity,
						image:utils.resources.image("towerofdefense.images.blasterbullet"),
						damage:10.0f,
						radius:10.0f,
						maxVelocity:0.3f,
						color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
						]
			}	
			]
		})
		return [icon:"towerofdefense.images.lasertower_icon", cost:20, instantiationTemplate:missileTower]
	}
	
	
}