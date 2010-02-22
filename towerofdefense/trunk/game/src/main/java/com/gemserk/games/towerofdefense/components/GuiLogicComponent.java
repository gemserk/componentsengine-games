package com.gemserk.games.towerofdefense.components;

import java.util.Collection;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;

public class GuiLogicComponent extends ReflectionComponent {

	@Inject
	MessageQueue messageQueue;

	@Inject
	@Root
	Entity rootEntity;

	@Inject
	Input input;

	PropertyLocator<String> stateProperty;

	PropertyLocator<Vector2f> mousePositionProperty;

	private PropertyLocator<Entity> selectedTowerProperty;

	private PropertyLocator<Color> deployCursorColorProperty;

	private PropertyLocator<Boolean> deployTowerEnabledProperty;

	public GuiLogicComponent(String id) {
		super(id);
		stateProperty = Properties.property(id, "state");
		mousePositionProperty = Properties.property(id, "mousePosition");
		selectedTowerProperty = Properties.property(id, "selectedTower");
		deployCursorColorProperty = Properties.property(id, "deployCursorColor");
		deployTowerEnabledProperty = Properties.property(id, "deployTowerEnabled");
	}

	public void handleMessage(UpdateMessage message) {

		String state = stateProperty.getValue(entity);
		
		if (state.equals("deployState")) {
			Vector2f mousePosition = mousePositionProperty.getValue(entity);
			Entity nearTowerEntity = getTowerNear(mousePosition);
			
			deployTowerEnabledProperty.setValue(entity, true);
			
			if (nearTowerEntity != null)
			{
				deployCursorColorProperty.setValue(entity, new Color(0.3f, 0.0f, 0.0f,0.1f));
				
			} else {
				deployCursorColorProperty.setValue(entity, new Color(0.0f, 0.3f, 0.0f,0.1f));
			}				
			
		} else {
			deployTowerEnabledProperty.setValue(entity, false);
		}
		
	}

	public void handleMessage(GenericMessage message) {

		String state = stateProperty.getValue(entity);

		Vector2f mousePosition = mousePositionProperty.getValue(entity);			

		if (message.getId().equals("move")) {
			float x = Properties.getValue(message, "x");
			float y = Properties.getValue(message, "y");
			mousePosition.set(x, y);
		}

		if (message.getId().equals("click")) {

			if (state.equals("deployState")) {

				Entity nearTowerEntity = getTowerNear(mousePosition);
				if (nearTowerEntity == null)
					messageQueue.enqueue(new GenericMessage("deployturret"));

			} else {

				Entity selectedTower = selectedTowerProperty.getValue(entity);
				if (selectedTower != null) {
					Properties.setValue(selectedTower, "selected", false);
				}

				Vector2f position = mousePositionProperty.getValue(entity);
				Entity newSelectedTower = getTowerNear(position);

				if (newSelectedTower != null) {
					Properties.setValue(newSelectedTower, "selected", true);
					selectedTowerProperty.setValue(entity, newSelectedTower);
				}

			}
		}

		if (message.getId().equals("changeState")) {
			if (state.equals("deployState"))
				state = "selectTowerState";
			else
				state = "deployState";
			stateProperty.setValue(entity, state);
		}
	}

	private Entity getTowerNear(Vector2f mousePosition) {
		Collection<Entity> towers = rootEntity.getEntities(Predicates.and(EntityPredicates.withAnyTag("tower"), EntityPredicates.isNear(mousePosition, 50.0f)));

		Entity entity = null;
		if (towers.size() > 0) {
			entity = towers.iterator().next();
		}
		
		return entity;
	}

}