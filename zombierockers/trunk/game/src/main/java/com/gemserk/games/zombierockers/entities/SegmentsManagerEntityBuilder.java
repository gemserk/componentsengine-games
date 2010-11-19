package com.gemserk.games.zombierockers.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.PropertiesWrapper;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.slick.utils.SlickSvgUtils;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.PathTraversal;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class SegmentsManagerEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(SegmentsManagerEntityBuilder.class);

	@Inject
	MessageQueue messageQueue;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	SlickSvgUtils slickSvgUtils;

	@Inject
	SlickUtils slick;

	List<Entity> getSortedSegments(Entity entity) {

		Collection<Entity> segments = entity.getRoot().getEntities(Predicates.and(EntityPredicates.withAllTags("segment"), new Predicate<Entity>() {
			@Override
			public boolean apply(Entity segmentEntity) {
				Boolean isEmpty = Properties.getValue(segmentEntity, "isEmpty");
				return !isEmpty.booleanValue();
			}
		}));

		ArrayList<Entity> sortedSegments = new ArrayList<Entity>(segments);
		Collections.sort(sortedSegments, new Comparator<Entity>() {
			@Override
			public int compare(Entity segment1, Entity segment2) {
				PathTraversal pathTraversal1 = Properties.getValue(segment1, "pathTraversal");
				PathTraversal pathTraversal2 = Properties.getValue(segment2, "pathTraversal");
				return pathTraversal1.compareTo(pathTraversal2);
			}
		});

		return sortedSegments;
	}

	class SegmentsManagerWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<Boolean> baseReached;

	}

	class SegmentWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<Entity> lastBall;

		@EntityProperty
		Property<Entity> firstBall;

		@EntityProperty
		Property<Float> speed;

		public Float getSpeed() {
			return speed.get();
		}

	}

	class BallWrapper extends PropertiesWrapper {

		@EntityProperty
		Property<Color> color;
		
		@EntityProperty
		Property<Vector2f> position;

		@EntityProperty
		Property<Float> radius;
	}

	@Override
	public void build() {

		tags("segmentsManager");

		property("baseReached", parameters.get("baseReached"));

		component(new ReferencePropertyComponent("checkSameColorSegmentsHandler") {

			SegmentWrapper segment = new SegmentWrapper();
			SegmentWrapper nextSegment = new SegmentWrapper();

			BallWrapper lastBall = new BallWrapper();
			BallWrapper firstBall = new BallWrapper();

			@Handles
			public void checkSameColorSegments(Message message) {

				final float reverseSpeed = -0.3f;

				List<Entity> sortedSegments = getSortedSegments(entity);

				if (logger.isInfoEnabled())
					logger.info("Segments not empty: " + sortedSegments.size());

				for (int i = 0; i < sortedSegments.size() - 1; i++) {

					final Entity segmentEntity = sortedSegments.get(i);
					final Entity nextSegmentEntity = sortedSegments.get(i + 1);

					segment.wrap(segmentEntity);
					nextSegment.wrap(nextSegmentEntity);
					lastBall.wrap(segment.lastBall.get());
					firstBall.wrap(nextSegment.firstBall.get());

					if (lastBall.color.get().equals(firstBall.color.get())) {
						if (logger.isInfoEnabled())
							logger.info("SegmentManager detected color coincidence between segments ends : " + segmentEntity.getId() + " and " + nextSegmentEntity.getId());
						messageQueue.enqueue(new Message("segmentChangeSpeed", new PropertiesMapBuilder() {
							{
								property("segment", nextSegmentEntity);
								property("speed", reverseSpeed);
							}
						}.build()));
					}
				}

			}

		});

		component(new ReferencePropertyComponent("checkFirstSegmentShouldAdvanceHandler") {

			SegmentsManagerWrapper segmentsManager = new SegmentsManagerWrapper();

			SegmentWrapper firstSegment = new SegmentWrapper();

			@Handles
			public void checkFirstSegmentSholdAdvance(Message message) {

				segmentsManager.wrap(entity);

				if (segmentsManager.baseReached.get())
					return;

				List<Entity> sortedSegments = getSortedSegments(entity);

				if (logger.isInfoEnabled())
					logger.info("Checking first segment should advance - cantSegments: " + sortedSegments.size());

				if (sortedSegments.isEmpty())
					return;

				final float speed = 0.04f;

				final Entity firstSegmentEntity = sortedSegments.get(0);
				firstSegment.wrap(firstSegmentEntity);

				if (firstSegment.getSpeed() <= 0) {
					messageQueue.enqueue(new Message("segmentChangeSpeed", new PropertiesMapBuilder() {
						{
							property("segment", firstSegmentEntity);
							property("speed", speed);
						}
					}.build()));
				}
			}

		});

		component(new ReferencePropertyComponent("destroySegmentHandler") {

			@Handles
			public void destroySegment(Message message) {
				Entity segment = Properties.getValue(message, "segment");
				messageQueue.enqueue(ChildrenManagementMessageFactory.removeEntity(segment));
				messageQueue.enqueue(new Message("checkFirstSegmentSholdAdvance"));
			}

		});
		
		component(new ReferencePropertyComponent("collisionBetweenSegmentsDetector") {

			SegmentWrapper segment = new SegmentWrapper();
			SegmentWrapper nextSegment = new SegmentWrapper();

			BallWrapper lastBall = new BallWrapper();
			BallWrapper firstBall = new BallWrapper();

			@Handles
			public void update(Message message) {
				
				List<Entity> sortedSegments = getSortedSegments(entity);
				
				boolean collisionFound = false;
				
				for (int i = 0; i < sortedSegments.size() - 1; i++) {
					
					if (collisionFound)
						break;

					final Entity segmentEntity = sortedSegments.get(i);
					final Entity nextSegmentEntity = sortedSegments.get(i + 1);

					segment.wrap(segmentEntity);
					nextSegment.wrap(nextSegmentEntity);
					lastBall.wrap(segment.lastBall.get());
					firstBall.wrap(nextSegment.firstBall.get());
					
					if (lastBall.position.get().distance(firstBall.position.get()) < lastBall.radius.get() * 2) {
						
						if (logger.isInfoEnabled())
							logger.info("Collision detected with other segment - masterSegment.id: " + segmentEntity.getId() + " - slaveSegment.id: " + nextSegmentEntity.getId());
						
						
						messageQueue.enqueue(new Message("mergeSegments", new PropertiesMapBuilder() {
							{
								property("masterSegment", segmentEntity);
								property("slaveSegment", nextSegmentEntity);
							}
						}.build()));
						
						collisionFound = true;
					}

				}
				
			}

		});

	}
}
