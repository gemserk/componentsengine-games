package com.gemserk.games.dosdewinia;

import org.newdawn.slick.geom.Vector2f;

public class Target {

	Vector2f position;
	float wanderRadius;
	float arrivalRadius;
	int zoneId;
	
	public Target(Vector2f position, float wanderRadius, float arrivalRadius, int zoneId) {
		this.position = position;
		this.wanderRadius = wanderRadius;
		this.arrivalRadius = arrivalRadius;
		this.zoneId = zoneId;
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
	
	public int getZoneId() {
		return zoneId;
	}
	
	
	
	
}
