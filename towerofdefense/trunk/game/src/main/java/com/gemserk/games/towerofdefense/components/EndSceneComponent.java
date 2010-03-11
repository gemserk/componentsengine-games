package com.gemserk.games.towerofdefense.components;

import java.util.*;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.entities.*;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.games.towerofdefense.waves.Waves;
import com.google.inject.Inject;

public class EndSceneComponent extends FieldsReflectionComponent {

	@Inject
	@Root
	Entity rootEntity;
	
	@EntityProperty(readOnly=true)
	Waves waves;

	@EntityProperty(readOnly=true)
	Timer timer;
	
	@EntityProperty(readOnly=true)
	String[] tags;
	
	@EntityProperty(readOnly=true)
	Integer lives;
	
	@EntityProperty
	Boolean endSceneEnabled;
	
	@EntityProperty
	String message;
	
	public EndSceneComponent(String id) {
		super(id);
	}
	
	final static String[] winMessages = {"Good one!!!", "Great!!!", "You rock!!!", "Awesome!!!"};
	final static String[] loseMessages = {"Fail!!!", "Try again!!!", "Looser!!!"};
	
	final static Random random = new Random();
	
	public void handleMessage(UpdateMessage updateMessage) {
		
		if (endSceneEnabled)
			return;
		
		if (lives == 0)
		{
			endSceneEnabled = true;
			timer.reset();
			message = loseMessages[random.nextInt(loseMessages.length)];
			return;
		}
		
		if (!waves.isLastWaveStarted())
			return;

		if (!waves.allWavesFinished())
			return;


		Collection<Entity> critters = rootEntity.getEntities(EntityPredicates.withAllTags(tags));
		
		if (critters.size() > 0)
			return;
		
		endSceneEnabled = true;
		timer.reset();
		message = winMessages[random.nextInt(winMessages.length)];
	}

}