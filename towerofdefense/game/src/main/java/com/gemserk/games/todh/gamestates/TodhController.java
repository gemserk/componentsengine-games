package com.gemserk.games.todh.gamestates;

import java.util.ArrayList;
import java.util.Collection;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.controllers.InputController;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.Game;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.world.World;
import com.google.inject.Inject;

public class TodhController implements InputListener, InputController {

	Entity selectedEntity = null;

	boolean changingRadius = false;

	World world;

	Input input;

	Game game;

	boolean editMode = true;

	@Inject
	public void setGame(Game game) {
		this.game = game;
	}

	@Inject
	public void setWorld(World world) {
		this.world = world;
	}

	public TodhController() {

	}

	@Inject
	@Override
	public void setInput(Input input) {
		this.input = input;
	}

	@Override
	public void mousePressed(int button, int x, int y) {

		final Vector2f mousePosition = new Vector2f(x, y);

		if (!editMode)
			return;

		if (onMousePressedHandleDragVertex(mousePosition))
			return;

		if (onMousePressedHandleDrag(mousePosition))
			return;

		if (onMousePressedHandleResize(mousePosition))
			return;

		if (onMousePressedHandleAddPath(button, mousePosition))
			return;

		if (onMousePressedHandleAddItem(button, mousePosition))
			return;

	}

	private boolean onMousePressedHandleAddPath(int button,
			final Vector2f mousePosition) {

		if (button != Input.MOUSE_LEFT_BUTTON)
			return false;

		world.handleMessage(new GenericMessage("addPathAction",
				new PropertiesMapBuilder() {
					{
						property("value", mousePosition);
						property("entityId", "hero");
					}
				}.build()));

		return true;
	}

	private boolean onMousePressedHandleAddItem(int button,
			final Vector2f mousePosition) {

		if (button != Input.MOUSE_RIGHT_BUTTON)
			return false;

		game.handleMessage(new GenericMessage("addItemAction",
				new PropertiesMapBuilder() {
					{
						property("value", mousePosition);
					}
				}.build()));

		return true;
	}

	private boolean onMousePressedHandleResize(final Vector2f mousePosition) {
		Collection<Entity> rangedEntities = world.getEntities(EntityPredicates
				.withAllTags("ranged"));

		for (Entity rangedEntity : rangedEntities) {

			Vector2f center = (Vector2f) Properties.property("position")
					.getValue(rangedEntity);
			Float radius = (Float) Properties.property("radius").getValue(
					rangedEntity);

			if (Math.abs(mousePosition.distance(center) - radius) < 10f) {
				selectedEntity = rangedEntity;
				changingRadius = true;
				return true;
			}

		}

		return false;
	}

	Vector2f selectedVertex = null;

	private boolean onMousePressedHandleDragVertex(Vector2f mousePosition) {

		Entity entity = world.getEntityById("hero");

		ArrayList<Vector2f> line = (ArrayList<Vector2f>) Properties.property(
				"followpath.path").getValue(entity);

		for (Vector2f vertex : line) {
			if (vertex.distance(mousePosition) < 5.0f) {
				selectedVertex = vertex;
				return true;
			}
		}

		return false;
	}

	private boolean onMousePressedHandleDrag(final Vector2f mousePosition) {
		// Collection<Entity> dragableEntities =
		// world.getEntities(Predicates.and(
		// EntityPredicates.withAllTags("dragable"), EntityPredicates
		// .isNear(mousePosition, 30.0f)));

		Collection<Entity> dragableEntities = world
				.getEntities(EntityPredicates.isNear(mousePosition, 20.0f));

		if (dragableEntities.isEmpty())
			return false;

		selectedEntity = dragableEntities.iterator().next();
		changingRadius = false;

		return true;
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		selectedEntity = null;
		changingRadius = false;
		selectedVertex = null;
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {

		final Vector2f mousePosition = new Vector2f(newx, newy);

		if (onMouseMovedHandleVertexDrag(mousePosition))
			return;

		if (onMouseMovedHandleResize(mousePosition))
			return;

		if (onMouseMovedHandleDrag(mousePosition))
			return;

	}

	private boolean onMouseMovedHandleVertexDrag(Vector2f mousePosition) {

		if (selectedVertex == null)
			return false;

		selectedVertex.set(mousePosition);

		return true;
	}

	private boolean onMouseMovedHandleResize(final Vector2f mousePosition) {

		if (selectedEntity == null)
			return false;

		if (!changingRadius)
			return false;

		PropertyLocator<Vector2f> positionProperty = Properties
				.property("position");
		final float distance = mousePosition.distance(positionProperty
				.getValue(selectedEntity));
		if (distance > 30.0f) {
			selectedEntity.handleMessage(new GenericMessage("changeradius",
					new PropertiesMapBuilder() {
						{
							property("value", distance);
						}
					}.build()));

			return true;

		}

		return false;
	}

	private boolean onMouseMovedHandleDrag(final Vector2f mousePosition) {

		if (selectedEntity == null)
			return false;

		selectedEntity.handleMessage(new GenericMessage("move",
				new PropertiesMapBuilder() {
					{
						property("value", mousePosition);
					}
				}.build()));

		return true;

	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {

	}

	@Override
	public void mouseWheelMoved(int change) {

	}

	@Override
	public void inputEnded() {

	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void keyPressed(int key, char c) {

		if (key == Input.KEY_R) {
			game.handleMessage(new GenericMessage("loadScene",
					new PropertiesMapBuilder() {
						{
							property("scene", "todh.scenes.scene1");
						}
					}.build()));
		}

		if (key == Input.KEY_E) {
			editMode = !editMode;
		}

	}

	@Override
	public void keyReleased(int key, char c) {

	}

	@Override
	public void controllerButtonPressed(int controller, int button) {

	}

	@Override
	public void controllerButtonReleased(int controller, int button) {

	}

	@Override
	public void controllerDownPressed(int controller) {

	}

	@Override
	public void controllerDownReleased(int controller) {

	}

	@Override
	public void controllerLeftPressed(int controller) {

	}

	@Override
	public void controllerLeftReleased(int controller) {

	}

	@Override
	public void controllerRightPressed(int controller) {
	}

	@Override
	public void controllerRightReleased(int controller) {

	}

	@Override
	public void controllerUpPressed(int controller) {

	}

	@Override
	public void controllerUpReleased(int controller) {

	}

	@Override
	public void register() {

		input.removeAllControllerListeners();
		input.addListener(this);

	}

}