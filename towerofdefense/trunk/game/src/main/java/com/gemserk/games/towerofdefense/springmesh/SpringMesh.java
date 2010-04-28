package com.gemserk.games.towerofdefense.springmesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.newdawn.slick.geom.Vector2f;

public class SpringMesh {

	Collection<SpringMeshPoint> springMeshPoints;

	Collection<Force> forcesToApply = new ArrayList<Force>();

	private class Force {
		
		int time;

		Vector2f position;

		float power;

		float range;

		public Force(int time, Vector2f position, float range, float power) {
			this.time = time;
			this.position = position;
			this.range = range;
			this.power = power;
		}

	}

	public Collection<SpringMeshPoint> getSpringMeshPoints() {
		return springMeshPoints;
	}

	public SpringMesh(Collection<SpringMeshPoint> springMeshPoints) {
		this.springMeshPoints = springMeshPoints;
	}

	public void applyForce(int time, Vector2f forcePosition, float range, float power) {
		forcesToApply.add(new Force(time, forcePosition, range, power));
	}

	public void update(int delta) {
		
		for (SpringMeshPoint springPoint : springMeshPoints) {
			
			for (Force force : forcesToApply) {
				
				Vector2f position = springPoint.getPosition();

				if (force.position.distance(position) > force.range)
					continue;
				
				float length = position.copy().sub(force.position).length();
				Vector2f forceToApply = position.copy().sub(force.position).scale(1f / length).scale(force.power);
				springPoint.force.add(forceToApply);
			}
			
		}
		
		Iterator<Force> iterator = forcesToApply.iterator();
		while (iterator.hasNext()) {
			Force force = iterator.next();
			
			force.range += 5.0f;
			force.power -= 2.0f;
			if (force.power < 0.0f)
				force.power = 0.0f;
			
			force.time -= delta;
			if (force.time< 0)
				iterator.remove();
		}
		
		// forcesToApply.clear();
	}

}