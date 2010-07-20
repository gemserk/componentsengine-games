package com.gemserk.commons.collisions;

import java.util.List;

public interface QuadTree {

	List<Collidable> getCollidables(Collidable collidable);

	void insert(Collidable collidable);

	void remove(Collidable collidable);
	
	void clear();

}