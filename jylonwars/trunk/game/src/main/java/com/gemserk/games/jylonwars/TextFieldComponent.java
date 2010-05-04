package com.gemserk.games.jylonwars;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.KeyListener;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.UpdateMessage;

public class TextFieldComponent extends FieldsReflectionComponent implements KeyListener {

	@EntityProperty
	TextField textField;

	@EntityProperty
	boolean enabled;

	public TextField getTextField() {
		return textField;
	}
	
	@Override
	public void onAdd(Entity entity) {
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

}
