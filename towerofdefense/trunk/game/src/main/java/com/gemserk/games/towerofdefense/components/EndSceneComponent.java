package com.gemserk.games.towerofdefense.components;

import java.util.Collection;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.entities.*;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.games.towerofdefense.timers.Timer;
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
	
	public EndSceneComponent(String id) {
		super(id);
	}
	
	public void handleMessage(UpdateMessage updateMessage) {
		if (!waves.isLastWaveStarted())
			return;

		if (!waves.allWavesFinished())
			return;

		Collection<Entity> critters = rootEntity.getEntities(EntityPredicates.withAllTags(tags));
		
		if (critters.size() > 0)
			return;
		
		if (!timer.isRunning())
			timer.reset();
	}

}