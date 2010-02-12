
package towerofdefense.scenes;
import towerofdefense.GroovyBootstrapper;
import towerofdefense.components.TowerDeployer;

import com.gemserk.componentsengine.components.ReflectionComponent 
import com.gemserk.componentsengine.entities.Entity 
import com.gemserk.componentsengine.messages.AddEntityMessage 
import com.gemserk.componentsengine.messages.GenericMessage 
import com.gemserk.componentsengine.messages.MessageQueue 
import com.gemserk.componentsengine.templates.EntityTemplate 
import com.gemserk.componentsengine.templates.TemplateProvider 
import com.google.inject.Inject 
import org.newdawn.slick.Input 

import org.newdawn.slick.Input 
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.AddEntityMessage;
import com.gemserk.componentsengine.messages.MessageQueue 
import com.gemserk.componentsengine.templates.EntityTemplate;

import com.gemserk.componentsengine.templates.TemplateProvider;

import com.gemserk.componentsengine.messages.GenericMessage;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.games.towerofdefense.Path;

builder.scene("todh.scenes.scene1") {
	
	new GroovyBootstrapper();
	
	images("assets/images.properties")
	
	input("playerInputMapping","towerofdefense.input.inputmapping")
	
	components(com.gemserk.games.towerofdefense.TowerOfDefenseComponentLoader.class);
	
	entity(template:"towerofdefense.entities.path", id:"path")	{
		path=new Path([
		utils.vector(0, 300), 		      
		utils.vector(100, 300), 
		utils.vector(200, 400), 		      
		utils.vector(400, 400), 		      
		utils.vector(400, 300), 		      
		utils.vector(670, 300)])		
		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
	}
	
	entity(template:"towerofdefense.entities.base", id:"base")	{
		position=utils.vector(700,300)
		direction=utils.vector(-1,0)
		radius=30f
		
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
		fillColor=utils.color(0.0f, 0.0f, 0.0f, 0.2f)
	}
	
	entity(template:"towerofdefense.entities.spawner", id:"spawner")	{
		position=utils.vector(-10,300)
		template="towerofdefense.entities.critter"
		spawnDelay=utils.interval(400,1000)
		instanceParameters= [
				maxVelocity:0.06f,
				pathEntityId:"path",
				pathProperty:"path",
				color:utils.color(1.0f, 0.5f, 0.5f, 0.95f),
				health:utils.container(100,100)
				]		
	}
	
	entity(template:"towerofdefense.entities.generic", id:"towerDeployer")	{
		
		property_template="towerofdefense.entities.tower"
		property_instanceParameters = [
				direction:utils.vector(-1,0),
				radius:100f,
				lineColor:utils.color(0.0f, 0.0f, 0.0f, 1.0f),
				fillColor:utils.color(0.0f, 0.0f, 0.0f, 0.2f),
				color:utils.color(0.0f, 0.2f, 0.0f, 1.0f),
				template:"towerofdefense.entities.bullet",
				reloadTime:1000,
				instanceParameters: [
				damage:25.0f,
				radius:10.0f,
				maxVelocity:0.5f,
				color:utils.color(0.4f, 1.0f, 0.4f, 1.0f)
				]		
				];
		
		component_towerdeployer=new TowerDeployer("towerdeployer")
	}

	input("inputmapping"){
		mouse {
			
			press(button:"left", eventId:"deployturret");
			
			
		}
	}
	
}