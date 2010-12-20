package com.gemserk.games.zombierockers.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ExplosionComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.OutOfBoundsRemover;
import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.commons.components.TimerComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
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
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertiesWrapper;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.render.RenderQueue;
import com.gemserk.componentsengine.slick.render.SlickCallableRenderObject;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.triggers.Trigger;
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSprite;
import com.gemserk.games.zombierockers.renderer.AlphaMaskedSpritesRenderObject;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
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

	@Inject
	ResourceManager resourceManager;

	class BallWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<Resource<Image>> currentFrame;

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

		property("points", parameters.get("points"));

		property("level", level);

		// pre load needed images?
		Map<Integer, String> alphaMasks = (Map<Integer, String>) level.get("alphaMasks");
		if (alphaMasks != null) {
			for (Integer key : alphaMasks.keySet()) {
				resourceManager.get(alphaMasks.get(key));
			}
		}

		component(new OutOfBoundsRemover("outofboundsremover")).withProperties(new ComponentProperties() {
			{
				property("tags", new String[] { "bullet" });
				propertyRef("bounds");
			}
		});

		property("backgroundImageResource", resourceManager.get(level.get("background")));

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", -1000);
				propertyRef("image", "backgroundImageResource");
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

		component(new ExplosionComponent("explosionsComponent"));

		component(new FieldsReflectionComponent("explosionSound") {

			@EntityProperty
			Resource<Sound> explosionSound;

			@Handles
			public void explosion(Message message) {
				explosionSound.get().play();
			}

		}).withProperties(new ComponentProperties() {
			{
				property("explosionSound", resourceManager.get("Explosion"));
			}
		});

		ArrayList<Map<String, Object>> paths = (ArrayList<Map<String, Object>>) level.get("paths");

		for (int i = 0; i < paths.size(); i++) {

			final Map<String, Object> pathProperties = paths.get(i);

			String pathId = (String) (pathProperties.get("pathId") != null ? pathProperties.get("pathId") : "path");

			final Path path = new Path(slickSvgUtils.loadPoints((String) pathProperties.get("path"), pathId));

			child(templateProvider.getTemplate("zombierockers.entities.path").instantiate("path_" + i, new HashMap<String, Object>() {
				{
					put("path", path);
					put("ballsQuantity", pathProperties.get("ballsQuantity"));
					put("pathProperties", pathProperties.get("pathProperties"));
					put("subPathDefinitions", pathProperties.get("subPathDefinitions"));

					put("ballDefinitions", level.get("ballDefinitions"));
				}
			}));
		}

		final float offset = 0f;

		child(templateProvider.getTemplate("zombierockers.entities.cannon").instantiate("cannon", new HashMap<String, Object>() {
			{
				put("bounds", slick.rectangle(20f + offset, 20f, screenBounds.getMaxX() - 40f - offset, screenBounds.getMaxY() - 40f));
				put("ballDefinitions", level.get("ballDefinitions"));
				put("collisionMap", level.get("collisionMap"));
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

		property("font", new FixedProperty(entity) {
			@Override
			public Object get() {
				return resourceManager.get("FontPlayingLabel").get();
			}
		});

		component(new ReferencePropertyComponent("baseReachedHandler") {
			@Handles
			public void baseReached(Message message) {
				Properties.setValue(entity, "baseReached", true);
			}
		});

		component(new ReferencePropertyComponent("gameOverChecker") {

			boolean gameOver = false;

			@Handles
			public void update(Message message) {
				if (gameOver)
					return;

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

				messageQueue.enqueue(new Message("levelFinished", new PropertiesMapBuilder() {
					{
						property("win", win);
					}
				}.build()));

				gameOver = true;
			}

		});

		component(new FieldsReflectionComponent("pointsHandler") {

			@EntityProperty
			Integer points;

			@EntityProperty
			Integer chainCount;

			@EntityProperty
			Integer minChainCount;

			@EntityProperty
			Integer comboCount;

			@EntityProperty
			Integer maxChainCount;

			@EntityProperty
			Integer maxComboCount;

			@EntityProperty
			Entity chainDetectionBall;

			@Inject
			ChildrenManagementMessageFactory childrenManagementMessageFactory;

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void explodeBall(Message message) {
				List<Entity> balls = Properties.getValue(message, "balls");

				final StringBuilder chainMessageStringBuilder = new StringBuilder();

				if (balls.contains(chainDetectionBall)) {
					chainCount++;
					chainDetectionBall = null;
					// reset chain ball to avoid cancelling chain when next series not detected and this ball on balls collection
					if (logger.isDebugEnabled())
						logger.debug("chain incremented to " + chainCount);
				}

				comboCount++;
				if (logger.isDebugEnabled())
					logger.debug("combo count incremented to " + comboCount);

				if (comboCount >= maxComboCount) {
					if (logger.isDebugEnabled())
						logger.debug("max combo count reached, reseting to 1");
					// show a message "max combo reached!! excelent, blah blah"
					comboCount = 1;
				}

				int ballPoints = 30;
				int chainPoints = 100;

				int newPoints = ballPoints * balls.size();

				if (chainCount >= minChainCount)
					newPoints += chainCount * chainPoints;

				final int messagePoints = newPoints;

				newPoints *= comboCount;

				points += newPoints;

				final Vector2f position = new Vector2f();
				final Color ballColor = Properties.getValue(balls.get(0), "color");

				for (int i = 0; i < balls.size(); i++) {
					Entity ball = balls.get(i);
					Vector2f ballPosition = Properties.getValue(ball, "position");
					position.x += ballPosition.x;
					position.y += ballPosition.y;
				}

				position.scale(1f / (float) balls.size()).add(slick.vector(0, -40));

				if (chainCount >= maxChainCount) {
					if (logger.isDebugEnabled())
						logger.debug("max chain count reached, reseting to 0 ");
					chainCount = 0;
					chainMessageStringBuilder.append("MAX chain bonus!");
				} else if (chainCount >= minChainCount) {
					chainMessageStringBuilder.append(chainCount);
					chainMessageStringBuilder.append("x chain bonus!");
				}

				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(messagePoints);
				stringBuilder.append(" points");

				if (comboCount > 1) {
					stringBuilder.append(" x");
					stringBuilder.append(comboCount);
				}

				Entity pointsMessageEntity = templateProvider.getTemplate("zombierockers.gui.bonusmessage").instantiate("pointsMessage", new HashMap<String, Object>() {
					{
						put("position", position);
						put("bounds", slick.rectangle(-50f, -20f, 100f, 40f));
						put("startColor", slick.color(ballColor.r, ballColor.g, ballColor.b, 0f));
						put("endColor", slick.color(ballColor.r, ballColor.g, ballColor.b, 1f));
						put("startSize", slick.vector(0.6f, 0.6f));
						put("endSize", slick.vector(1.0f, 1.0f));
						put("align", "center");
						put("valign", "center");
						put("layer", 40);
						put("lines", new String[] { stringBuilder.toString(), chainMessageStringBuilder.toString() });
					}
				});

				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(pointsMessageEntity, entity));
			}

			@Handles
			public void seriesNotDetected(Message message) {
				Entity ball = Properties.getValue(message, "ball");
				if (ball == chainDetectionBall) {
					chainCount = 0;
					chainDetectionBall = null;
					// cancel chain count because bullet didn't make a series

					if (logger.isDebugEnabled())
						logger.debug("chain reseted because bullet didn't make a ball group");
				}

				comboCount = 0;
				if (logger.isDebugEnabled())
					logger.debug("combo count reseted!");
			}

			@Handles
			public void bulletHit(Message message) {
				// ball added to a segment from a bullet hit, start a new chain detection with that ball
				Entity bullet = Properties.getValue(message, "source");
				chainDetectionBall = Properties.getValue(bullet, "ball");

				if (logger.isDebugEnabled())
					logger.debug("chain started with ball.id: " + chainDetectionBall.getId());

				comboCount = 0;
				if (logger.isDebugEnabled())
					logger.debug("combo count reseted because bullet hit!");
			}

		}).withProperties(new ComponentProperties() {
			{
				property("chainCount", 0);
				property("minChainCount", 2);
				property("maxChainCount", 8);
				property("maxComboCount", 8);
				property("chainDetectionBall", null);
				property("comboCount", 0);
			}
		});

		property("ballShadowImage", resourceManager.get("ballshadow"));

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
				Resource<Image> ballShadowImage = Properties.getValue(entity, "ballShadowImage");

				Map<String, Object> level = Properties.getValue(entity, "level");
				Multimap<Integer, Entity> ballsByLayer = HashMultimap.create();

				for (Entity ballEntity : allBalls) {
					ball.wrap(ballEntity);
					ballsByLayer.put(ball.layer.get(), ballEntity);
				}

				Color shadowColor = slick.color(1, 1, 1, 1);
				Color ballColor = slick.color(1f, 1f, 1f, 1f);
				Vector2f shadowDispacement = slick.vector(3, 3);

				Map<Integer, String> alphaMasks = (Map<Integer, String>) level.get("alphaMasks");

				for (Integer layer : ballsByLayer.keySet()) {
					Collection<Entity> balls = ballsByLayer.get(layer);

					Image alphaMask = null;
					if (alphaMasks != null) {
						String alphaMaskId = alphaMasks.get(layer);
						if (alphaMaskId != null) {
							Resource<Image> imageResource = resourceManager.get(alphaMasks.get(layer));
							alphaMask = imageResource.get();
						}
					}

					AlphaMaskedSpritesRenderObject ballsRenderer = new AlphaMaskedSpritesRenderObject(layer, alphaMask, new ArrayList<AlphaMaskedSprite>(balls.size()));
					AlphaMaskedSpritesRenderObject shadowRenderer = new AlphaMaskedSpritesRenderObject(layer - 1, alphaMask, new ArrayList<AlphaMaskedSprite>(balls.size()));

					List<AlphaMaskedSprite> ballsSprites = ballsRenderer.getSprites();
					List<AlphaMaskedSprite> shadowSprites = shadowRenderer.getSprites();

					for (Entity ballEntity : balls) {
						ball.wrap(ballEntity);

						Resource<Image> image = ball.currentFrame.get();
						Vector2f position = ball.position.get();
						Vector2f direction = ball.direction.get();
						Vector2f size = ball.size.get();
						// Color color = ball.color.get();

						ballsSprites.add(new AlphaMaskedSprite(image.get(), position, direction, size, ballColor));
						shadowSprites.add(new AlphaMaskedSprite(ballShadowImage.get(), position.copy().add(shadowDispacement), direction, size, shadowColor));
					}

					renderer.enqueue(ballsRenderer);
					renderer.enqueue(shadowRenderer);
				}

			}

		});
	}
}
