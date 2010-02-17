package com.gemserk.games.towerofdefense;

import java.text.MessageFormat;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.componentsengine.scene.Scene;

public class LabelComponent extends ReflectionComponent {
	
	private PropertyLocator<Vector2f> positionProperty;
	private PropertyLocator<String> messageProperty;
	private PropertyLocator<Object> valueProperty;
	

	public LabelComponent(String id) {
		super(id);
		positionProperty = Properties.property(id, "position");
		messageProperty = Properties.property(id, "message");
		valueProperty = Properties.property(id, "value");
	}

	public void handleMessage(SlickRenderMessage message) {
		Scene scene = message.getScene();
		Graphics graphics = message.getGraphics();
		graphics.pushTransform();
		{
			Vector2f position = positionProperty.getValue(scene);
			graphics.translate(position.x, position.y);
			
			String formatMessage = messageProperty.getValue(scene);
			String text = MessageFormat.format(formatMessage, valueProperty.getValue(scene));
			
			graphics.drawString(text, 0, 0);
		}
		graphics.popTransform();
	}
}