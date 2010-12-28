package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Scores;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings( { "unchecked", "unused" })
public class HighscoresGameStateEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(HighscoresGameStateEntityBuilder.class);

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
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;
	
	@SuppressWarnings("serial")
	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("level", parameters.get("level"));
		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		component(new ReferencePropertyComponent("enterStateHandler") {

			@EntityProperty
			Property<Map<String, Object>> level;
			
			@EntityProperty
			Property<Rectangle> screenBounds;

			@Handles
			public void enterNodeState(Message message) {

				Message sourceMessage = Properties.getValue(message, "message");
				String levelName = Properties.getValue(sourceMessage, "levelName");

				if (levelName == null)
					levelName = (String) level.get().get("name");

				final Set<String> tags = Sets.newHashSet(levelName);

				Entity highscoreScreen = templateProvider.getTemplate("screens.highscores").instantiate("highscoreScreen", new HashMap<String, Object>() {
					{
						put("screenBounds", screenBounds.get());
						put("timeOut", 8000);
						put("ascending", false);
						put("tags", tags);
						put("limit", 10);
					}
				});
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(highscoreScreen, entity));
			}

		});

		child(templateProvider.getTemplate("commons.entities.utils").instantiate("utilsEntity"));
		
		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("escape", "paused");
					}
				});
			}

		}));

	}
}