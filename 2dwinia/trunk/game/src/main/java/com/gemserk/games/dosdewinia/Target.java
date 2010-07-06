package com.gemserk.games.dosdewinia;

import org.newdawn.slick.geom.Vector2f;

public class Target {

	Vector2f position;
	float wanderRadius;
	float arrivalRadius;
	
	public Target(Vector2f position, float wanderRadius, float arrivalRadius) {
		this.position = position;
		this.wanderRadius = wanderRadius;
		this.arrivalRadius = arrivalRadius;
	}

	public Vector2f getPosition() {
		return position;
	}

	public float getWanderRadius() {
		return wanderRadius;
	}

	public float getArrivalRadius() {
		return arrivalRadius;
	}
	
	
	
	
}
