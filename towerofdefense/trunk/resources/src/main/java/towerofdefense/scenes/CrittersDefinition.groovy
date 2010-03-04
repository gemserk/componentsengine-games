package towerofdefense.scenes;

import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;

public class CrittersDefinition{
	
	def CrittersDefinition(BuilderUtils utils){
		this.utils = utils;
	}
	
	BuilderUtils utils;
	
	def critter(def type){
		switch (type) {
		case "chomper":
			return chomper()
		case "spinner":
			return spinner();
		case "wiggle":
			return wiggle();
		case "star":
			return star();
		case "cubic":
			return cubic();
		default:
			return null;
		}
	}
	
	def chomper(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
			image:utils.resources.image("towerofdefense.images.cee"),
			position:entity.position.copy(),
			speed:50f,
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			health:8f,
			points: 5,
			reward:1f			
			]	
		})
	}
	
	def spinner(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
			rotationImage:utils.resources.image("towerofdefense.images.rombox"),
			rotationSpeed:4f,
			position:entity.position.copy(),
			speed:70f,
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			health:12f,
			points: 10,
			reward:1f
			]	
		}	)
	}
	
	def wiggle(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
		   image:utils.resources.image("towerofdefense.images.ralph"),
			rotationImage:utils.resources.image("towerofdefense.images.ralph_balls"),
			rotationSpeed:3f,
			position:entity.position.copy(),
			speed:20f,
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			health:20f,
			points: 15,
			reward:1f
			]	
		}	)
	}
	
	def star(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
		   image:utils.resources.image("towerofdefense.images.dan"),
			rotationImage:utils.resources.image("towerofdefense.images.dan_balls"),
			rotationSpeed:1f,
			position:entity.position.copy(),
			speed:90f,
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			health:15f,
			points: 20,
			reward:1f
			]	
		}	
		)
	}
	
	def cubic(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
		   image:utils.resources.image("towerofdefense.images.dan"),
			rotationImage:utils.resources.image("towerofdefense.images.dan_balls"),
			rotationSpeed:1f,
			position:entity.position.copy(),
			speed:90f,
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			health:15f,
			points: 20,
			reward:1f
			]	
		}	
		)
	}
}