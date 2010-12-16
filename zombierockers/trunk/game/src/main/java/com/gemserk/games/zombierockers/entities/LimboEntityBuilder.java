package com.gemserk.games.zombierockers.entities;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.Path;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertiesWrapper;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.PathTraversal;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class LimboEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(LimboEntityBuilder.class);

	private static int limboNumber = 1;

	@Override
	public String getId() {
		return MessageFormat.format("limbo-{0}", LimboEntityBuilder.limboNumber);
	}

	class LimboEntity extends PropertiesWrapper {

		@EntityProperty
		Property<Deque<Entity>> deque;

		@EntityProperty
		Property<Entity> segment;

		@EntityProperty
		Property<Boolean> done;

		@EntityProperty
		Property<PathTraversal> nextBallPoint;

		@EntityProperty
		Property<Path> path;

	}

	class BallEntity extends PropertiesWrapper {

		@EntityProperty
		Property<Vector2f> position;

		@EntityProperty
		Property<String> state;

		@EntityProperty
		Property<PathTraversal> pathTraversal;

	}

	@Override
	public void build() {

		LimboEntityBuilder.limboNumber++;

		tags("limbo");

		property("path", parameters.get("path"));
		Path path = Properties.getValue(entity, "path");

		property("deque", new LinkedList<Entity>());
		property("nextBallPoint", new PathTraversal(path, 0, 0).add(32f));
		property("done", false);

		property("segment", null);


		property("isEmpty", new FixedProperty(entity) {
			@Override
			public Object get() {
				Deque<Entity> deque = Properties.getValue(getHolder(), "deque");
				return deque.isEmpty();
			}
		});

		component(new ReferencePropertyComponent("releaseBallsHandler") {

			final LimboEntity limbo = new LimboEntity();

			final BallEntity ball = new BallEntity();

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void releaseBalls(Message message) {

				limbo.wrap(entity);

				if (limbo.deque.get().isEmpty())
					return;

				final Entity ballEntity = limbo.deque.get().pop();
				ball.wrap(ballEntity);

				ball.state.set("spawned");

				// if (logger.isInfoEnabled())
				// logger.info("Ball released from limbo - ballId:{0} - color:{1}", rawBall.getId(), ball.color);

				messageQueue.enqueue(new Message("addNewBall", new PropertiesMapBuilder() {
					{
						property("segment", limbo.segment.get());
						property("ball", ballEntity);
					}
				}.build()));

				Integer quantity = Properties.getValue(entity.getParent(), "ballsQuantity");
				Properties.setValue(entity.getParent(), "ballsQuantity", quantity + 1);

				if (limbo.deque.get().isEmpty()) {
					if (logger.isInfoEnabled())
						logger.info("Last ball in limbo released - limbo.id: " + entity.getId());
					limbo.done.set(true);
				}

			}

		});

		component(new ReferencePropertyComponent("nextBallPointReached") {

			final LimboEntity limbo = new LimboEntity();

			final BallEntity ball = new BallEntity();

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void update(Message message) {

				limbo.wrap(entity);

				Predicate<Entity> predicate = Predicates.and(EntityPredicates.withAllTags("ball"), //
						new Predicate<Entity>() {
							@Override
							public boolean apply(Entity ballEntity) {
								ball.wrap(ballEntity);
								return ball.state.get().equals("spawned");
							}
						}, new Predicate<Entity>() {
							@Override
							public boolean apply(Entity ballEntity) {
								ball.wrap(ballEntity);
								if (ball.pathTraversal.get() != null)
									return limbo.path.get() == ball.pathTraversal.get().getPath();
								return false;
							}
						}, new Predicate<Entity>() {
							@Override
							public boolean apply(Entity ballEntity) {
								ball.wrap(ballEntity);
								return ball.pathTraversal.get().compareTo(limbo.nextBallPoint.get()) > 0;
							}
						});

				Collection<Entity> balls = entity.getRoot().getEntities(predicate);

				if (balls.isEmpty())
					return;

				Entity ballEntity = balls.toArray(new Entity[] {})[0];
				ball.wrap(ballEntity);
				ball.state.set("inWorld");

				messageQueue.enqueue(new Message("releaseBalls"));
			}

		});

		component(new ReferencePropertyComponent("spawnedSegmentHandler") {

			final LimboEntity limbo = new LimboEntity();

			@Inject
			MessageQueue messageQueue;

			@Inject
			ChildrenManagementMessageFactory childrenManagementMessageFactory;
			
			class SpawnedSegmentMessage extends PropertiesWrapper {

				@EntityProperty
				Property<Collection<Entity>> balls;

				@EntityProperty
				Property<Path> path;
				
				@EntityProperty
				Property<Entity> segment;

			}
			
			SpawnedSegmentMessage spawnedSegmentMessage = new SpawnedSegmentMessage();

			@Handles
			public void spawnedSegment(Message message) {
				limbo.wrap(entity);
				spawnedSegmentMessage.wrap(message);
				
				if (limbo.path.get() != spawnedSegmentMessage.path.get()) {
					if (logger.isDebugEnabled()) 
						logger.debug("Spawned segment message for a different limbo");
					return;
				}
				
				Deque<Entity> deque = limbo.deque.get();

				Collection<Entity> balls = spawnedSegmentMessage.balls.get();
				for (Entity ballEntity : balls)
					deque.addLast(ballEntity);

				Entity segment = spawnedSegmentMessage.segment.get();

				limbo.segment.set(segment);

				if (logger.isInfoEnabled())
					logger.info("New segment and balls added to limbo - segment.id:" + segment.getId());

				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(segment, entity.getParent()));
				messageQueue.enqueue(new Message("releaseBalls"));
			}

		});

		component(new ReferencePropertyComponent("baseReachedHandler") {

			final LimboEntity limbo = new LimboEntity();

			@Handles
			public void baseReached(Message message) {
				limbo.wrap(entity);
				limbo.deque.get().clear();
				if (logger.isInfoEnabled())
					logger.info("Limbo cleared because of baseReached");
			}

		});

	}
}
