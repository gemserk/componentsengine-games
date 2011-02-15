package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.gemserk.componentsengine.commons.components.FutureCallableComponent;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class UploadScoreEntityBuilder extends EntityBuilder {

	@Inject
	GlobalProperties globalProperties;

	@Inject
	Scores scores;

	
	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@Override
	public void build() {

		property("name", parameters.get("name"));
		property("points", parameters.get("points"));
		property("levelName", parameters.get("levelName"));

		final String name = Properties.getValue(entity, "name");
		final Long points = Properties.getValue(entity, "points");
		final String levelName = Properties.getValue(entity, "levelName");

		ExecutorService executor = (ExecutorService) globalProperties.getProperties().get("executor");
		Future future = executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return scores.submit(new Score(name, points, Sets.newHashSet(levelName), new HashMap<String, Object>()));
			}
		});

		Timer timer = new CountDownTimer(10000, true);

		property("future", future);
		property("timer", timer);

		component(new FutureCallableComponent("futureDoneHandler"));

		component(new ReferencePropertyComponent("showScoresWhenScoreUploadedOrFailed") {

			@EntityProperty
			Property<String> levelName;

			@Handles
			public void futureDone(Message message) {
				messageQueue.enqueue(messageBuilder.newMessage("highscores").property("levelName", levelName.get()).get());
			}

			@Handles
			public void futureTimedOut(Message message) {
				messageQueue.enqueue(messageBuilder.newMessage("highscores").property("levelName", levelName.get()).get());
			}

		});

	}
}