package towerofdefense.scenes;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.games.towerofdefense.InstantiationTemplateImpl;
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
		default:
			return null;
		}
	}
	
	def chomper(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
			position:entity.position.copy(),
			maxVelocity:0.05f,
			color:utils.color(1.0f, 0.5f, 0.5f, 0.95f),
			health:utils.container(8,8),
			points: 5,
			reward:1			
			]	
		})
	}
	
	def spinner(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
			position:entity.position.copy(),
			maxVelocity:0.07f,
			color:utils.color(1.0f, 1.0f, 1.0f, 1.0f),
			health:utils.container(12,12),
			points: 10,
			reward:2
			]	
		}	)
	}
	
	def wiggle(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
			position:entity.position.copy(),
			maxVelocity:0.02f,
			color:utils.color(0.0f, 1.0f, 0.0f, 1.0f),
			health:utils.container(20,20),
			points: 15,
			reward:3
			]	
		}	)
	}
	
	def star(){
		return new InstantiationTemplateImpl(
		utils.custom.templateProvider.getTemplate("towerofdefense.entities.critter"),
		utils.custom.genericprovider.provide{ entity ->
			[
			position:entity.position.copy(),
			maxVelocity:0.09f,
			color:utils.color(0.0f, 0.0f, 1.0f, 1.0f),
			health:utils.container(15,15),
			points: 20,
			reward:4
			]	
		}	
		)
	}
	
}