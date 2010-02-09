import com.gemserk.commons.components.Component;
import com.gemserk.commons.slick.gamestates.GameContext;
import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.Game;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.componentsengine.world.World;
import com.google.inject.Inject;

import com.gemserk.componentsengine.properties.*;

builder.scene("todh.scenes.scene1") {

	images("todh/assets/images.properties")
	
	input("playerInputMapping","todh.input.inputmapping")
	
	components(com.gemserk.games.todh.gamestates.TodhComponentLoader.class)	
	
	controller(com.gemserk.games.todh.gamestates.TodhController.class)
	
	entity(template:"todh.entities.hero", id:"hero") {
	
		position=utils.vector(-15.0f, 200.0f)
		color=utils.color(0.1f, 0.5f, 1.0f, 1.0f)

		followPath=[
			utils.vector(-10.0f, 200.0f), 
			utils.vector(400.0f, 190.0f), 
			utils.vector(390.0f, 300.0f), 
			utils.vector(800.0f, 290.0f)
		]

		defense1Size=25.0f
		defense1Color=utils.color(0.0f, 1.0f, 0.0f, 1.0f)

		defense2Size=25.0f
		defense2Color=utils.color(1.0f, 1.0f, 0.0f, 1.0f)
		
		hitpoints=utils.container(100,100)
		hitpointsPosition=utils.vector(120.0f, 30.0f)
	}
	
	entity(template:"todh.entities.tower", id:"tower1")	{
		position=utils.vector(360, 230)
		direction=utils.vector(-1, 0)
		color=utils.color(0.0f, 1.0f, 0.0f, 1.0f)
		radius=50.0f
		template="todh.entities.bullet"
		reloadTime=1500
		damage=15.0f
	}
	
	entity(template:"todh.entities.tower", id:"tower2")	{
		position=utils.vector(440, 260)
		direction=utils.vector(-1, 0)
		color=utils.color(1.0f, 1.0f, 0.0f, 1.0f)
		radius=50.0f
		template="todh.entities.bullet"
		reloadTime=1500
		damage=15.0f
	}
	
	entity(template:"todh.entities.tower", id:"tower3")	{
		position=utils.vector(700, 260)
		direction=utils.vector(-1, 0)
		color=utils.color(0.0f, 1.0f, 0.0f, 1.0f)
		radius=80.0f
		template="todh.entities.bullet"
		reloadTime=1000
		damage=25.0f
	}
	
	component(new Component("restartWhenHeroDies") {

		@Inject
		Game game;

		@Inject
		World world;

		@Override
		public void handleMessage(Message message) {

			if (!(message instanceof UpdateMessage))
				return;

			Entity heroEntity = world.getEntityById("hero");
			Container healthContainer = (Container) Properties.property(
				"hitpoints").getValue(heroEntity);

			if (healthContainer.isEmpty()) {
				game.handleMessage(new GenericMessage("SCENE",
						new PropertiesMapBuilder() {
							{
								property("scene", "todh.scenes.scene1");
							}
						}.build()));
			}

		}

	})
	
	def simpleProperty = { value ->
		return new SimpleProperty(value);
	}
	
	def simplePropertyMap = { map ->
		map.each {
			it.value = simpleProperty(it.value)
		}
	}
	
	def buildGenericMessage = { id, map ->
		return new GenericMessage(id, simplePropertyMap(map));
	}
	
	component(new com.gemserk.games.todh.components.editor.AddItemComponent("addTowerComponent"));
	
	component(new Component("logic") {

		@Inject
		World world;
		
		@Inject
		Game game;

		@Override
		public void handleMessage(Message message) {

			if (!(message instanceof GenericMessage))
				return;
				
			GenericMessage genericMessage = (GenericMessage) message;
			
			switch (genericMessage.getId()) {
			
			case "defense1.enable":
				game.handleMessage(buildGenericMessage("enableDefense", ["value":"defense1"]));
				break

			case "defense1.disable":
				game.handleMessage(buildGenericMessage("disableDefense", ["value":"defense1"]));
				break

			case "defense2.enable":
				game.handleMessage(buildGenericMessage("enableDefense", ["value":"defense2"]));
				break

			case "defense2.disable":
				game.handleMessage(buildGenericMessage("disableDefense", ["value":"defense2"]));
				break

			case "SCENE":
				String sceneId = (String) Properties.property("scene").getValue(genericMessage);
				game.loadScene(sceneId);
				break;
			}
			
		}

	})
	
	
	
}