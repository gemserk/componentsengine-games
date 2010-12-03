package com.gemserk.games.zombierockers.entities;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.slick.effects.EffectFactory;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.zombierockers.PathTraversal;
import com.gemserk.games.zombierockers.SubPathDefinition;
import com.gemserk.games.zombierockers.ScenesDefinitions.SubPathDefinitions;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;

public class BallEntityBuilder extends EntityBuilder {

	@Inject
	SlickUtils slick;

	private static int ballNumber = 1;

	@Inject
	ResourceManager resourceManager;

	@Override
	public String getId() {
		return MessageFormat.format("ball-{0}", BallEntityBuilder.ballNumber);
	}

	@Override
	public void build() {

		BallEntityBuilder.ballNumber++;

		tags("ball");

		Map definition = (Map) parameters.get("definition");

		property("type", definition.get("type"));
		property("color", definition.get("color"));
		
		property("image", definition.get("image"));
//		property("image", resourceManager.get((String) definition.get("image")));

		// property("animationResource", animationResource);
		// property("animation", new FixedProperty(entity){
		// @Override
		// public Object get() {
		// Resource resource = Properties.getValue(getHolder(), "animationResource");
		// return resource.get();
		// }
		// });

		property("radius", parameters.get("radius"));
		property("finalRadius", parameters.get("finalRadius") != null ? parameters.get("finalRadius") : parameters.get("radius"));
		property("state", parameters.get("state"));

		property("fired", parameters.get("fired"));
		property("isGrownUp", new FixedProperty(entity) {
			@Override
			public Object get() {
				Float radius = Properties.getValue(getHolder(), "radius");
				Float finalRadius = Properties.getValue(getHolder(), "finalRadius");
				return radius.equals(finalRadius);
			}
		});

		property("pathTraversal", null);
		property("newPathTraversal", null);

		property("position", new FixedProperty(entity) {
			@Override
			public Object get() {
				PathTraversal pathTraversal = Properties.getValue(getHolder(), "pathTraversal");
				if (pathTraversal != null)
					return pathTraversal.getPosition();
				return new Vector2f();
			}
		});

		// Animation animation = animationResource.get();
		// Float finalRadius = Properties.getValue(entity, "finalRadius");
		// float frameSize = (float) (2 * Math.PI * finalRadius / animation.getFrameCount());
		// property("animationHelper", new AnimationHelper(animationResource, frameSize));

		property("angle", 0f);
		property("direction", new FixedProperty(entity) {
			@Override
			public Object get() {
				Float angle = Properties.getValue(getHolder(), "angle");
				PathTraversal pathTraversal = Properties.getValue(getHolder(), "pathTraversal");
				if (pathTraversal != null)
					return pathTraversal.getTangent().add(angle);
				return new Vector2f();
			}
		});

		property("alive", true);
		property("segment", null);

		property("size", new FixedProperty(entity) {

			final Vector2f size = new Vector2f();

			@Override
			public Object get() {
				Float radius = Properties.getValue(getHolder(), "radius");
				Float finalRadius = Properties.getValue(getHolder(), "finalRadius");
				Float s = radius / finalRadius;
				size.set(s, s);
				return size;
			}
		});

		property("subPathDefinitions", parameters.get("subPathDefinitions"));

		property("layer", 0);
		property("collisionMask", 0);

		property("currentFrame", new FixedProperty(entity) {
			@Override
			public Object get() {
				
				// Resource<Image> imageResource = Properties.getValue(getHolder(), "image");
				String image = Properties.getValue(getHolder(), "image");
				return resourceManager.get(image).get(); 
				// return imageResource.get();

				// Animation animation = Properties.getValue(getHolder(), "animation");
				// return animation.getCurrentFrame();
			}
		});

		component(new FieldsReflectionComponent("updatePositionHandler") {

			@EntityProperty
			PathTraversal pathTraversal;

			@EntityProperty(readOnly = true)
			PathTraversal newPathTraversal;

			// @EntityProperty(readOnly = true)
			// AnimationHelper animationHelper;
			
			@EntityProperty
			Float angle;

			@EntityProperty
			Integer layer;

			@EntityProperty
			Integer collisionMask;

			@Handles
			public void update(Message message) {

				float distance = newPathTraversal.getDistanceFromOrigin() - pathTraversal.getDistanceFromOrigin();

				// animationHelper.add(distance);
				angle += (distance * 3);

				pathTraversal = newPathTraversal;

				SubPathDefinitions subPathDefinitions = Properties.getValue(entity, "subPathDefinitions");

				// Map subPathDefinitions = Properties.getValue(entity, "subPathDefinitions");
				// Closure method = (Closure) subPathDefinitions.get("getSubPathDefinition");
				// SubPathDefinition subPathDefinition = (SubPathDefinition) method.call(newPathTraversal);

				SubPathDefinition subPathDefinition = subPathDefinitions.getSubPathDefinition(newPathTraversal);

				layer = (Integer) subPathDefinition.getMetadata().get("layer");
				collisionMask = (Integer) subPathDefinition.getMetadata().get("collisionMask");
			}

		});

		component(new FieldsReflectionComponent("explosionsWhenRemoveBallsHandler") {

			@Inject
			MessageQueue messageQueue;

			@EntityProperty
			Boolean alive;

			@EntityProperty(readOnly = true)
			Color color;

			@EntityProperty(readOnly = true)
			Vector2f position;

			@EntityProperty(readOnly = true)
			Integer layer;
			
			@Inject ChildrenManagementMessageFactory childrenManagementMessageFactory;

			@Handles
			public void explodeBall(Message message) {

				List<Entity> balls = Properties.getValue(message, "balls");
				if (!balls.contains(entity))
					return;

				alive = false;

				messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(entity));

				messageQueue.enqueue(new Message("explosion", new PropertiesMapBuilder() {
					{
						property("explosion", EffectFactory.explosionEffect(100, (int) position.x, (int) position.y, 0f, 360f, 800, 10.0f, 50f, 320f, 3f, color, color));
						property("layer", layer + 1);
					}
				}.build()));
			}

		});
	}
}
