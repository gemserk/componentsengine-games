package com.gemserk.games.zombierockers.gamestates;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.google.inject.Inject;

@SuppressWarnings("unchecked")
public class HighscoresTableEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(HighscoresTableEntityBuilder.class);

	@Inject
	ResourceManager resourceManager;

	@Inject
	SlickUtils slick;

	@SuppressWarnings("serial")
	@Override
	public void build() {

		tags("highscorestable");

		property("screenBounds", parameters.get("screenBounds"));
		property("scoreList", parameters.get("scoreList"));
		property("font", parameters.get("font"));

		ArrayList<Score> scoreList = Properties.getValue(entity, "scoreList");
		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

		final Rectangle labelBounds = slick.rectangle(-350, -50, 700, 100);

		final Resource<Font> font = Properties.getValue(entity, "font");

		int fontHeight = font.get().getHeight("A");

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("scoresLabel-name", new HashMap<String, Object>() {
			{
				put("font", font);
				put("position", slick.vector(screenBounds.getCenterX(), 150f));
				put("color", slick.color(0.1f, 0.6f, 0.1f, 1f));
				put("bounds", labelBounds);
				put("align", "left");
				put("valign", "center");
				put("layer", 10);
				put("message", "NAME");
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("scoresLabel-date", new HashMap<String, Object>() {
			{
				put("font", font);
				put("position", slick.vector(screenBounds.getCenterX() + 30, 150f));
				put("color", slick.color(0.1f, 0.6f, 0.1f, 1f));
				put("bounds", labelBounds);
				put("align", "center");
				put("valign", "center");
				put("layer", 10);
				put("message", "DATE");
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("scoresLabel-points", new HashMap<String, Object>() {
			{
				put("font", font);
				put("position", slick.vector(screenBounds.getCenterX(), 150f));
				put("color", slick.color(0.1f, 0.6f, 0.1f, 1f));
				put("bounds", labelBounds);
				put("align", "right");
				put("valign", "center");
				put("layer", 10);
				put("message", "POINTS");
			}
		}));

		for (int i = 0; i < scoreList.size(); i++) {

			final Score score = scoreList.get(i);

			final float yOffset = (fontHeight + 10) * (i + 1);

			child(templateProvider.getTemplate("gemserk.gui.label").instantiate("scoresLabel-name-" + i, new HashMap<String, Object>() {
				{
					put("font", font);
					put("position", slick.vector(screenBounds.getCenterX(), 150f + yOffset));
					put("color", slick.color(0.6f, 0.1f, 0.1f, 1f));
					put("bounds", labelBounds);
					put("align", "left");
					put("valign", "center");
					put("layer", 10);
					put("message", "" + score.getName());
				}
			}));

			child(templateProvider.getTemplate("gemserk.gui.label").instantiate("scoresLabel-date-" + i, new HashMap<String, Object>() {
				{
					put("font", font);
					put("position", slick.vector(screenBounds.getCenterX() + 30, 150f + yOffset));
					put("color", slick.color(0.6f, 0.1f, 0.1f, 1f));
					put("bounds", labelBounds);
					put("align", "center");
					put("valign", "center");
					put("layer", 10);
					put("message", dateFormat.format(new Date(score.getTimestamp())));
				}
			}));

			child(templateProvider.getTemplate("gemserk.gui.label").instantiate("scoresLabel-points-" + i, new HashMap<String, Object>() {
				{
					put("font", font);
					put("position", slick.vector(screenBounds.getCenterX(), 150f + yOffset));
					put("color", slick.color(0.6f, 0.1f, 0.1f, 1f));
					put("bounds", labelBounds);
					put("align", "right");
					put("valign", "center");
					put("layer", 10);
					put("message", "" + score.getPoints());
				}
			}));

		}

	}
}