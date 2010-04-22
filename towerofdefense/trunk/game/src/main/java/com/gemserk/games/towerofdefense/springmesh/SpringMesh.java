package com.gemserk.games.towerofdefense.springmesh;

import java.util.ArrayList;
import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

public class SpringMesh {

	Collection<SpringMeshPoint> springMeshPoints;

	Collection<Force> forcesToApply = new ArrayList<Force>();

	private class Force {

		Vector2f position;

		float power;

		public Force(Vector2f position, float power) {
			this.position = position;
			this.power = power;
		}

	}

	public Collection<SpringMeshPoint> getSpringMeshPoints() {
		return springMeshPoints;
	}

	public SpringMesh(Collection<SpringMeshPoint> springMeshPoints) {
		this.springMeshPoints = springMeshPoints;
	}

	public void applyForce(Vector2f forcePosition, float power) {
		forcesToApply.add(new Force(forcePosition, power));
	}

	public void update(int delta) {
		
		for (SpringMeshPoint springPoint : springMeshPoints) {
			
			for (Force force : forcesToApply) {
				Vector2f position = springPoint.getPosition();
				float length = position.copy().sub(force.position).length();
				Vector2f forceToApply = position.copy().sub(force.position).scale(1f / length).scale(force.power);
				springPoint.force.add(forceToApply);
			}
			
		}
		
		forcesToApply.clear();
	}

}