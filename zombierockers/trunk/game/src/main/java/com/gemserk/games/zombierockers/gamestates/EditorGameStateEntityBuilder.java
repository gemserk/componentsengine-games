package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class EditorGameStateEntityBuilder extends EntityBuilder {
	
	@Inject
	MessageQueue messageQueue;
	
	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;
	
	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));
		property("level", parameters.get("level"));

		component(new FieldsReflectionComponent("reloadLevel-enternodestate") {

			@EntityProperty
			Map<String, Object> level;

			@EntityProperty
			Rectangle screenBounds;

			@Handles
			public void enterNodeState(Message message) {
				
				Entity editorEntity = templateProvider.getTemplate("zombierockers.entities.editor").instantiate("editor_entity", new HashMap<String, Object>() {
					{
						put("level", level);
						put("screenBounds", screenBounds);
					}
				});
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(editorEntity, entity));
			}

		});
		
		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("escape", "restartLevel");
					}
				});
			}

		}));

	}
}