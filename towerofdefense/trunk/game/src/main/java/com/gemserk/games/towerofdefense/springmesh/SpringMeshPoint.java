package com.gemserk.games.towerofdefense.springmesh;

import org.newdawn.slick.geom.Vector2f;

public class SpringMeshPoint {

	Vector2f restPosition;

	Vector2f position = new Vector2f(0.0f, 0.0f);

	Vector2f velocity = new Vector2f(0.0f, 0.0f);

	Vector2f force = new Vector2f(0.0f, 0.0f);

	float mass = 1.0f;

	public SpringMeshPoint(Vector2f restPosition) {
		this.restPosition = restPosition;
	}

	private Vector2f getDampingForce() {
		Vector2f damping = new Vector2f(2.0f, 2.0f);
		return new Vector2f(damping.x * velocity.x, damping.y * velocity.y).negate();
	}

	private Vector2f getSpringForce() {
		float stiffnessx = 10.0f;
		float stiffnessy = 10.0f;
		return new Vector2f(stiffnessx * position.x, stiffnessy * position.y).negate();
	}

	public Vector2f getPosition() {
		return position.copy().add(restPosition);
	}

	public void update(int deltaInt) {

		float delta = ((float) deltaInt) / 1000f;

		force.add(getSpringForce().add(getDampingForce()));

		updatePhysics(delta, force, position, velocity, mass);

		force.set(0.0f, 0.0f);
	}

	private void updatePhysics(float delta, Vector2f force, Vector2f position, Vector2f velocity, float mass) {
		Vector2f acceleration = force.copy().scale(1.0f / mass);

		Vector2f newVelocity = velocity.copy().add(acceleration.copy().scale(delta));
		Vector2f newPosition = position.copy().add(newVelocity.copy().scale(delta));

		velocity.set(newVelocity);
		position.set(newPosition);

		if (acceleration.length() < 0.1f && velocity.length() < 0.1f) {
			velocity.set(0.0f, 0.0f);
			position.set(0.0f, 0.0f);
		}
	}

}