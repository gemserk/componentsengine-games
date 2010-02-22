package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.games.towerofdefense.InstantiationTemplate;
import com.gemserk.games.towerofdefense.waves.Waves;
import com.google.inject.Inject;

public class WavesSpawnerComponent extends ReflectionComponent {

	PropertyLocator<Vector2f> spawnPositionProperty;
	PropertyLocator<Waves> wavesProperty;


	@Inject
	MessageQueue messageQueue;



	public WavesSpawnerComponent(String id) {
		super(id);

		spawnPositionProperty = property(id, "position");
		wavesProperty = property(id, "waves");
	}


	public void handleMessage(UpdateMessage message) {
		int delta = message.getDelta();
		Waves waves = wavesProperty.getValue(entity);
		List<InstantiationTemplate> templates = waves.generateTemplates(delta);
		for (InstantiationTemplate instantiationTemplate : templates) {
			Entity newEntity = instantiationTemplate.get(entity);
			messageQueue.enqueue(ChildMessage.addEntity(newEntity,"world"));
		}
	}

}
