package com.gemserk.games.zombierockers.entities;

import java.util.HashMap;

import net.sf.json.JSONArray;

import org.newdawn.slick.GameContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.utils.EntityDumper;
import com.gemserk.resources.ResourcesMonitor;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class UtilsEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(UtilsEntityBuilder.class);

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@SuppressWarnings( { "serial", "unused" })
	@Override
	public void build() {

		child(templateProvider.getTemplate("commons.entities.screenshotGrabber").instantiate("screenshotEntity", new HashMap<String, Object>() {
			{
				put("prefix", "zombierockers-");
				put("extension", "png");
			}
		}));

		child(templateProvider.getTemplate("commons.entities.fps").instantiate("fpsLabel", new HashMap<String, Object>() {
			{
				put("enabled", new FixedProperty(entity) {
					@Override
					public Object get() {
						return globalProperties.getProperties().get("showFps");
					}

					@Override
					public void set(Object value) {
						globalProperties.getProperties().put("showFps", value);
					}
				});
			}
		}));

		component(new ReflectionComponent("closeGameHandler") {

			@Inject
			GameContainer container;

			@Handles
			public void closeGame(Message message) {
				container.exit();
			}

		});

		component(new ReferencePropertyComponent("resouceReloadingHandler") {

			@Inject
			ResourcesMonitor resourcesMonitor;

			@Handles
			public void reloadAllResources(Message message) {
				resourcesMonitor.reloadAll();
				if (logger.isInfoEnabled())
					logger.info("Reloading all resources");
			}

		});

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("x", "dumpEntities");
						press("n", "nextLevel");
						press("k", "takeScreenshot");
						press("f", "toggleFps");
						press("u", "reloadResources");
						press("back", "closeGame");
						press("f5", "reloadAllResources");
					}
				});
			}

		}));

		component(new ReflectionComponent("dumpEntitiesHandler") {

			@Handles
			public void dumpEntities(Message message) {
				System.out.println(JSONArray.fromObject(new EntityDumper().dumpEntity(entity.getRoot())).toString(4));
			}

		});

	}
}