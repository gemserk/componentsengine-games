package com.gemserk.games.towerofdefense.components;

import static com.gemserk.componentsengine.properties.Properties.property;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;

public class MovementComponent extends Component {

	PropertyLocator<Float> frictionProperty = property("movement.friction");

	PropertyLocator<Float> constSpeedProperty = property("movement.constSpeed");

	PropertyLocator<Float> turnRateProperty = property("movement.turnRate");

	PropertyLocator<Float> speedProperty = property("movement.speed");

	PropertyLocator<Vector2f> velocityProperty = property("velocity");

	PropertyLocator<Vector2f> positionProperty = property("position");

	PropertyLocator<Vector2f> directionProperty = property("movement.direction");

	PropertyLocator<Double> rotationProperty = property("movement.rotation");

	public MovementComponent(String name) {
		super(name);
	}

	@Override
	public void onAdd(Entity entity) {
		super.onAdd(entity);
		double initialRotation = -Math.PI / 2;
		rotationProperty.setValue(entity, initialRotation);
		directionProperty.setValue(entity, new Vector2f());
		velocityProperty.setValue(entity, new Vector2f());
	}

	protected Vector2f getRotationDirection(double rotation) {

		return new Vector2f((float) Math.cos(rotation), (float) Math
				.sin(rotation));
	}

	@Override
	public void update(Entity entity, int delta) {
		Vector2f currentVelocity = velocityProperty.getValue(entity);
		Vector2f currentPosition = positionProperty.getValue(entity);

		Vector2f nextVelocity = currentVelocity.copy();
		Vector2f nextPosition = currentPosition.copy();

		calculateNextPosition(nextPosition, nextVelocity, entity, delta * 0.1f);

		currentPosition.set(nextPosition);
		currentVelocity.set(nextVelocity);
	}

	protected void calculateNextPosition(Vector2f position, Vector2f velocity,
			Entity entity, float delta) {

		Vector2f direction = this.directionProperty.getValue(entity);
		Double rotation = this.rotationProperty.getValue(entity);

		float friction = this.frictionProperty.getValue(entity);
		float constSpeed = this.constSpeedProperty.getValue(entity);
		float turnRate = this.turnRateProperty.getValue(entity);
		float speed = this.speedProperty.getValue(entity);

		rotation = turnToFace(rotation, direction, turnRate, delta);

		Vector2f forward = getRotationDirection(rotation);
		Vector2f targetForward = new Vector2f(direction.x, -direction.y);

		float facingForward = forward.dot(targetForward);

		if (facingForward > 0)
			velocity.add(forward.scale(facingForward * facingForward * speed * delta));

		forward.normalise();

		Vector2f vel = velocity.copy();

		Vector2f result = position.copy().add(vel).add(
				getRotationDirection(rotation).scale(constSpeed));

		position.set(result);

		velocity.scale(friction);

		this.rotationProperty.setValue(entity, rotation);
	}

	protected double turnToFace(double rotation, Vector2f target, float turnRate, float delta) {
		if (target.length() == 0)
			return rotation;

		double angle = Math.atan2(target.y, target.x);

		double difference = rotation - angle;

		while (difference > Math.PI)
			difference -= Math.PI * 2;

		while (difference < -Math.PI)
			difference += Math.PI * 2;

		turnRate *= Math.abs(difference);

		if (difference < 0)
			return rotation + Math.min(turnRate, -difference) * delta;
		else
			return rotation - Math.min(turnRate, difference) * delta;
	}

}