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
			return laser();
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
			radius:45f,
			lineColor:utils.color(0.0f, 0.8f, 0.0f,0.5f),
			fillColor:utils.color(0.0f, 0.8f, 0.0f,0.25f),
			color:utils.color(0.2f, 1.0f, 0.2f, 1.0f),
			template:"towerofdefense.entities.bullet",
			reloadTime:250,
			instanceParameters: utils.custom.genericprovider.provide{
				[
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
			damage:0.03f
			]
		})
		return [icon:"towerofdefense.images.lasertower_icon", cost:7, instantiationTemplate:laserTower]
	}
	

	
	
}