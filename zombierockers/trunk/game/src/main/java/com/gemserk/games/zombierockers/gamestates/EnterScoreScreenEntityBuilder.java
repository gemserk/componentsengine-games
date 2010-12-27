package com.gemserk.games.zombierockers.gamestates;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.gui.ClipboardAwtImpl;
import com.gemserk.componentsengine.commons.gui.TextField;
import com.gemserk.componentsengine.commons.gui.TextFieldSlickImpl;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickCallableRenderObject;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Scores;
import com.google.inject.Inject;

@SuppressWarnings( { "unchecked", "unused" })
public class EnterScoreScreenEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(EnterScoreScreenEntityBuilder.class);

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Inject
	ResourceManager resourceManager;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	Scores scores;

	@Inject
	Input input;

	@SuppressWarnings("serial")
	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});
		
		property("textFieldSlickImpl", null);
		
		component(new ReferencePropertyComponent("enterStateHandler") {
			
			@EntityProperty
			Property<TextFieldSlickImpl> textFieldSlickImpl;

			@Handles
			public void enterNodeState(Message message) {
				TextField textField = new TextField("", new ClipboardAwtImpl());
				textFieldSlickImpl.set(new TextFieldSlickImpl(textField));
				input.addKeyListener(textFieldSlickImpl.get());
			}

		});
		
		component(new ReferencePropertyComponent("leaveStateHandler") {

			@EntityProperty
			Property<TextFieldSlickImpl> textFieldSlickImpl;
			
			@Handles
			public void leaveNodeState(Message message) {
				input.removeKeyListener(textFieldSlickImpl.get());
			}

		});
		
		property("position", slick.vector(400, 300));
		property("color", slick.color(0f,0f,0f,1f));
		
		component(new ReferencePropertyComponent("renderTextField") {

			@EntityProperty
			Property<Vector2f> position;

			@EntityProperty
			Property<Color> color;
			
			@EntityProperty
			Property<TextFieldSlickImpl> textFieldSlickImpl;
			
			@Handles
			public void render(Message message) {
				RenderQueue renderQueue = Properties.getValue(message, "renderer");
				
				renderQueue.enqueue(new SlickCallableRenderObject(100) {
					
					@Override
					public void execute(Graphics graphics) {

						graphics.setColor(color.get());
						graphics.drawString(textFieldSlickImpl.get().getTextField().getText(), position.get().x, position.get().y);
						
					}
				});
				
			}

		});


	}
}