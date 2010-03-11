package com.gemserk.games.towerofdefense.waves;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;

public interface Wave {

	InstantiationTemplate generateTemplates(int delta);

	void start();

	boolean isDone();

}