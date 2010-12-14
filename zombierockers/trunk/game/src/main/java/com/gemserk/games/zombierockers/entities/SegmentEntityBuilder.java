package com.gemserk.games.zombierockers.entities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.genericproviders.GenericProvider;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertiesWrapper;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.PathTraversal;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class SegmentEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(SegmentEntityBuilder.class);

	class SegmentWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<List<Entity>> balls;

		@EntityProperty
		Property<Entity> lastBall;

		@EntityProperty
		Property<Entity> firstBall;

		@EntityProperty
		Property<PathTraversal> pathTraversal;

		@EntityProperty
		Property<Float> speed;

		@EntityProperty
		Property<Boolean> accelerated;

		@EntityProperty
		Property<Float> acceleratedSpeed;

		@EntityProperty
		Property<Boolean> baseReached;

		@EntityProperty
		Property<Float> speedWhenReachBase;

		@EntityProperty
		Property<Float> minSpeedFactor;

		@EntityProperty
		Property<Float> maxSpeed;

		@EntityProperty
		Property<Float> pathLength;

		@EntityProperty
		Property<Float> accelerationStopPoint;
		
		@EntityProperty
		Property<InstantiationTemplate> segmentTemplate;

		public List<Entity> getBalls() {
			return balls.get();
		}

		public Float getSpeed() {
			return speed.get();
		}

		public void setSpeed(Float speed) {
			this.speed.set(speed);
		}

	}

	class BallWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<PathTraversal> pathTraversal;

		@EntityProperty
		Property<PathTraversal> newPathTraversal;

		@EntityProperty
		Property<Entity> segment;

		@EntityProperty
		Property<Vector2f> position;

		@EntityProperty
		Property<Boolean> isGrownUp;

		@EntityProperty
		Property<Boolean> alive;

		@EntityProperty
		Property<Float> radius;

		@EntityProperty
		Property<Float> finalRadius;

		@EntityProperty
		Property<String> type;

		@EntityProperty
		Property<String> state;

		public String getState() {
			return state.get();
		}
	}

	class BulletWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<Vector2f> position;

		@EntityProperty
		Property<Entity> ball;

	}

	PathTraversal getPathTraversal(Entity entity, int index) {
		PathTraversal pathTraversal = Properties.getValue(entity, "pathTraversal");
		List<Entity> balls = Properties.getValue(entity, "balls");
		int currentIndex = balls.size() - 1;

		for (int i = balls.size() - 1; i >= 0; i--) {
			if (currentIndex == index)
				continue;
			Entity ball = balls.get(i);
			Float radius = Properties.getValue(ball, "radius");
			pathTraversal = pathTraversal.add(-radius * 2);
			currentIndex--;
		}

		return pathTraversal;
	}

	private static int segmentNumber = 1;
	
	@Override
	public String getId() {
		return MessageFormat.format("segment-{0}", SegmentEntityBuilder.segmentNumber);
	}
	
	@Override
	public void build() {
		
		SegmentEntityBuilder.segmentNumber++;

		tags("segment");

		property("pathTraversal", parameters.get("pathTraversal"));
		property("speed", parameters.get("speed"));
		property("balls", parameters.get("balls") != null ? parameters.get("balls") : new LinkedList());
		
		property("baseReached", false);

		property("firstBall", new FixedProperty(entity) {
			@Override
			public Object get() {
				List<Entity> balls = Properties.getValue(getHolder(), "balls");
				return balls.get(0);
			}
		});
		property("lastBall", new FixedProperty(entity) {
			@Override
			public Object get() {
				List<Entity> balls = Properties.getValue(getHolder(), "balls");
				return balls.get(balls.size() - 1);
			}
		});
		property("isEmpty", new FixedProperty(entity) {
			@Override
			public Object get() {
				List<Entity> balls = Properties.getValue(getHolder(), "balls");
				return balls.isEmpty();
			}
		});

		property("minSpeedFactor", parameters.get("minSpeedFactor") != null ? parameters.get("minSpeedFactor") : 0.2f);
		property("maxSpeed", parameters.get("maxSpeed") != null ? parameters.get("maxSpeed") : 0.04f);
		property("speedWhenReachBase", parameters.get("speedWhenReachBase") != null ? parameters.get("speedWhenReachBase") : 0.4f);

		property("acceleratedSpeed", parameters.get("acceleratedSpeed") != null ? parameters.get("acceleratedSpeed") : 0.08f);
		property("accelerated", parameters.get("accelerated") != null ? parameters.get("accelerated") : false);
		property("accelerationStopPoint", parameters.get("accelerationStopPoint"));

		property("pathLength", parameters.get("pathLength"));

		property("segmentTemplate", new InstantiationTemplateImpl(templateProvider.getTemplate("zombierockers.entities.segment"), new GenericProvider() {

			@SuppressWarnings( { "unchecked", "serial" })
			@Override
			public <T> T get(Object... objects) {
				final Map<String, Object> data = (Map<String, Object>) objects[0];
				return (T) new HashMap<String, Object>() {
					{
						put("pathTraversal", data.get("pathTraversal"));
						put("balls", data.get("balls"));
						put("speed", data.get("speed"));
						put("minSpeedFactor", data.get("minSpeedFactor"));
						put("maxSpeed", data.get("maxSpeed"));
						put("speedWhenReachBase", data.get("speedWhenReachBase"));
						put("pathLength", data.get("pathLength"));
					}
				};
			}

			@Override
			public <T> T get() {
				throw new RuntimeException("must never be called");
			}
		}));

		component(new ReferencePropertyComponent("segmentRemoveHead") {

			SegmentWrapper segment = new SegmentWrapper();

			@Inject
			MessageQueue messageQueue;

			@Inject ChildrenManagementMessageFactory childrenManagementMessageFactory;
			
			@Handles
			public void segmentRemoveHead(Message message) {

				Entity segmentEntity = Properties.getValue(message, "segment");
				if (segmentEntity != entity)
					return;

				segment.wrap(entity);

				if (segment.balls.get().size() < 2) {

					for (Entity ball : segment.balls.get()) {
						if (logger.isInfoEnabled())
							logger.info("Removed last ball - segment.id: " + entity.getId() + " - ball.id: " + ball.getId());
						messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(ball));
					}

					messageQueue.enqueue(new Message("destroySegment", new PropertiesMapBuilder() {
						{
							property("segment", entity);
						}
					}.build()));

					return;
				}

				Entity lastBallEntity = segment.lastBall.get();
				segment.pathTraversal.set(getPathTraversal(entity, segment.balls.get().size() - 2));
				if (logger.isInfoEnabled())
					logger.info("Removed last ball - segment.id: " + entity.getId() + " - ball.id: " + segment.lastBall.get().getId());
				segment.balls.get().remove(lastBallEntity);
				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(lastBallEntity));
			}

		});

		component(new ReferencePropertyComponent("addNewBallHandler") {

			SegmentWrapper segment = new SegmentWrapper();

			BallWrapper ball = new BallWrapper();

			@Inject
			MessageQueue messageQueue;
			
			@Inject ChildrenManagementMessageFactory childrenManagementMessageFactory;

			@Handles
			public void addNewBall(Message message) {

				Entity segmentEntity = Properties.getValue(message, "segment");
				if (segmentEntity != entity)
					return;

				segment.wrap(entity);

				Integer insertionPoint = (Integer) (Properties.getValue(message, "index") != null ? Properties.getValue(message, "index") : 0);

				if (insertionPoint > segment.balls.get().size())
					insertionPoint = segment.balls.get().size();

				Entity ballEntity = Properties.getValue(message, "ball");

				segment.balls.get().add(insertionPoint, ballEntity);

				ball.wrap(ballEntity);

				ball.pathTraversal.set(getPathTraversal(entity, insertionPoint));
				ball.newPathTraversal.set(ball.pathTraversal.get());

				if (insertionPoint == segment.balls.get().size() - 1)
					ball.pathTraversal.set(segment.pathTraversal.get());

				ball.segment.set(entity);

				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(ballEntity, entity.getParent()));

				if (logger.isInfoEnabled())
					logger.info("Added ball to segment - segment.id: " + entity.getId() + " -  ball: " + ballEntity.getId() + " - index: " + insertionPoint);
			}

		});

		component(new ReferencePropertyComponent("incrementRadiusBallQueued") {

			SegmentWrapper segment = new SegmentWrapper();

			BallWrapper ball = new BallWrapper();

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void update(Message message) {

				List<Entity> delayedCheckBallSeries = new ArrayList<Entity>();
				Integer delta = Properties.getValue(message, "delta");

				segment.wrap(entity);

				List<Entity> balls = segment.balls.get();

				for (Entity ballEntity : balls) {

					ball.wrap(ballEntity);

					if (ball.isGrownUp.get())
						continue;

					float ballGrowSpeed = 0.016f * 6f;
					float grow = ballGrowSpeed * delta;

					ball.radius.set(ball.radius.get() + grow);

					float diff = ball.radius.get() - ball.finalRadius.get();

					if (diff > 0) {
						grow -= diff;
						ball.radius.set(ball.finalRadius.get());
						delayedCheckBallSeries.add(ballEntity);
					}

					if (ballEntity != segment.firstBall)
						segment.pathTraversal.set(segment.pathTraversal.get().add(grow * 2));

				}

				for (final Entity ballEntity : delayedCheckBallSeries) {
					messageQueue.enqueue(new Message("checkBallSeries", new PropertiesMapBuilder() {
						{
							property("ball", ballEntity);
						}
					}.build()));
				}

			}

		});

		component(new ReferencePropertyComponent("advanceHandler") {

			SegmentWrapper segment = new SegmentWrapper();

			BallWrapper ball = new BallWrapper();

			@Handles
			public void update(Message message) {

				segment.wrap(entity);

				Float speed = segment.speed.get();

				Integer delta = Properties.getValue(message, "delta");

				if (segment.accelerated.get())
					speed = segment.acceleratedSpeed.get();
				else if (segment.baseReached.get())
					speed = segment.speedWhenReachBase.get();
				else if (speed > 0) {
					float minSpeedFactor = segment.minSpeedFactor.get();
					float maxSpeed = segment.maxSpeed.get();
					float minSpeed = maxSpeed * minSpeedFactor;

					float distanceFromOrigin = segment.pathTraversal.get().getDistanceFromOrigin();
					float pathLength = segment.pathLength.get();

					speed = minSpeed + maxSpeed * (1 - minSpeedFactor) * (1 - (distanceFromOrigin / pathLength));
				}

				float distance = speed * delta;
				PathTraversal pathTraversal = segment.pathTraversal.get().add(distance);
				segment.pathTraversal.set(pathTraversal);

				List<Entity> balls = segment.balls.get();
				for (int i = balls.size() - 1; i >= 0; i--) {
					Entity ballEntity = balls.get(i);
					ball.wrap(ballEntity);

					ball.newPathTraversal.set(pathTraversal);
					pathTraversal = pathTraversal.add(-ball.radius.get() * 2);
				}

			}

		});

		component(new ReferencePropertyComponent("checkEndAcceleration") {

			SegmentWrapper segment = new SegmentWrapper();

			@Handles
			public void update(Message message) {

				segment.wrap(entity);

				if (!segment.accelerated.get())
					return;

				if (segment.pathTraversal.get().getDistanceFromOrigin() > segment.accelerationStopPoint.get()) {
					if (logger.isInfoEnabled())
						logger.info("Segment stoped initial acceleration - segment.id: " + entity.getId());
					segment.accelerated.set(false);
				}

			}

		});

		component(new ReferencePropertyComponent("bulletHitHandler") {

			SegmentWrapper segment = new SegmentWrapper();

			BallWrapper ball = new BallWrapper();

			BallWrapper collisionBall = new BallWrapper();

			BulletWrapper bullet = new BulletWrapper();

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void bulletHit(Message message) {
				List<Entity> targets = Properties.getValue(message, "targets");

				Entity collisionBallEntity = targets.get(0);

				collisionBall.wrap(collisionBallEntity);

				if (collisionBall.segment.get() != entity)
					return;

				segment.wrap(entity);

				int ballIndex = segment.balls.get().indexOf(collisionBallEntity);

				if (ballIndex == -1) {
					String textMessage = "Collision ball had wrong segment setted -  ball.id:" + collisionBallEntity.getId() + " - ball.segment.id: " + entity.getId();
					if (logger.isErrorEnabled())
						logger.error(textMessage);
					throw new RuntimeException(textMessage);
				}

				Entity bulletEntity = Properties.getValue(message, "source");
				bullet.wrap(bulletEntity);

				if (logger.isInfoEnabled())
					logger.info("Bullet collided with segment: segment.id: " + entity.getId() + "- ball.id: " + collisionBallEntity.getId() // 
							+ " - ballIndex: " + ballIndex + "- newBall.id: " + bullet.ball.get().getId());

				Vector2f tangent = getPathTraversal(entity, ballIndex).getTangent();

				Vector2f collisionBallPosition = collisionBall.position.get();
				Vector2f bulletPosition = bullet.position.get();

				Vector2f differenceVector = bulletPosition.copy().sub(collisionBallPosition);

				float proyection = tangent.dot(differenceVector);

				if (proyection > 0)
					ballIndex++;

				final int newBallIndex = ballIndex;

				messageQueue.enqueue(new Message("addNewBall", new PropertiesMapBuilder() {
					{
						property("segment", entity);
						property("ball", bullet.ball.get());
						property("index", newBallIndex);
					}
				}.build()));

				Integer ballsQuantity = Properties.getValue(entity.getParent(), "ballsQuantity");
				Properties.setValue(entity.getParent(), "ballsQuantity", ballsQuantity + 1);

			}

		});

		component(new ReferencePropertyComponent("checkBallSeriesHandler") {

			SegmentWrapper segment = new SegmentWrapper();

			BallWrapper ball = new BallWrapper();

			BallWrapper newBall = new BallWrapper();

			BallWrapper ballToCheck = new BallWrapper();

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void checkBallSeries(Message message) {

				final Entity ballFromMessage = Properties.getValue(message, "ball");
				
				boolean seriesDetected = internalCheckBallSeries(message);
				
				if (!seriesDetected) {
					messageQueue.enqueue(new Message("seriesNotDetected", new PropertiesMapBuilder() {
						{
							property("ball", ballFromMessage);
						}
					}.build()));
				}

			}

			private boolean internalCheckBallSeries(Message message) {
				Entity ballFromMessage = Properties.getValue(message, "ball");
				ball.wrap(ballFromMessage);

				if (!ball.alive.get())
					return false;

				if (ball.segment.get() != entity)
					return false;

				segment.wrap(entity);

				int index = segment.getBalls().indexOf(ballFromMessage);

				if (logger.isInfoEnabled())
					logger.info("Checking ball series - segment.id: $entity.id - ball.id: " + ballFromMessage.getId() + " - ballIndex: " + index);

				if (index == -1) {
					String textMessage = "CheckBallSeries -  ball had wrong segment setted -  ball.id: " + ballFromMessage.getId() + " - ball.segment.id: " + entity.getId();
					if (logger.isErrorEnabled())
						logger.error(textMessage);
					throw new RuntimeException(textMessage);
				}

				ListIterator<Entity> forwardIterator = segment.getBalls().listIterator(index);
				Entity newBallEntity = forwardIterator.next();
				final List<Entity> ballsToRemove = new ArrayList<Entity>();
				ballsToRemove.add(newBallEntity);

				newBall.wrap(newBallEntity);

				while (forwardIterator.hasNext()) {
					Entity ballToCheckEntity = forwardIterator.next();
					ballToCheck.wrap(ballToCheckEntity);

					if (!ballToCheck.type.get().equals(newBall.type.get()))
						break;

					ballsToRemove.add(ballToCheckEntity);
				}

				ListIterator<Entity> backwardsIterator = segment.getBalls().listIterator(index);
				while (backwardsIterator.hasPrevious()) {
					Entity ballToCheckEntity = backwardsIterator.previous();
					ballToCheck.wrap(ballToCheckEntity);

					if ("spawned".equals(ballToCheck.getState()) || !ballToCheck.type.get().equals(newBall.type.get()))
						break;

					ballsToRemove.add(0, ballToCheckEntity);
				}

				if (ballsToRemove.size() < 3) {
					messageQueue.enqueue(new Message("checkSameColorSegments"));
					if (logger.isInfoEnabled())
						logger.info("When ball added to segment less than 3 balls  in series- segment.id: " + entity.getId());
					return false;
				}

				Entity mustContainBallEntity = Properties.getValue(message, "mustContainBall");
				if (mustContainBallEntity != null) {
					if (!ballsToRemove.contains(mustContainBallEntity)) {
						if (logger.isInfoEnabled())
							logger.info("When ball added to segment more than 3 balls but does not contains - segment.id: " + entity.getId());
						return false;
					}
				}

				if (logger.isInfoEnabled())
					logger.info("When ball added to segment 3 or more in series- segment.id: " + entity.getId());

				messageQueue.enqueue(new Message("seriesDetected", new PropertiesMapBuilder() {
					{
						property("segment", entity);
						property("ballsToRemove", ballsToRemove);
					}
				}.build()));
				
				return true;
			}

		});

		component(new ReferencePropertyComponent("ChangeSpeedHandler") {

			SegmentWrapper segment = new SegmentWrapper();

			@Handles
			public void segmentChangeSpeed(Message message) {
				Entity segmentEntity = Properties.getValue(message, "segment");
				if (segmentEntity != entity)
					return;
				segment.wrap(entity);
				Float newSpeed = Properties.getValue(message, "speed");
				if (logger.isInfoEnabled())
					logger.info("Changing speed - " + entity.getId() + " - oldSpeed: " + segment.getSpeed() + "- newSpeed: " + newSpeed);
				segment.setSpeed(newSpeed);
			}

		});

		component(new ReferencePropertyComponent("splitSegmentHandler") {

			final SegmentWrapper segment = new SegmentWrapper();

			@Inject
			MessageQueue messageQueue;
			
			@Inject ChildrenManagementMessageFactory childrenManagementMessageFactory;

			@Handles
			public void seriesDetected(Message message) {

				final boolean performed = internalSeriesDetected(message);
				
				messageQueue.enqueue(new Message("seriesDetectedPerformed", new PropertiesMapBuilder() {
					{
						property("performed", performed);
					}
				}.build()));
				
			}

			private boolean internalSeriesDetected(Message message) {
				segment.wrap(entity);

				final List<Entity> balls = segment.getBalls();
				final List<Entity> ballsToRemove = Properties.getValue(message, "ballsToRemove");

				final List<Entity> ballsInside = new ArrayList<Entity>(Collections2.filter(ballsToRemove, new Predicate<Entity>() {
					@Override
					public boolean apply(Entity ballEntity) {
						return balls.contains(ballEntity);
					}
				}));

				if (ballsInside.isEmpty())
					return false;

				if (ballsInside.size() != ballsToRemove.size()) {

					// add log....

					messageQueue.enqueue(new Message("checkBallSeries", new PropertiesMapBuilder() {
						{
							property("ball", ballsInside.get(0));
						}
					}.build()));

					return false;
				}
				
				int firstIndex = balls.indexOf(ballsToRemove.get(0));
				int lastIndex = balls.indexOf(ballsToRemove.get(ballsToRemove.size() - 1));

				final PathTraversal originalPathTraversal = segment.pathTraversal.get();

				LinkedList<Entity> firstSegmentBalls = new LinkedList<Entity>(balls.subList(0, firstIndex));
				final LinkedList<Entity> secondSegmentBalls = new LinkedList<Entity>(balls.subList(lastIndex + 1, balls.size()));
				
				if (logger.isInfoEnabled()) {
					logger.info("Splitting segment when removeBalls - segment.id: " + entity.getId());
					logger.info("First subsegment balls - " + firstSegmentBalls.size());
					logger.info("Second subsegment balls - " + secondSegmentBalls.size());
				}
				
				LinkedList<Entity> betweenSegment = new LinkedList<Entity>(balls.subList(firstIndex, lastIndex+1));
				
				if (betweenSegment.size() != ballsToRemove.size()) {
					if (logger.isInfoEnabled())
						logger.info("Splitting canceled because concurrent merge and ball insertion - " + entity.getId());
					return false;
				}
				
				// chain detected correctly
				
				if (firstSegmentBalls.isEmpty() && secondSegmentBalls.isEmpty()) {
					if (logger.isInfoEnabled())
						logger.info("Both subsegments are empty, removing balls - segment.id: " + entity.getId());
					segment.getBalls().clear();
					messageQueue.enqueue(new Message("destroySegment", new PropertiesMapBuilder() {
						{
							property("segment", entity);
						}
					}.build()));
				} else if (firstSegmentBalls.isEmpty()) {
					if (logger.isInfoEnabled())
						logger.info("First segment is empty - segment.id: " + entity.getId());
					segment.balls.set(secondSegmentBalls);
				} else {
					segment.pathTraversal.set(getPathTraversal(entity, firstIndex-1));
					segment.balls.set(firstSegmentBalls);
					
					if (!secondSegmentBalls.isEmpty()) {
						
						Entity newSegmentEntity = segment.segmentTemplate.get().get(new HashMap<String, Object>() {{
							put("pathTraversal", originalPathTraversal);
							put("balls", secondSegmentBalls);
							put("speed", 0.0f);
							put("pathLength", segment.pathLength.get());
							put("minSpeedFactor", segment.minSpeedFactor.get());
							put("maxSpeed", segment.maxSpeed.get());
							put("speedWhenReachBase", segment.speedWhenReachBase.get());
						}});
						
						messageQueue.enqueue(childrenManagementMessageFactory.addEntity(newSegmentEntity, entity.getParent()));
						for (Entity ballEntity : secondSegmentBalls) 
							Properties.setValue(ballEntity, "segment", newSegmentEntity);
						
						// logger....
						
					} else {
						if (logger.isInfoEnabled())
							logger.info("Second subsegment is empty - segment.id: " + entity.getId());
					}
				}
			
				messageQueue.enqueue(new Message("explodeBall", new PropertiesMapBuilder() {
					{
						property("balls", ballsToRemove);
					}
				}.build()));
				
				messageQueue.enqueue(new Message("checkSameColorSegments"));
				
				return true;
			}

		});
		
		component(new ReferencePropertyComponent("mergeSegmentsHandler") {

			final SegmentWrapper masterSegment = new SegmentWrapper();

			final SegmentWrapper slaveSegment = new SegmentWrapper();
			
			final BallWrapper ball = new BallWrapper();

			@Inject
			MessageQueue messageQueue;

			@Handles
			public void mergeSegments(Message message) {
				Entity masterSegmentEntity = Properties.getValue(message, "masterSegment");
				if (masterSegmentEntity != entity)
					return;
				
				//// logger....
				masterSegment.wrap(entity);
				
				final Entity slaveSegmentEntity = Properties.getValue(message, "slaveSegment");
				slaveSegment.wrap(slaveSegmentEntity);
				
				masterSegment.pathTraversal.set(slaveSegment.pathTraversal.get());
				
				final Entity ballToCheck = slaveSegment.firstBall.get();
				final Entity mustContainBall = masterSegment.lastBall.get();
				
				masterSegment.getBalls().addAll(slaveSegment.getBalls());
				
				for (Entity slaveSegmentBall : slaveSegment.getBalls()) {
					ball.wrap(slaveSegmentBall);
					ball.segment.set(masterSegmentEntity);
				}
				slaveSegment.getBalls().clear();
				
				messageQueue.enqueue(new Message("destroySegment", new PropertiesMapBuilder() {
					{
						property("segment", slaveSegmentEntity);
					}
				}.build()));
				
				messageQueue.enqueue(new Message("checkBallSeries", new PropertiesMapBuilder() {
					{
						property("ball", ballToCheck);
						property("mustContainBall", mustContainBall);
					}
				}.build()));
				
			}

		});
		
		component(new ReferencePropertyComponent("baseReachedHandler") {
			
			SegmentWrapper segment = new SegmentWrapper();

			@Handles
			public void baseReached(Message message) {
				if (logger.isInfoEnabled()) 
					logger.info("Base reached - Accelerating - segment.id: " + entity.getId());
				segment.wrap(entity);
				segment.baseReached.set(true);
			}

		});

	}
}
