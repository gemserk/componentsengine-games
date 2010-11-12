package com.gemserk.games.zombierockers.entities;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.GenericHitComponent;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.triggers.Trigger;
import com.gemserk.games.zombierockers.PathTraversal;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

public class BaseEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(BaseEntityBuilder.class);

	private static int baseNumber = 1;

	@Override
	public String getId() {
		return MessageFormat.format("base-{0}", BaseEntityBuilder.baseNumber);
	}
	
	@Inject MessageQueue messageQueue;

	@Override
	public void build() {

		BaseEntityBuilder.baseNumber++;

		tags("base");

		property("position", parameters.get("position"));
		property("radius", parameters.get("radius"));
		property("baseReached", false);
		
		component(new CircleRenderableComponent("")).withProperties(new ComponentProperties() {
			{
				propertyRef("position", "position");
				propertyRef("radius", "radius");
				property("lineColor", new Color(0f,0f,0f,1f));
				property("fillColor", new Color(0f,0f,0f,1f));
				property("layer",1);
			}
		});
		
		component(new GenericHitComponent("segmentHit")).withProperties(new ComponentProperties() {
			{
				property("targetTag", "segment");
				property("predicate", new FixedProperty(entity){
					public Object get() {
						return new Predicate<Entity>() {
							@Override
							public boolean apply(Entity segment) {
								PathTraversal pathTraversal = Properties.getValue(segment, "pathTraversal");
								Vector2f position = Properties.getValue(getHolder(), "position");
								Float radius = Properties.getValue(getHolder(), "radius");
								return pathTraversal.getPosition().distance(position) < radius;
							}
						};
					};
				});
				property("trigger", new Trigger() {
					
					@Override
					public void trigger(Object... parameters) {
						messageQueue.enqueue(new Message("segmentReachedBase", new PropertiesMapBuilder().addProperties((Map)parameters[0]).build()));
					}
					
					@Override
					public void trigger() {
						
					}
				});
			}
		});
		
		component(new FieldsReflectionComponent("segmentReachedBaseHandler") {
			
			@Inject
			MessageQueue messageQueue;
			
			@EntityProperty
			Boolean baseReached;
			
			@Handles
			public void segmentReachedBase(Message message) {
				
				Entity source = Properties.getValue(message, "source");
				if (source != entity)
					return;
				
				if (baseReached.equals(false)) {
					if (logger.isInfoEnabled())
						logger.info("Base reached - base.id: " + entity.getId());
					messageQueue.enqueue(new Message("baseReached"));
					baseReached = true;
				}
				
				final List<Entity> targets = Properties.getValue(message, "targets");
				
				messageQueue.enqueue(new Message("segmentRemoveHead", new PropertiesMapBuilder(){{
					property("segment", targets.get(0));
				}}.build()));
			}
			
		});
		
		
	}
}
