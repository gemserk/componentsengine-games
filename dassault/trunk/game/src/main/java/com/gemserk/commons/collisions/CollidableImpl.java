package com.gemserk.commons.collisions;

import java.text.MessageFormat;

import com.gemserk.componentsengine.entities.Entity;

public class CollidableImpl implements Collidable {

	AABB aabb;
	
	Entity entity;
	
	QuadTree quadTree;

	public CollidableImpl(Entity entity, AABB aabb) {
		this.aabb = aabb;
	}

	public AABB getAABB() {
		return aabb;
	}
	
	public void setQuadTree(QuadTree quadTree) {
		this.quadTree = quadTree;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("AABB[{0}|{1}|{2}|{3}]", aabb.getMinX(), aabb.getMinY(), // 
				aabb.getMaxX(), aabb.getMaxY());
	}

}