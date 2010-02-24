package com.gemserk.games.towerofdefense.components;

import java.util.Collection;
import java.util.List;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Line;
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
import com.gemserk.games.towerofdefense.Path;
import com.google.common.base.Predicates;
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

	private PropertyLocator<String> deployCursorStateProperty;

	private PropertyLocator<Boolean> deployTowerEnabledProperty;

	private PropertyLocator<Path> pathProperty;

	public GuiLogicComponent(String id) {
		super(id);
		stateProperty = Properties.property(id, "state");
		mousePositionProperty = Properties.property(id, "mousePosition");
		selectedTowerProperty = Properties.property(id, "selectedTower");
		deployCursorStateProperty = Properties.property(id, "deployCursorState");
		deployTowerEnabledProperty = Properties.property(id, "deployTowerEnabled");
		pathProperty = Properties.property(id, "path");
	}

	public void handleMessage(UpdateMessage message) {

		String state = stateProperty.getValue(entity);

		if (isDeployState(state)) {
			Vector2f mousePosition = mousePositionProperty.getValue(entity);
			Entity nearestBlockingEntity = getNearestEntity(mousePosition, "tower", 25.0f);
			
			if (nearestBlockingEntity == null)
				nearestBlockingEntity = getNearestEntity(mousePosition, "base", 45.0f);

			deployTowerEnabledProperty.setValue(entity, true);

			boolean candeploy = true;

			if (nearestBlockingEntity == null) {
				
				Path path = pathProperty.getValue(entity);
				List<Vector2f> points = path.getPoints();

				for (int i = 0; i < points.size(); i++) {
					Vector2f source = points.get(i).copy();

					int j = i + 1;

					if (j >= points.size())
						continue;

					Vector2f target = points.get(j);

					Line line = new Line(source, target);

					float pathWidth = 30.0f;
					if (line.distance(mousePosition) < pathWidth) {
						candeploy = false;
						break;
					}

				}
				
			} else {
				candeploy = false;
			}

			deployCursorStateProperty.setValue(entity, candeploy ? "candeploy" : "cantdeploy");

		} else {
			deployTowerEnabledProperty.setValue(entity, false);
		}

	}

	public void handleMessage(GenericMessage message) {

		String state = stateProperty.getValue(entity);

		Vector2f mousePosition = mousePositionProperty.getValue(entity);

		if (message.getId().equals("move")) {
			float x = (Float) Properties.getValue(message, "x");
			float y = (Float) Properties.getValue(message, "y");
			mousePosition.set(x, y);
		}

		if (message.getId().equals("click")) {

			if (isDeployState(state)) {

				String cursorState = deployCursorStateProperty.getValue(entity);
				if (cursorState.equals("candeploy"))
				{
					messageQueue.enqueue(new GenericMessage("deployturret"));
					// changeToSelectTowerState();
				}

			} else {

				unselectCurrentTower();

				Vector2f position = mousePositionProperty.getValue(entity);
				Entity newSelectedTower = getNearestEntity(position, "tower", 25.0f);

				if (newSelectedTower != null) {
					Properties.setValue(newSelectedTower, "selected", true);
					selectedTowerProperty.setValue(entity, newSelectedTower);
				}

			}
		}

		if (message.getId().equals("changeState")) {
			if (isDeployState(state))
				state = "selectTowerState";
			else
				state = "deployState";
			stateProperty.setValue(entity, state);
		}
		
		if (message.getId().equals("rightClick")) {
			
			if (isDeployState(state)){
				changeToSelectTowerState();
			}
			
			if (isSelectTowerState(state)) {
				unselectCurrentTower();
			} 
			
		}
	}

	private void changeToSelectTowerState() {
		stateProperty.setValue(entity, "selectTowerState");
	}

	private void unselectCurrentTower() {
		Entity selectedTower = selectedTowerProperty.getValue(entity);
		if (selectedTower != null)
			Properties.setValue(selectedTower, "selected", false);
	}

	private boolean isSelectTowerState(String state) {
		return state.equals("selectTowerState");
	}

	private boolean isDeployState(String state) {
		return state.equals("deployState");
	}

	private Entity getNearestEntity(Vector2f position, String tag, float distance) {
		Collection<Entity> entities = rootEntity.getEntities(Predicates.and(EntityPredicates.withAnyTag(tag), EntityPredicates.isNear(position, distance)));

		Entity entity = null;

		if (entities.size() > 0) 
			entity = entities.iterator().next();

		return entity;
	}

}