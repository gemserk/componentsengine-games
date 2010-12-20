package com.gemserk.games.zombierockers.gamestates;

import java.util.ArrayList;
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
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickCallableRenderObject;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.games.zombierockers.PathTraversal;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class EditorEntityBuilder extends EntityBuilder {

	private static final Logger logger = LoggerFactory.getLogger(EditorEntityBuilder.class);

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

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	class PathEditorEntityBuilder extends EntityBuilder {
		@Override
		public void build() {

			property("pathTraversal", parameters.get("pathTraversal"));
			property("delta", 1);
			property("direction", 0);
			property("velocity", parameters.get("velocity"));
			property("enabled", parameters.get("enabled"));

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

			component(new FieldsReflectionComponent("movementhandler") {

				@EntityProperty
				Boolean enabled;

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
					if (!enabled)
						return;
					
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
					if (!enabled)
						return;
					
					System.out.println(pathTraversal.getDistanceFromOrigin());
				}

			});

		}
	}

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));
		property("level", parameters.get("level"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");
		Map<String, Object> level = Properties.getValue(entity, "level");

		property("backgroundImage", resourceManager.get(level.get("background")));

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", -1000);
				propertyRef("image", "backgroundImage");
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
						hold("up", "advance");
						hold("down", "retreat");
						press("prior", "accelerate");
						press("next", "deaccelerate");
						press("space", "printDistance");

						press("tab", "switchPathEditor");
					}
				});
			}

		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("velocityLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontFps"));
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
				put("font", resourceManager.get("FontFps"));
				put("position", slick.vector(60f, 40f));
				put("color", slick.color(0f, 0f, 0f, 1f));
				put("bounds", slick.rectangle(-50f, -20f, 100f, 40f));
				put("align", "left");
				put("valign", "top");
				put("message", new FixedProperty(entity) {
					public Object get() {
						Entity currentPathEditor = Properties.getValue(getHolder(), "currentPathEditor");
						PathTraversal pathTraversal = Properties.getValue(currentPathEditor, "pathTraversal");
						Vector2f position = pathTraversal.getPosition();
						return "POS: (" + position.x + "," + position.y + ")";
					};
				});
			}
		}));

		property("velocity", 100f / 1000f);

		ArrayList<Map<String, Object>> paths = (ArrayList<Map<String, Object>>) level.get("paths");

		ArrayList<Entity> pathEditors = new ArrayList<Entity>();

		for (int i = 0; i < paths.size(); i++) {

			final Map<String, Object> pathProperties = paths.get(i);
			
			final int index = i;

			String pathId = (String) (pathProperties.get("pathId") != null ? pathProperties.get("pathId") : "path");

			final Path path = new Path(slickSvgUtils.loadPoints((String) pathProperties.get("path"), pathId));

			Entity pathEditor = javaEntityTemplateProvider.get().with(new PathEditorEntityBuilder()).instantiate("pathEditor_" + i, new HashMap<String, Object>() {
				{
					put("pathTraversal", new PathTraversal(path, 0));
					put("velocity", new ReferenceProperty<Object>("velocity", entity));
					put("enabled", new FixedProperty(entity) {
						@Override
						public Object get() {
							ArrayList pathEditors = Properties.getValue(getHolder(), "pathEditors");
							Integer currentPathEditorIndex = Properties.getValue(getHolder(), "currentPathEditorIndex");
							return currentPathEditorIndex == index;
						}
					});
				}
			});

			child(pathEditor);

			pathEditors.add(pathEditor);
		}

		property("pathEditors", pathEditors);
		property("currentPathEditorIndex", 0);

		property("currentPathEditor", new FixedProperty(entity) {
			@Override
			public Object get() {
				ArrayList pathEditors = Properties.getValue(getHolder(), "pathEditors");
				Integer currentPathEditorIndex = Properties.getValue(getHolder(), "currentPathEditorIndex");
				return pathEditors.get(currentPathEditorIndex);
			}
		});
		
		component(new FieldsReflectionComponent("switchPathEditorHandler") {

			@EntityProperty(readOnly=true)
			ArrayList pathEditors;
			
			@EntityProperty
			Integer currentPathEditorIndex;

			@Handles
			public void switchPathEditor(Message message) {
				currentPathEditorIndex = (currentPathEditorIndex + 1) % pathEditors.size();
			}

		});

	}
}