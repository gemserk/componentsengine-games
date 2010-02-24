package com.gemserk.games.towerofdefense.components;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
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

	PropertyLocator<Vector2f> mousePositionProperty;

	private PropertyLocator<Entity> selectedTowerProperty;

	private PropertyLocator<String> deployCursorStateProperty;

	private PropertyLocator<Boolean> deployTowerEnabledProperty;

	private PropertyLocator<Path> pathProperty;
	private PropertyLocator<Rectangle> gameBoundsProperty;

	public GuiLogicComponent(String id) {
		super(id);
		mousePositionProperty = Properties.property(id, "mousePosition");
		selectedTowerProperty = Properties.property(id, "selectedTower");
		deployCursorStateProperty = Properties.property(id, "deployCursorState");
		deployTowerEnabledProperty = Properties.property(id, "deployTowerEnabled");
		pathProperty = Properties.property(id, "path");
		gameBoundsProperty = Properties.property(id, "gameBounds");

		internalState = new SelectTowerState();
	}

	public void handleMessage(Message message) {
		internalState.handleMessage(message);
	}

	InternalState internalState;

	public abstract class InternalState {

		public void handleMessage(Message message) {

			if (message instanceof GenericMessage) {
				GenericMessage genericMessage = (GenericMessage) message;
				if (genericMessage.getId().equals("click"))
					handleLeftClick(genericMessage);
				else if (genericMessage.getId().equals("rightClick"))
					handleRightClick(genericMessage);
				else if (genericMessage.getId().equals("move"))
					handleMove(genericMessage);
				else
					handleMessage(genericMessage);
			}

			if (message instanceof UpdateMessage) {
				UpdateMessage updateMessage = (UpdateMessage) message;

				int money = (Integer) Properties.getValue(entity, "money");
				Map<String, Map<String, Object>> towerDescriptions = Properties.getValue(entity, "towerDescriptions");

				for (Entry<String, Map<String, Object>> entry : towerDescriptions.entrySet()) {
					String key = entry.getKey();
					Map<String, Object> values = entry.getValue();
					int cost = (Integer) values.get("cost");

					Entity button = entity.getEntityById("button-" + key);
					Properties.setValue(button, "enabled", money >= cost);
				}

				handleMessage(updateMessage);
			}

		}

		protected void handleMove(GenericMessage message) {
			float x = (Float) Properties.getValue(message, "x");
			float y = (Float) Properties.getValue(message, "y");
			mousePositionProperty.setValue(entity, new Vector2f(x, y));
		}

		protected void handleMessage(GenericMessage message) {

		}

		protected abstract void handleMessage(UpdateMessage message);

		protected abstract void handleLeftClick(GenericMessage message);

		protected abstract void handleRightClick(GenericMessage message);

	}

	public class DeployState extends InternalState {

		protected void handleMessage(UpdateMessage message) {
			Vector2f mousePosition = mousePositionProperty.getValue(entity);

			boolean candeploy = true;
			deployTowerEnabledProperty.setValue(entity, true);
			Rectangle gameBounds = gameBoundsProperty.getValue(entity);
			
			if (gameBounds.contains(mousePosition.x, mousePosition.y)) {

				Entity nearestBlockingEntity = getNearestEntity(mousePosition, "tower", 25.0f);

				if (nearestBlockingEntity == null)
					nearestBlockingEntity = getNearestEntity(mousePosition, "base", 45.0f);

				
				
				
			
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
			} else {
				candeploy = false;
			}

			deployCursorStateProperty.setValue(entity, candeploy ? "candeploy" : "cantdeploy");
		}

		protected void handleRightClick(GenericMessage message) {
			changeToSelectTowerState();
		}

		protected void handleLeftClick(GenericMessage message) {
			String cursorState = deployCursorStateProperty.getValue(entity);
			if (cursorState.equals("candeploy")) {
				messageQueue.enqueue(new GenericMessage("deployturret"));
			}
		}
		
		@Override
		protected void handleMessage(GenericMessage message) {
			if (message.getId().equals("deployTowerSelected")) {
				String towerType = Properties.getValue(message, "towerType");

				Properties.setValue(entity, "towerType", towerType);
				changeToDeployState();
			}
		}
	}

	public class SelectTowerState extends InternalState {

		@Override
		protected void handleMessage(GenericMessage message) {
			if (message.getId().equals("deployTowerSelected")) {
				String towerType = Properties.getValue(message, "towerType");

				Properties.setValue(entity, "towerType", towerType);
				changeToDeployState();
			}
		}

		protected void handleMessage(UpdateMessage message) {
			deployTowerEnabledProperty.setValue(entity, false);
		}

		protected void handleRightClick(GenericMessage message) {
			unselectCurrentTower();
		}

		protected void handleLeftClick(GenericMessage message) {
			unselectCurrentTower();

			Vector2f position = mousePositionProperty.getValue(entity);
			Entity newSelectedTower = getNearestEntity(position, "tower", 25.0f);

			if (newSelectedTower != null) {
				Properties.setValue(newSelectedTower, "selected", true);
				selectedTowerProperty.setValue(entity, newSelectedTower);
			}
		}

	}

	private void changeToDeployState() {
		internalState = new DeployState();
	}

	private void changeToSelectTowerState() {
		internalState = new SelectTowerState();
	}

	private void unselectCurrentTower() {
		Entity selectedTower = selectedTowerProperty.getValue(entity);
		if (selectedTower != null)
			Properties.setValue(selectedTower, "selected", false);
	}

	private Entity getNearestEntity(Vector2f position, String tag, float distance) {
		Collection<Entity> entities = rootEntity.getEntities(Predicates.and(EntityPredicates.withAnyTag(tag), EntityPredicates.isNear(position, distance)));

		Entity entity = null;

		if (entities.size() > 0)
			entity = entities.iterator().next();

		return entity;
	}

}