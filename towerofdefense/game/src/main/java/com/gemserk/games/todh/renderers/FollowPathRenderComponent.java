package com.gemserk.games.todh.renderers;

import static com.gemserk.componentsengine.properties.Properties.property;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.PropertyLocator;
import com.gemserk.games.todh.components.Component;

public class FollowPathRenderComponent extends Component {

	PropertyLocator<ArrayList<Vector2f>> pathProperty = property("followpath.path");

	PropertyLocator<Integer> currentTargetProperty = property("followpath.currentTarget");

	public FollowPathRenderComponent(String name) {
		super(name);
	}

	@Override
	public void render(Graphics g, Entity entity) {

		g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.3f));
		g.pushTransform();

		ArrayList<Vector2f> path = pathProperty.getValue(entity);

		if (path.size() == 0)
			return;

		Integer currentTargetIndex = currentTargetProperty.getValue(entity);

		for (int i = 0; i < path.size(); i++) {
			Vector2f source = path.get(i);
			int j = i + 1;

			if (j >= path.size())
				continue;

			// j = 0;
			Vector2f target = path.get(j);

			float lineWidth = g.getLineWidth();
			g.setLineWidth(3.0f);
			g.drawLine(source.x, source.y, target.x, target.y);
			g.setLineWidth(lineWidth);
		}

		Vector2f currentTarget = path.get(currentTargetIndex);

		g.setColor(Color.red);
		g.translate(currentTarget.x, currentTarget.y);
		g.fillOval(-2.5f, -2.5f, 5.0f, 5.0f);

		g.popTransform();
	}
}