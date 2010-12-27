package com.gemserk.componentsengine.commons.gui;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

public class TextFieldSlickImpl implements KeyListener {

	TextField textField;
	
	String regex;
	
	public TextFieldSlickImpl(TextField textField, String regex) {
		this.textField = textField;
		this.regex = regex;
	}

	public TextFieldSlickImpl(TextField textField) {
		this(textField, "[\\w ]");
	}

	public TextField getTextField() {
		return textField;
	}
	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_LEFT)
			textField.cursorLeft();
		else if (key == Input.KEY_RIGHT)
			textField.cursorRight();
		else if (key == Input.KEY_BACK)
			textField.backspace();
		else if (key == Input.KEY_DELETE)
			textField.delete();
		else {
			if (Character.toString(c).matches(regex))
				textField.insert(c);
		}
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
	public void inputStarted() {
		
	}

}
