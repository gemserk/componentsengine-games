package com.gemserk.commons.collisions;

import java.util.ArrayList;
import java.util.Collections;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.render.SlickCallableRenderObject;

public class QuadTreeRenderObject extends SlickCallableRenderObject {

	class Entry {
		
		int level;

		ArrayList<QuadTreeImpl> quadTrees = new ArrayList<QuadTreeImpl>();

		public Entry(int level) {
			this.level = level;
		}

	}

	private ArrayList<Entry> entries = new ArrayList<Entry>();

	private Color[] colors = { Color.red, Color.green, Color.blue, Color.yellow, Color.magenta, Color.cyan, Color.orange, Color.pink };
	
	private Float[] widths = { 8f, 6f, 4f, 2f, 1f, 1f, 1f, 1f, 1f };

	// TODO: color and widths as parameters.
	
	public QuadTreeRenderObject(int layer, QuadTreeImpl quadTree) {
		super(layer);
		// generate levels...
		generateLevels(quadTree, 0);
		
		Collections.reverse(entries);
		
	}

	private void generateLevels(QuadTreeImpl quadTree, int i) {

		Entry entry = getEntry(i);

		entry.quadTrees.add(quadTree);

		for (QuadTreeImpl child : quadTree.getChildren()) {
			generateLevels(child, i + 1);
		}

	}

	private Entry getEntry(int i) {
		if (i >= entries.size())
			entries.add(new Entry(i));
		return entries.get(i);
	}

	@Override
	public void execute(Graphics g) {

		for (Entry entry : entries) {

			Color color = colors[entry.level];
			
//			System.out.println("entry level: " + entry.level + ", color: " + color);

			for (QuadTreeImpl quadTree : entry.quadTrees) {

				Color treeColor = Color.white;
				
				float width = 1f;

				if (!quadTree.collidables.isEmpty()) {
					treeColor = color;
					width = widths[entry.level];
				}

				AABB aabb = quadTree.getAabb();

				float previousLineWidth = g.getLineWidth();
				
				g.setColor(treeColor);
				g.pushTransform();
				g.setLineWidth(width);
				g.draw(new Rectangle(aabb.getMinX(), aabb.getMinY(), aabb.getWidth(), aabb.getHeight()));
				g.popTransform();
				
				g.setLineWidth(previousLineWidth);

			}

		}

	}

}
