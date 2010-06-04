package com.gemserk.games.zombierockers;

import java.text.MessageFormat;

import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.components.Path;

public class PathTraversal implements Comparable<PathTraversal> {

	final Path path;
	final int index;
	final float innerDistance;

	Vector2f currentPosition = null;
	Vector2f currentTangent;
	
	Float distanceFromOrigin = null;

	public PathTraversal(Path path, int index, float innerDistance) {
		this.path = path;
		this.index = index;
		this.innerDistance = innerDistance;
	}

	public PathTraversal(Path path, int index) {
		this(path, index, 0);
	}

	protected PathTraversal(Path path, int index, float innerDistance,float distanceFromOrigin) {
		this(path, index, innerDistance);
		this.distanceFromOrigin = distanceFromOrigin;
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
			return new PathTraversal(path, index, innerDistance,0);

		if (distance < innerDistance)
			return new PathTraversal(path, index, innerDistance - distance, getDistanceFromOrigin() - distance);
		else {

			if (index == 0)
				return new PathTraversal(path, index, 0);

			Vector2f p0 = path.getPoint(index - 1);
			Vector2f p1 = path.getPoint(index);

			float segmentLength = p0.distance(p1);

			return new PathTraversal(path, index - 1, segmentLength,getDistanceFromOrigin()-innerDistance).backward(distance - innerDistance);
		}
	}

	private PathTraversal forward(float distance) {
		if (isOnLastPoint())
			return new PathTraversal(path, index, innerDistance,getDistanceFromOrigin());

		float segmentLength = getCurrentSegmentLength();

		if (segmentLength < innerDistance + distance) {
			float distanceLeftToAdvance = distance - getDistanceLeftInSegment();
			
			return new PathTraversal(path, index + 1, 0,this.getDistanceFromOrigin() + getDistanceLeftInSegment()).forward(distanceLeftToAdvance);
		} else
			return new PathTraversal(path, index, innerDistance + distance, this.getDistanceFromOrigin() + distance);
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
	
	protected float getDistanceLeftInSegment(){
		return getCurrentSegmentLength() - innerDistance;
	}

	protected float getCurrentSegmentLength() {
		if(isOnLastPoint())
			return 0f;
		
		Vector2f p0 = path.getPoint(index);
		Vector2f p1 = path.getPoint(index + 1);

		return p0.distance(p1);
	}

	protected PathTraversal forwardSegment(){
		if(isOnLastPoint())
			return this;

		return new PathTraversal(path,index+1,0,getDistanceFromOrigin()+getDistanceLeftInSegment());
	}
	
	@Override
	public int compareTo(PathTraversal pathTraversal) {
		if (pathTraversal.path != this.path)
			throw new RuntimeException("cannot compare, different paths");
		
		if (this.index == pathTraversal.index){
			if (this.innerDistance < pathTraversal.innerDistance)
				return -1;
			if (this.innerDistance > pathTraversal.innerDistance)
				return 1;
			return 0;
		} 
		
		return this.index - pathTraversal.index;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("PATHTRAVERSAL: index: {0}, innerDistance: {1}, position: {2}",index,innerDistance,getPosition());  
	}

	public float getDistanceFromOrigin() {
		if(distanceFromOrigin == null){
			PathTraversal pathTraversal = new PathTraversal(path,0,0,0);
			while(pathTraversal.index < this.index){
				pathTraversal = pathTraversal.forwardSegment();
			}
			pathTraversal = pathTraversal.forward(innerDistance);
			distanceFromOrigin = pathTraversal.getDistanceFromOrigin();
		}
			
		return distanceFromOrigin;
	}

}
