package com.gemserk.games.towerofdefense.components;

import com.gemserk.componentsengine.game.Game;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Properties;
import com.google.inject.Inject;

public class RestartSceneComponent extends Component {
	@Inject
	Game game;

	public RestartSceneComponent(String name) {
		super(name);
	}

	@Override
	public void handleMessage(Message message) {

		if (!(message instanceof GenericMessage))
			return;

		GenericMessage genericMessage = (GenericMessage) message;

		if (!genericMessage.getId().equals("loadScene"))
			return;

		String sceneId = (String) Properties.property("scene").getValue(
				genericMessage);

		game.loadScene(sceneId);

	}
}