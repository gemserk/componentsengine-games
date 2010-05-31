package com.gemserk.games.zombierockers;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.Path;

public class PathTraversal {

	final Path path;
	final int index;
	final float innerDistance;

	Vector2f currentPosition = null;
	Vector2f currentTangent;

	public PathTraversal(Path path, int index, float innerDistance) {
		super();
		this.path = path;
		this.index = index;
		this.innerDistance = innerDistance;
	}

	public PathTraversal(Path path, int index) {
		this(path, index, 0);
	}

	public PathTraversal add(float distance) {

		if (distance < 0f)
			return backward(-distance);

		if (distance > 0f)
			return forward(distance);

		return new PathTraversal(path, index, innerDistance);
	}

	private PathTraversal backward(float distance) {
		if (index == 0 && innerDistance == 0)
			return new PathTraversal(path, index, innerDistance);

		if (distance < innerDistance)
			return new PathTraversal(path, index, innerDistance - distance);
		else {

			if (index == 0)
				return new PathTraversal(path, index, 0);

			Vector2f p0 = path.getPoint(index - 1);
			Vector2f p1 = path.getPoint(index);

			float segmentLength = p0.distance(p1);

			return new PathTraversal(path, index - 1, segmentLength).backward(distance - innerDistance);
		}
	}

	private PathTraversal forward(float distance) {
		if (isOnLastPoint())
			return new PathTraversal(path, index, innerDistance);

		Vector2f p0 = path.getPoint(index);
		Vector2f p1 = path.getPoint(index + 1);

		float segmentLength = p0.distance(p1);

		if (segmentLength < innerDistance + distance)
			return new PathTraversal(path, index + 1, 0).forward(distance - (segmentLength - innerDistance));
		else
			return new PathTraversal(path, index, innerDistance + distance);
	}

	public Vector2f getPosition() {
		if (currentPosition == null) {
			if (isOnLastPoint())
				return path.getPoint(index);

			Vector2f p0 = path.getPoint(index);
			Vector2f p1 = path.getPoint(index + 1);

			Vector2f direction = p1.copy().sub(p0).normalise();

			currentPosition = p0.copy().add(direction.scale(innerDistance));
		}
		return currentPosition.copy();
	}

	private boolean isOnLastPoint() {
		return index == path.getPoints().size() - 1;
	}

	public Vector2f getTangent() {
		if(currentTangent == null){
			if(path.getPoints().size()==1)
				return new Vector2f();

			if(isOnLastPoint())
				return path.getPoint(index).copy().sub(path.getPoint(index-1)).normalise();
			else
				return path.getPoint(index +1 ).copy().sub(path.getPoint(index)).normalise();			
		}
		return currentTangent.copy();
	}

}
