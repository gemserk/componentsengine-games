package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickCallableRenderObject;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.PathTraversal;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class EditorGameStateEntityBuilder extends EntityBuilder {

	private static final Logger logger = LoggerFactory.getLogger(EditorGameStateEntityBuilder.class);

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	SlickSvgUtils slickSvgUtils;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	ResourceManager resourceManager;

	@Override
	public void build() {

		final Rectangle screenBounds = (Rectangle) parameters.get("screenBounds");

		property("bounds", parameters.get("screenBounds"));
		property("level", parameters.get("level"));
		
		Map<String, Object> level = Properties.getValue(entity, "level");

		Path path = new Path(slickSvgUtils.loadPoints((String) level.get("path"), "path"));
		property("path", path);

		property("backgroundImageResource", resourceManager.get(level.get("background")));
		
		component(new FieldsReflectionComponent("reloadLevel-enternodestate") {
			
			@EntityProperty
			Map<String, Object> level;

			@EntityProperty
			Path path;
			
			@EntityProperty
			Resource backgroundImageResource;
			
			@EntityProperty
			PathTraversal pathTraversal;
			
			@Handles
			public void enterNodeState(Message message) {
				path = new Path(slickSvgUtils.loadPoints((String) level.get("path"), "path"));
				backgroundImageResource =  resourceManager.get(level.get("background"));
				pathTraversal = new PathTraversal(path, 0);
			}
			
		});

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", -1000);

				property("image", new FixedProperty(entity) {
					@Override
					public Object get() {
						Resource resource = Properties.getValue(getHolder(), "backgroundImageResource");
						return resource.get();
					}
				});
			}
		});

		// TODO: create the placeables as entities instead....
		component(new ReferencePropertyComponent("placeablesRender") {

			@EntityProperty
			Property<Map<String, Object>> level;

			@Handles
			public void render(Message message) {

				RenderQueue renderer = Properties.getValue(message, "renderer");

				List<Map<String, Object>> placeables = (List<Map<String, Object>>) level.get().get("placeables");

				for (Map<String, Object> placeable : placeables) {
					final Vector2f position = (Vector2f) placeable.get("position");
					Integer layer = (Integer) placeable.get("layer");

					Resource<Image> imageResource = resourceManager.get(placeable.get("image"));
					final Image image = imageResource.get();

					renderer.enqueue(new SlickCallableRenderObject(layer) {

						@Override
						public void execute(Graphics g) {
							g.pushTransform();
							g.translate(position.x, position.y);
							g.drawImage(image, (float) -(image.getWidth() / 2), (float) -(image.getHeight() / 2));
							g.popTransform();
						}

					});
				}

			}

		}).withProperties(new ComponentProperties() {
			{
				propertyRef("level");
			}
		});

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("escape", "restartLevel");
						hold("up", "advance");
						hold("down", "retreat");
						press("prior", "accelerate");
						press("next", "deaccelerate");
						press("space", "printDistance");
					}
				});
			}

		}));

		property("pathTraversal", new PathTraversal(path, 0));
		property("velocity", 100f / 1000f);
		property("delta", 1);
		property("direction", 0);

		component(new CircleRenderableComponent("circlerendererbig")).withProperties(new ComponentProperties() {
			{
				property("position", new FixedProperty(entity) {
					@Override
					public Object get() {
						PathTraversal pathTraversal = Properties.getValue(getHolder(), "pathTraversal");
						return pathTraversal.getPosition();
					}
				});
				property("radius", 16f);
				property("lineColor", slick.color(0, 0, 0, 1));
				property("fillColor", slick.color(0, 0, 0, 0.1f));
				property("layer", 1000);
			}
		});

		component(new CircleRenderableComponent("circlerenderersmall")).withProperties(new ComponentProperties() {
			{
				property("position", new FixedProperty(entity) {
					@Override
					public Object get() {
						PathTraversal pathTraversal = Properties.getValue(getHolder(), "pathTraversal");
						return pathTraversal.getPosition();
					}
				});
				property("radius", 1f);
				property("lineColor", slick.color(0, 0, 0, 1));
				property("fillColor", slick.color(0, 0, 0, 0.1f));
				property("layer", 1000);
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("velocityLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(60f, 20f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", slick.rectangle(-50f, -20f, 100f, 40f));
				put("align", "left");
				put("valign", "top");
				put("message", new FixedProperty(entity) {
					public Object get() {
						return "VEL: " + Properties.getValue(getHolder(), "velocity");
					};
				});
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("positionLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(60f, 40f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", slick.rectangle(-50f, -20f, 100f, 40f));
				put("align", "left");
				put("valign", "top");
				put("message", new FixedProperty(entity) {
					public Object get() {
						PathTraversal pathTraversal = Properties.getValue(getHolder(), "pathTraversal");
						Vector2f position = pathTraversal.getPosition();
						return "POS: (" + position.x + "," + position.y + ")";
					};
				});
			}
		}));

		component(new FieldsReflectionComponent("movementhandler") {

			@EntityProperty
			Integer direction;

			@EntityProperty
			Float velocity;

			@EntityProperty
			PathTraversal pathTraversal;

			@Handles
			public void advance(Message message) {
				direction = 1;
			}

			@Handles
			public void retreat(Message message) {
				direction = -1;
			}

			@Handles
			public void update(Message message) {
				Integer delta = Properties.getValue(message, "delta");
				pathTraversal = pathTraversal.add(direction * velocity * delta);
				direction = 0;
			}

			@Handles
			public void accelerate(Message message) {
				velocity = 1.10f * velocity;
			}
			
			@Handles
			public void deaccelerate(Message message) {
				velocity = 0.9f * velocity;
			}
			
			@Handles
			public void printDistance(Message message) {
				System.out.println(pathTraversal.getDistanceFromOrigin());
			}

		});

	}
}