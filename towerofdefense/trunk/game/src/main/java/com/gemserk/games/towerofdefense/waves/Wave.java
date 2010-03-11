package com.gemserk.games.towerofdefense.waves;

import com.gemserk.games.towerofdefense.instantiationTemplates.InstantiationTemplate;

public interface Wave {

	InstantiationTemplate generateTemplates(int delta);

	void start();

	boolean isDone();

}