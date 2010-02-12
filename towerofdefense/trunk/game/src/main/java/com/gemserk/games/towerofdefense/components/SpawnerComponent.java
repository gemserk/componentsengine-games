package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Map;
import java.util.Random;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.AddEntityMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.componentsengine.utils.Interval;
import com.gemserk.games.towerofdefense.GenericProvider;
import com.google.inject.Inject;

public class SpawnerComponent extends Component {

	PropertyLocator<Vector2f> spawnPositionProperty;

	PropertyLocator<Interval> spawnDelayProperty;

	PropertyLocator<String> templateNameProperty;

	PropertyLocator<Integer> timeToNextSpawnProperty;
	
	PropertyLocator<GenericProvider> instanceParametersProviderProperty;

	@Inject MessageQueue messageQueue;
	
	private TemplateProvider templateProvider;

	private final Random random = new Random();

	public SpawnerComponent(String id) {
		super(id);

		timeToNextSpawnProperty = property(id, "timeToNextSpawn");
		spawnPositionProperty = property(id, "position");
		spawnDelayProperty = property(id, "spawnDelay");
		templateNameProperty = property(id, "template");
		instanceParametersProviderProperty= property(id, "instanceParameters");
	}

	@Inject
	public void setTemplateProvider(TemplateProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	private void update(final Entity entity, int delta) {
		Integer timeToNextSpawn = this.timeToNextSpawnProperty.getValue(entity);
		if (timeToNextSpawn == null)
			timeToNextSpawn = 0;

		if (timeToNextSpawn <= 0) {
			final Vector2f entityPosition = spawnPositionProperty.getValue(entity).copy();
			
			Map<String, Object> instanceProperties = instanceParametersProviderProperty.getValue(entity).get();

			instanceProperties.put("position", entityPosition.copy());
			
			String templateName = templateNameProperty.getValue(entity);
			Entity newEntity = templateProvider.getTemplate(templateName).instantiate("", instanceProperties);
			messageQueue.enqueue(new AddEntityMessage(newEntity));

			Interval interval = this.spawnDelayProperty.getValue(entity);
			Integer delay = interval.getMin() + random.nextInt(interval.getLength() + 1);
			this.timeToNextSpawnProperty.setValue(entity, delay); // sumarle timetonextspawn si es negativo creo
		} else {
			this.timeToNextSpawnProperty.setValue(entity, timeToNextSpawn - delta);
		}
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof UpdateMessage) {
			UpdateMessage update = (UpdateMessage) message;
			this.update(message.getEntity(), update.getDelta());

		}
	}

}
