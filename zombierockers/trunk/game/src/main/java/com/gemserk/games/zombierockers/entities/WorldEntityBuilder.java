package com.gemserk.games.zombierockers.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ExplosionComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover;
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertiesWrapper;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickCallableRenderObject;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.triggers.Trigger;
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSprite;
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSpritesRenderObject;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class WorldEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(WorldEntityBuilder.class);

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	MessageQueue messageQueue;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	SlickSvgUtils slickSvgUtils;

	@Inject
	SlickUtils slick;
	
	class BallWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<Image> currentFrame;
		
		@EntityProperty
		Property<Vector2f> position;

		@EntityProperty
		Property<Vector2f> direction;

		@EntityProperty
		Property<Color> color;
		
		@EntityProperty
		Property<Vector2f> size;
		
		@EntityProperty
		Property<Integer> layer;
		
	}

	@Override
	public void build() {

		tags("world");

		final Rectangle screenBounds = (Rectangle) parameters.get("screenBounds");
		final Map<String, Object> level = (Map<String, Object>) parameters.get("level");

		property("bounds", screenBounds);
		property("ballsQuantity", 0);
		property("baseReached", false);

		property("level", level);

		final Path levelPath = new Path(slickSvgUtils.loadPoints((String) level.get("path"), "path"));

		property("path", levelPath);

		component(new OutOfBoundsRemover("outofboundsremover")).withProperties(new ComponentProperties() {
			{
				property("tags", new String[] { "bullet" });
				propertyRef("bounds");
			}
		});

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("image", slick.getResources().image((String) level.get("background")));
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", -1000);
			}
		});

		// // placeables renderer

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
					final Image image = slick.getResources().image((String) placeable.get("image"));

					renderer.enqueue(new SlickCallableRenderObject(layer) {

						@Override
						public void execute(Graphics g) {
							g.pushTransform();
							g.translate(position.x + 5, position.y + 5);
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

		component(new ExplosionComponent("explosionsComponent"));

		child(templateProvider.getTemplate("zombierockers.entities.base").instantiate("base", new HashMap<String, Object>() {
			{
				put("position", levelPath.getPoint(levelPath.getPoints().size() - 1));
				put("radius", 15f);
			}
		}));

		child(templateProvider.getTemplate("zombierockers.entities.spawner").instantiate("spawner", new HashMap<String, Object>() {
			{
				put("path", levelPath);
				put("ballsQuantity", level.get("ballsQuantity"));
				put("ballDefinitions", level.get("ballDefinitions"));
				put("pathProperties", level.get("pathProperties"));
				put("subPathDefinitions", level.get("subPathDefinitions"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.entities.limbo").instantiate("limbo", new HashMap<String, Object>() {
			{
				put("path", levelPath);
			}
		}));

		final float offset = 0f;

		child(templateProvider.getTemplate("zombierockers.entities.cannon").instantiate("cannon", new HashMap<String, Object>() {
			{
				put("bounds", slick.rectangle(20f + offset, 20f, screenBounds.getMaxX() - 40f - offset, screenBounds.getMaxY() - 40f));
				put("ballDefinitions", level.get("ballDefinitions"));
				put("collisionMap", level.get("collisionMap"));
				put("subPathDefinitions", level.get("subPathDefinitions"));
			}
		}));

		child(templateProvider.getTemplate("zombierockers.entities.segmentsmanager").instantiate("segmentsManager", new HashMap<String, Object>() {
			{
				put("baseReached", new ReferenceProperty<Object>("baseReached", entity));
			}
		}));

		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("s", "spawn");
						press("d", "dumpDebug");
						press("t", "concurrentHit");
					}
				});
			}

		}));

		// dump debug...

		CountDownTimer startTimer = new CountDownTimer(2000);
		startTimer.reset();

		property("startTimer", startTimer);

		component(new TimerComponent("startTimerComponent")).withProperties(new ComponentProperties() {
			{
				property("trigger", new Trigger() {

					@Override
					public void trigger(Object... parameters) {
						trigger();
					}

					@Override
					public void trigger() {
						messageQueue.enqueue(new Message("spawn"));
					}
				});
				propertyRef("timer", "startTimer");
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("pausedLabel", new HashMap<String, Object>() {
			{
				put("position", slick.vector(screenBounds.getMaxX() - 60f, screenBounds.getMinY() + 30f));
				put("fontColor", slick.color(0f, 0f, 0f, 1f));
				put("bounds", slick.rectangle(-50f, -20f, 100f, 40f));
				put("align", "left");
				put("valign", "top");
				put("message", new FixedProperty(entity) {
					public Object get() {
						return "Balls: " + Properties.getValue(getHolder(), "ballsQuantity").toString();
					};
				});
			}
		}));

		component(new ReferencePropertyComponent("baseReachedHandler") {
			@Handles
			public void baseReached(Message message) {
				Properties.setValue(entity, "baseReached", true);
			}
		});

		component(new ReferencePropertyComponent("gameOverChecker") {

			@Handles
			public void update(Message message) {
				Collection<Entity> limbosNotDone = entity.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("limbo"), new Predicate<Entity>() {
					@Override
					public boolean apply(Entity limbo) {
						Boolean done = Properties.getValue(limbo, "done");
						return !done.booleanValue();
					}
				}));
				boolean allLimbosDone = limbosNotDone.isEmpty();
				Boolean baseReached = Properties.getValue(entity, "baseReached");

				if (!baseReached.booleanValue() && !allLimbosDone)
					return;

				Collection<Entity> segments = entity.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("segment")));
				if (!segments.isEmpty())
					return;

				final boolean win = allLimbosDone && !baseReached;

				messageQueue.enqueue(new Message("gameover", new PropertiesMapBuilder() {
					{
						property("win", win);
					}
				}.build()));
			}

		});

		property("ballShadowImage", slick.getResources().image("ballshadow"));

		component(new ReferencePropertyComponent("ballRenderer") {
			
			BallWrapper ball = new BallWrapper();

			@Handles
			public void render(Message message) {

				Collection<Entity> allBalls = entity.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("ball"), new Predicate<Entity>() {
					@Override
					public boolean apply(Entity ball) {
						Boolean alive = Properties.getValue(ball, "alive");
						return alive;
					}
				}));

				if (allBalls.isEmpty())
					return;

				RenderQueue renderer = Properties.getValue(message, "renderer");
				Image ballShadowImage = Properties.getValue(entity, "ballShadowImage");

				Map<String, Object> level = Properties.getValue(entity, "level");
				Multimap<Integer, Entity> ballsByLayer = HashMultimap.create();

				for (Entity ballEntity : allBalls) {
					ball.wrap(ballEntity);
					ballsByLayer.put(ball.layer.get(), ballEntity);
				}
				
				Color shadowColor = slick.color(1, 1, 1, 1);
				Vector2f shadowDispacement = slick.vector(3,3);

				for (Integer layer : ballsByLayer.keySet()) {
					Collection<Entity> balls = ballsByLayer.get(layer);

					Map<Integer, Image> alphaMasks = (Map<Integer, Image>) level.get("alphaMasks");
					Image alphaMask = null;
					if (alphaMasks != null)
						alphaMask = alphaMasks.get(layer);

					AlphaMaskedSpritesRenderObject ballsRenderer = new AlphaMaskedSpritesRenderObject(layer, alphaMask, new ArrayList<AlphaMaskedSprite>(balls.size()));
					AlphaMaskedSpritesRenderObject shadowRenderer = new AlphaMaskedSpritesRenderObject(layer - 1, alphaMask, new ArrayList<AlphaMaskedSprite>(balls.size()));

					List<AlphaMaskedSprite> ballsSprites = ballsRenderer.getSprites();
					List<AlphaMaskedSprite> shadowSprites = shadowRenderer.getSprites();
					
					for (Entity ballEntity : balls) {
						ball.wrap(ballEntity);
						
						Image image = ball.currentFrame.get();
						Vector2f position = ball.position.get();
						Vector2f direction = ball.direction.get();
						Vector2f size = ball.size.get();
						Color color = ball.color.get();
						
						ballsSprites.add(new AlphaMaskedSprite(image, position, direction, size, color));
						shadowSprites.add(new AlphaMaskedSprite(ballShadowImage, position.copy().add(shadowDispacement), direction, size, shadowColor));
					}
					
					renderer.enqueue(ballsRenderer);
					renderer.enqueue(shadowRenderer);
				}

			}

		});
	}
}
