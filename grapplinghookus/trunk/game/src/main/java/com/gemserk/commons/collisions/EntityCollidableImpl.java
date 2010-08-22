package com.gemserk.commons.collisions;

import com.gemserk.componentsengine.entities.Entity;
public class EntityCollidableImpl extends CollidableImpl {

	Entity entity;
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public EntityCollidableImpl(Entity entity, AABB aabb) {
		super(aabb);
		this.entity = entity;
	}
	
}