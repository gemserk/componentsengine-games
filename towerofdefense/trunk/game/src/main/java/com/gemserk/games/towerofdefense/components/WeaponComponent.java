package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.Map;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.*;
import com.gemserk.componentsengine.properties.*;
import com.gemserk.componentsengine.templates.*;
import com.gemserk.games.towerofdefense.GenericProvider;
import com.google.inject.Inject;

public class WeaponComponent extends ReflectionComponent {

	PropertyLocator<Entity> targetEntityProperty;

	TemplateProvider templateProvider;

	PropertyLocator<Vector2f> positionProperty;

	PropertyLocator<Integer> reloadTimeProperty;

	PropertyLocator<Integer> currentReloadTimeProperty;

	PropertyLocator<String> templateProperty;

	PropertyLocator<GenericProvider> instanceParametersProviderProperty;

	PropertyLocator<Entity> entityProperty;
	
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
		entityProperty = property(id, "entity");
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

		messageQueue.enqueue(ChildrenManagementMessageFactory.addEntity(bullet, entityProperty.getValue(entity).getId()));

		currentReloadTime = reloadTimeProperty.getValue(entity);
		currentReloadTimeProperty.setValue(entity, currentReloadTime);
	}

}