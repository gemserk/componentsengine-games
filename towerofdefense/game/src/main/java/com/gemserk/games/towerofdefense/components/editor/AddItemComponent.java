package com.gemserk.games.towerofdefense.components.editor;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.componentsengine.world.World;
import com.gemserk.games.towerofdefense.components.Component;
import com.google.inject.Inject;

public class AddItemComponent extends Component {

	World world;

	TemplateProvider templateProvider;

	@Inject
	public void setTemplateProvider(TemplateProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	@Inject
	public void setWorld(World world) {
		this.world = world;
	}

	public AddItemComponent(String id) {
		super(id);
	}

	@Override
	public void handleMessage(Message message) {

		if (message instanceof GenericMessage) {
			GenericMessage genericMessage = (GenericMessage) message;
			if (genericMessage.getId().equals("addItemAction")) {
				Vector2f position = (Vector2f) Properties.property("value")
						.getValue(genericMessage);
				onAddItem(position);
			}
		}

	}

	private void onAddItem(final Vector2f position) {

		Entity entity = templateProvider.getTemplate("todh.entities.tower")
				.instantiate("tower_" + position.x,
						new HashMap<String, Object>() {
							{
								put("position", position.copy());
								put("direction", new Vector2f(1.0f, 0.0f));
								put("size", 20.0f);
								put("color", Color.green);
								put("radius", 90.0f);

								put("template", "todh.entities.bullet");

								put("reloadTime", 1500);

								put("damage", 5.0f);
							}
						});
		world.queueAddEntity(entity);
	}

}
