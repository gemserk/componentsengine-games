package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.gui.ClipboardAwtImpl;
import com.gemserk.componentsengine.commons.gui.TextField;
import com.gemserk.componentsengine.commons.gui.TextFieldSlickImpl;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

@SuppressWarnings( { "serial", "unused" })
public class EnterScoreGameStateEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(EnterScoreGameStateEntityBuilder.class);

	@Inject
	MessageQueue messageQueue;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	Input input;

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));
		property("textFieldSlickImpl", null);

		component(new ReferencePropertyComponent("stateHandler") {

			@EntityProperty
			Property<TextFieldSlickImpl> textFieldSlickImpl;

			@EntityProperty
			Property<Rectangle> screenBounds;

			@Handles
			public void enterNodeState(Message message) {
				TextField textField = new TextField("", new ClipboardAwtImpl());
				textField.setMaxLength(20);
				textFieldSlickImpl.set(new TextFieldSlickImpl(textField));
				input.addKeyListener(textFieldSlickImpl.get());
				
				Message sourceMessage = Properties.getValue(message, "message");
				
				final Long points = Properties.getValue(sourceMessage, "points");
				final String levelName = Properties.getValue(sourceMessage, "levelName");
				
				Entity scoreScreen = templateProvider.getTemplate("screens.enterscore").instantiate("scoreScreen", new HashMap<String, Object>(){{
					put("textFieldSlickImpl", textFieldSlickImpl);
					put("points", points);
					put("screenBounds", screenBounds.get());
					put("levelName", levelName);
				}});
				
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(scoreScreen, entity));
			}

			@Handles
			public void leaveNodeState(Message message) {
				input.removeKeyListener(textFieldSlickImpl.get());
			}
		});

	}
}