package com.gemserk.games.zombierockers.gamestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.gemserk.componentsengine.properties.PropertiesMapBuilder;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.google.inject.Inject;

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

	@SuppressWarnings("serial")
	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("level", parameters.get("level"));
		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		property("future", null);
		property("refreshScoresTimer", null);
		property("refreshingScoresLabel", null);
		property("childPanel", null);
		property("highscoresTable", null);
		
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
				put("font", resourceManager.get("FontTitle2"));
				put("position", slick.vector(screenBounds.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "High Scores");
			}
		}));

		component(new ReferencePropertyComponent("enterStateHandler") {

			@EntityProperty
			Property<Entity> childPanel;
			
			@EntityProperty
			Property<Entity> highscoresTable;

			@EntityProperty
			Property<Map<String, Object>> level;

			@Handles
			public void enterNodeState(Message message) {

				childPanel.set(new Entity("childPanel"));

				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(childPanel.get(), entity));
				
				if (highscoresTable.get() != null)
					messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(highscoresTable.get()));

				final Set<String> tags = new HashSet<String>();

				tags.add((String) level.get().get("name"));

				messageQueue.enqueue(new Message("refreshScores", new PropertiesMapBuilder() {
					{
						property("timeOut", 8000);
						property("ascending", false);
						property("tags", tags);
						property("limit", 10);
					}
				}.build()));

			}

		});
		
		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "playAgainButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage2"));
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
				put("font", resourceManager.get("FontDialogMessage2"));
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

		component(new ReferencePropertyComponent("refreshScoresHandler") {

			@EntityProperty
			Property<Future> future;

			@EntityProperty
			Property<Timer> refreshScoresTimer;

			@Handles
			public void refreshScores(Message message) {

				final Boolean ascending = Properties.getValue(message, "ascending");
				final Set<String> tags = Properties.getValue(message, "tags");
				final Integer limit = Properties.getValue(message, "limit");

				ExecutorService executor = (ExecutorService) globalProperties.getProperties().get("executor");

				future.set(executor.submit(new Callable<Collection<Score>>() {
					@Override
					public Collection<Score> call() throws Exception {
						return scores.getOrderedByPoints(tags, limit, ascending);
					}
				}));

				Integer timeOut = Properties.getValue(message, "timeOut");

				refreshScoresTimer.set(new CountDownTimer(timeOut, true));

				messageQueue.enqueue(new Message("refreshScoresStarted"));
			}

		});

		component(new ReferencePropertyComponent("showMessageWhenRefreshScoresStarted") {

			@EntityProperty
			Property<Entity> refreshingScoresLabel;

			@EntityProperty
			Property<Entity> childPanel;

			@Handles
			public void refreshScoresStarted(Message message) {
				refreshingScoresLabel.set(templateProvider.getTemplate("gemserk.gui.label").instantiate("dialogMessageLabel", new HashMap<String, Object>() {
					{
						put("font", resourceManager.get("FontDialogMessage2"));
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
						put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
						put("bounds", slick.rectangle(-160, -25, 320, 50));
						put("align", "center");
						put("valign", "center");
						put("layer", 1);
						put("message", "Updating scores, please wait...");
					}
				}));
				messageQueue.enqueue(childrenManagementMessageFactory.addEntity(refreshingScoresLabel.get(), childPanel.get()));
			}

		});

		component(new ReferencePropertyComponent("checkScoresRefreshedHandler") {

			@EntityProperty
			Property<Future> future;

			@EntityProperty
			Property<Timer> refreshScoresTimer;

			@Handles
			public void update(Message message) {

				if (future.get() == null)
					return;

				Integer delta = Properties.getValue(message, "delta");

				boolean triggered = refreshScoresTimer.get().update(delta);

				if (future.get().isDone()) {

					try {
						messageQueue.enqueue(new Message("scoresRefreshed", new PropertiesMapBuilder() {
							{
								property("scores", future.get().get());
							}
						}.build()));

					} catch (Exception e) {
						logger.error("Failed to load highscores from server", e);
					}

				} else {

					if (!triggered)
						return;

					if (logger.isInfoEnabled())
						logger.info("Refresh scores timer expired!!, failed to load highscores from server");

					messageQueue.enqueue(new Message("scoresRefreshFailed", new PropertiesMapBuilder() {
						{
							property("reason", "Failed to load highscores from server");
						}
					}.build()));
				}

				future.set(null);
			}

		});

		component(new ReferencePropertyComponent("scoresRefreshFailedHandler") {

			@EntityProperty
			Property<Entity> childPanel;

			@Handles
			public void scoresRefreshFailed(final Message message) {

				Entity scoresFailedLabel = templateProvider.getTemplate("gemserk.gui.label").instantiate("dialogMessageLabel", new HashMap<String, Object>() {
					{
						put("font", resourceManager.get("FontDialogMessage2"));
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
						put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
						put("bounds", slick.rectangle(-160, -25, 320, 50));
						put("align", "center");
						put("valign", "center");
						put("layer", 1);
						put("message", Properties.getValue(message, "reason"));
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
			public void scoresRefreshed(final Message message) {
				final Collection<Score> scores = Properties.getValue(message, "scores");

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
							put("font", resourceManager.get("FontDialogMessage2"));
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