package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Map;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildMessage;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.templates.EntityTemplate;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.games.towerofdefense.GenericProvider;
import com.google.inject.Inject;

public class WeaponComponent extends ReflectionComponent {

	PropertyLocator<Entity> targetEntityProperty;


	TemplateProvider templateProvider;

	private PropertyLocator<Vector2f> positionProperty;

	private PropertyLocator<Integer> reloadTimeProperty;

	private PropertyLocator<Integer> currentReloadTimeProperty;

	private PropertyLocator<String> templateProperty;

	PropertyLocator<GenericProvider> instanceParametersProviderProperty;

	MessageQueue messageQueue;
	@Inject 
	public void setMessageQueue(MessageQueue messageQueue) {
		this.messageQueue = messageQueue;
	}
	
	@Inject
	public void setTemplateProvider(TemplateProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	public WeaponComponent(String id) {
		super(id);
		targetEntityProperty = property(id, "targetEntity");
		positionProperty = property(id, "position");
		reloadTimeProperty = property(id, "reloadTime");
		currentReloadTimeProperty = property(id, "currentReloadTime");
		templateProperty = property(id, "template");
		instanceParametersProviderProperty = property(id, "instanceParameters");
	}

	@Override
	public void onAdd(Entity entity) {
		super.onAdd(entity);
		currentReloadTimeProperty.setValue(entity, 0);
	}

	public void handleMessage(UpdateMessage message) {
		int delta = message.getDelta();
		Integer currentReloadTime = currentReloadTimeProperty.getValue(entity);
		
		if (currentReloadTime > 0) {
			currentReloadTime -= delta;
			currentReloadTimeProperty.setValue(entity, currentReloadTime);
			return;
		}

		Entity targetEntity = targetEntityProperty.getValue(entity);
		if (targetEntity == null)
			return;
		
		final Vector2f position = positionProperty.getValue(entity);
		String templateName = templateProperty.getValue(entity);
		
		EntityTemplate bulletTemplate = templateProvider.getTemplate(templateName);
		
		Vector2f targetPosition = (Vector2f) Properties.property("position").getValue(targetEntity);
		
		Map<String, Object> instanceParameters = instanceParametersProviderProperty.getValue(entity).get();
		instanceParameters.put("position", position.copy());
		instanceParameters.put("direction", targetPosition.copy().sub(position).normalise());
		
		Entity bullet = bulletTemplate.instantiate("", instanceParameters);
		
		messageQueue.enqueue(ChildMessage.addEntity(bullet,"world"));
		
		currentReloadTime = reloadTimeProperty.getValue(entity);
		currentReloadTimeProperty.setValue(entity, currentReloadTime);
	}

}