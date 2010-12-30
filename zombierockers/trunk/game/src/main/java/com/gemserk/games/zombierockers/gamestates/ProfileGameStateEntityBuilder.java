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
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.datastore.Data;
import com.gemserk.datastore.DataStore;
import com.google.inject.Inject;

@SuppressWarnings( { "serial", "unused" })
public class ProfileGameStateEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(ProfileGameStateEntityBuilder.class);

	@Inject
	MessageQueue messageQueue;
	
	@Inject
	MessageBuilder messageBuilder;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	Input input;
	
	@Inject
	GlobalProperties globalProperties;
	
	@Inject
	DataStore dataStore;
	
	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));
		property("textFieldSlickImpl", null);

		component(new ReferencePropertyComponent("enterStateHandler") {

			@EntityProperty
			Property<TextFieldSlickImpl> textFieldSlickImpl;

			@EntityProperty
			Property<Rectangle> screenBounds;

			@Handles
			public void enterNodeState(Message message) {
				TextField textField = new TextField("", new ClipboardAwtImpl());
				textField.setMaxLength(20);
				textFieldSlickImpl.set(new TextFieldSlickImpl(textField));
				
				Data profile = (Data) globalProperties.getProperties().get("profile");
				if (!profile.getTags().contains("guest"))
					textField.insert((String)profile.getValues().get("name"));
				
				input.addKeyListener(textFieldSlickImpl.get());
				
				Entity profileScreen = templateProvider.getTemplate("screens.profile").instantiate("profileScreen", new HashMap<String, Object>(){{
					put("textFieldSlickImpl", textFieldSlickImpl);
					put("screenBounds", screenBounds.get());
					put("onBackButton", "onBackButton");
				}});
				
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(profileScreen, entity));
			}
			
			@Handles
			public void leaveNodeState(Message message) {
				input.removeKeyListener(textFieldSlickImpl.get());
			}

			@Handles
			public void onBackButton(Message message) {
				messageQueue.enqueue(messageBuilder.newMessage("menu").get());
			}
			
			@Handles
			public void onProfileUpdated(Message message) {
				// save profile? who does this work, the screen, the gamestate?
				
				// should update current profile instead...
				
				Data profile = (Data) globalProperties.getProperties().get("profile");
				profile.getTags().remove("guest");
				profile.getValues().put("name", textFieldSlickImpl.get().getTextField().getText());
				
				dataStore.update(profile);
				
				messageQueue.enqueue(messageBuilder.newMessage("menu").get());
			}
			
		});
		
	}
}