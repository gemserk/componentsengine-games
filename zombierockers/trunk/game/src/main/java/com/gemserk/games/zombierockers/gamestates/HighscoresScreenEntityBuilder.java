package com.gemserk.games.zombierockers.gamestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.FutureCallableComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings( { "unchecked", "unused" })
public class HighscoresScreenEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(HighscoresScreenEntityBuilder.class);

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Inject
	ResourceManager resourceManager;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	Scores scores;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@SuppressWarnings("serial")
	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("level", parameters.get("level"));
		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		property("refreshingScoresLabel", null);
		property("highscoresTable", null);

		property("ascending", parameters.get("ascending"), false);
		property("tags", parameters.get("tags"));
		property("limit", parameters.get("limit"), 10);
		property("timeout", parameters.get("timeout"), 10000);

		final Boolean ascending = Properties.getValue(entity, "ascending");
		final Set<String> tags = Properties.getValue(entity, "tags");
		final Integer limit = Properties.getValue(entity, "limit");

		ExecutorService executor = (ExecutorService) globalProperties.getProperties().get("executor");

		Future future = executor.submit(new Callable<Collection<Score>>() {
			@Override
			public Collection<Score> call() throws Exception {
				return scores.getOrderedByPoints(tags, limit, ascending);
			}
		});

		Integer timeOut = Properties.getValue(entity, "timeout");

		Timer refreshScoresTimer = new CountDownTimer(timeOut, true);
		
		property("future", future);
		property("refreshScoresTimer", refreshScoresTimer);

		Entity childPanel = javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {

				child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_updatingScoresLabel", new HashMap<String, Object>() {
					{
						put("font", resourceManager.get("FontDialogMessage"));
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
						put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
						put("bounds", slick.rectangle(-160, -25, 320, 50));
						put("align", "center");
						put("valign", "center");
						put("layer", 1);
						put("message", "Updating scores, please wait...");
					}
				}));

			}

		}).instantiate(entity.getId() + "_childPanel");

		property("childPanel", childPanel);
		child(childPanel);

		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate("titleLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontTitle"));
				put("position", slick.vector(screenBounds.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "High Scores");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "playAgainButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX() - 150, screenBounds.getMaxY() - 60f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Play again");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));

				put("buttonReleasedMessageId", "playAgainPressed");
			}
		}));

		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "nextLevelButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX() + 150, screenBounds.getMaxY() - 60f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Next level");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));

				put("buttonReleasedMessageId", "nextLevelPressed");
			}
		}));

		component(new ReferencePropertyComponent("guiHandler") {

			@Handles
			public void playAgainPressed(Message message) {
				messageQueue.enqueue(new Message("resume"));
				messageQueue.enqueueDelay(new Message("restartLevel"));
			}

			@Handles
			public void nextLevelPressed(Message message) {
				messageQueue.enqueue(new Message("resume"));
				messageQueue.enqueueDelay(new Message("nextLevel"));
			}

		});

		component(new FutureCallableComponent("checkScoresRefreshedHandler")).withProperties(new ComponentProperties() {
			{
				propertyRef("future", "future");
				propertyRef("timer", "refreshScoresTimer");
			}
		});

		component(new ReferencePropertyComponent("scoresRefreshFailedHandler") {

			@EntityProperty
			Property<Entity> childPanel;

			@Handles
			public void futureTimedOut(final Message message) {
				Entity scoresFailedLabel = templateProvider.getTemplate("gemserk.gui.label").instantiate("dialogMessageLabel", new HashMap<String, Object>() {
					{
						put("font", resourceManager.get("FontDialogMessage"));
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
						put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
						put("bounds", slick.rectangle(-160, -25, 320, 50));
						put("align", "center");
						put("valign", "center");
						put("layer", 1);
						put("message", "Failed to retrieve highscores from server, try again later");
					}
				});
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(scoresFailedLabel, childPanel.get()));
			}

		});

		component(new ReferencePropertyComponent("scoresRefreshedHandler") {

			@EntityProperty
			Property<Entity> childPanel;

			@EntityProperty
			Property<Entity> highscoresTable;

			@Handles
			public void futureDone(final Message message) {
				final Collection<Score> scores = Properties.getValue(message, "data");

				if (logger.isInfoEnabled())
					logger.info("Scores refreshed");

				if (scores.size() > 0) {

					highscoresTable.set(templateProvider.getTemplate("zombierockers.gui.highscorestable").instantiate("highscoresTable", new HashMap<String, Object>() {
						{
							put("scoreList", new ArrayList<Score>(scores));
							put("screenBounds", new ReferenceProperty("screenBounds", entity));
							put("font", resourceManager.get("FontScores"));
						}
					}));

					messageQueue.enqueue(childrenManagementMessageFactory.addEntity(highscoresTable.get(), entity));
					messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(childPanel.get()));

					childPanel.set(null);
				} else {

					Entity scoresFailedLabel = templateProvider.getTemplate("gemserk.gui.label").instantiate("dialogMessageLabel", new HashMap<String, Object>() {
						{
							put("font", resourceManager.get("FontDialogMessage"));
							put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
							put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
							put("bounds", slick.rectangle(-160, -25, 320, 50));
							put("align", "center");
							put("valign", "center");
							put("layer", 1);
							put("message", "No scores available for this level.");
						}
					});

					messageQueue.enqueue(childrenManagementMessageFactory.addEntity(scoresFailedLabel, childPanel.get()));

				}
			}

		});

	}
}