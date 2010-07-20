package com.gemserk.commons.collisions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QuadTreeNode implements QuadTree {

	QuadTreeNode parent;

	// TODO: hashmap using quadrant as key
	LinkedList<QuadTreeNode> children = new LinkedList<QuadTreeNode>();

	// TODO: change to be an AABB
	AABB aabb;

	LinkedList<Collidable> collidables = new LinkedList<Collidable>();
	
	public AABB getAabb() {
		return aabb;
	}

	public QuadTreeNode(AABB aabb, QuadTreeNode parent, int levels) {
		this.aabb = aabb;
		this.parent = parent;
		init(levels);
	}

	public QuadTreeNode(AABB rectangle, int levels) {
		this(rectangle, null, levels);
	}

	private void init(int levels) {

		if (levels == 0)
			return;

		float minX = aabb.getMinX();
		float minY = aabb.getMinY();

		float maxX = aabb.getMaxX();
		float maxY = aabb.getMaxY();

		float centerX = aabb.getCenterX();
		float centerY = aabb.getCenterY();

		AABB firstQuadrant = new AABB(centerX, minY, maxX, centerY);
		AABB secondQuadrant = new AABB(centerX, centerY, maxX, maxY);

		AABB thirdQuadrant = new AABB(minX, centerY, centerX, maxY);
		AABB fourthQuadrant = new AABB(minX, minY, centerX, centerY);

		children.add(new QuadTreeNode(firstQuadrant, this, levels - 1));
		children.add(new QuadTreeNode(secondQuadrant, this, levels - 1));
		children.add(new QuadTreeNode(thirdQuadrant, this, levels - 1));
		children.add(new QuadTreeNode(fourthQuadrant, this, levels - 1));
	}

	public void insert(Collidable collidable) {

		if (!canHold(collidable))
			return;

		QuadTreeNode destination = getDestinationFor(collidable);
		destination.collidables.add(collidable);
	}

	protected QuadTreeNode getDestinationFor(Collidable collidable) {

		for (QuadTreeNode child : children) {
			if (child.canHold(collidable))
				return child.getDestinationFor(collidable);
		}

		return this;
	}

	protected boolean canHold(Collidable collidable) {
		return aabb.contains(collidable.getAABB());
	}

	public List<Collidable> getCollidables(Collidable collidable) {

		ArrayList<Collidable> collidablesFound = new ArrayList<Collidable>();

		QuadTreeNode destination = getDestinationFor(collidable);

		if (!destination.canHold(collidable))
			return collidablesFound;

		// first, add children collidables
		collidablesFound.addAll(destination.getAllCollidables());

		while (destination != null) {
			collidablesFound.addAll(destination.collidables);
			destination = destination.parent;
		}

		return collidablesFound;
	}

	List<Collidable> getAllCollidables() {
		ArrayList<Collidable> allCollidables = new ArrayList<Collidable>();
		allCollidables.addAll(this.collidables);
		for (QuadTreeNode child : children)
			allCollidables.addAll(child.getAllCollidables());
		return allCollidables;
	}

	public void remove(Collidable collidable) {
		QuadTreeNode node = getDestinationFor(collidable);
		node.collidables.remove(collidable);
	}

	// TEMPORAL
	public List<QuadTreeNode> getLeafs() {
		List<QuadTreeNode> nodes = new ArrayList<QuadTreeNode>();
		if (children.isEmpty()) {
			nodes.add(this);
		} else {
			for (QuadTreeNode child : children) {
				nodes.addAll(child.getLeafs());
			}
		}
		return nodes;
	}

	@Override
	public void clear() {
		this.collidables.clear();
		for (QuadTreeNode child : children)
			child.clear();
	}
}