package com.gemserk.games.towerofdefense.components;

import org.newdawn.slick.Graphics;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;

public class Component extends com.gemserk.componentsengine.components.Component {

	public Component(String id) {
		super(id);
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof UpdateMessage) {
			UpdateMessage updateMessage = (UpdateMessage) message;
			update(updateMessage.getEntity(), updateMessage.getDelta());
		} else if (message instanceof SlickRenderMessage) {
			SlickRenderMessage slickRenderMessage = (SlickRenderMessage) message;
			render(slickRenderMessage.getGraphics(), slickRenderMessage.getEntity());
		}
	}

	protected void render(Graphics graphics, Entity entity) {

	}

	protected void update(Entity entity, int delta) {

	}

}
