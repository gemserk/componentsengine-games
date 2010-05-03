package com.gemserk.games.jylonwars;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.google.inject.Inject;

public class TextFieldComponent extends FieldsReflectionComponent implements InputListener {

	@EntityProperty
	TextField textField;

	@EntityProperty
	boolean enabled;

	@Inject
	Input input;

	public TextField getTextField() {
		return textField;
	}
	
	@Override
	public void onAdd(Entity entity) {
		input.addListener(this);
		super.onAdd(entity);
	}

	public boolean isFocus() {
		return enabled;
	}

	public void setFocus(boolean focus) {
		this.enabled = focus;
	}

	public TextFieldComponent(String id) {
		super(id);
	}

	public void keyPressed(int key, char c) {
		if (!enabled)
			return;
		if (key == Input.KEY_LEFT)
			textField.cursorLeft();
		else if (key == Input.KEY_RIGHT)
			textField.cursorRight();
		else if (key == Input.KEY_BACK)
			textField.backspace();
		else if (key == Input.KEY_DELETE)
			textField.delete();
		else
			textField.insert(c);
	}
	
	public void handleMessage(UpdateMessage message) {
		
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		
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
	public void setInput(Input input) {
		
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


}
