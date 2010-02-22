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

		if (state.equals("deployState")) {
			Vector2f mousePosition = mousePositionProperty.getValue(entity);
			Entity nearTowerEntity = getTowerNear(mousePosition);

			deployTowerEnabledProperty.setValue(entity, true);

			boolean candeploy = true;

			if (nearTowerEntity == null) {

				Path path = pathProperty.getValue(entity);
				List<Vector2f> points = path.getPoints();

				for (int i = 0; i < points.size(); i++) {
					Vector2f source = points.get(i).copy();

					int j = i + 1;

					if (j >= points.size())
						continue;

					Vector2f target = points.get(j);

					Line line = new Line(source, target);

					float pathWidth = 23.0f;
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

			if (state.equals("deployState")) {

				String cursorState = deployCursorStateProperty.getValue(entity);
				if (cursorState.equals("candeploy"))
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
		float currentTowerSize = 25.0f;
		Collection<Entity> towers = rootEntity.getEntities(Predicates.and(EntityPredicates.withAnyTag("tower"), EntityPredicates.isNear(mousePosition, currentTowerSize)));

		Entity entity = null;
		if (towers.size() > 0) {
			entity = towers.iterator().next();
		}

		return entity;
	}

}